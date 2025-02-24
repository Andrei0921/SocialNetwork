package com.example.socialnetwork.repository.database;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.ReplyMessage;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDBRepo implements Repository<Long, Message> {

    private final String url;
    private final String username;
    private final String password;
    private Repository<Long, Utilizator>repo_u;
    Connection connection;
    public MessageDBRepo(String url, String username, String password, Repository<Long, Utilizator> repo_u) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.repo_u=repo_u;
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Message> findOne(Long aLong) {

        try (
             PreparedStatement statementMessage = connection.prepareStatement("SELECT * FROM mesaje" + " WHERE id = ?");
        ) {
            statementMessage.setInt(1, Math.toIntExact(aLong));
            ResultSet resultSet = statementMessage.executeQuery();
            if (resultSet.next()) {
                Long senderId = resultSet.getLong("sender_id");
                String messageText = resultSet.getString("message_text");
                LocalDateTime sentAt = resultSet.getTimestamp("sent_at").toLocalDateTime();
                String type = resultSet.getString("message_type");
                Long repliedMessageId = resultSet.getLong("replied_messageid");

                Message message;
                Utilizator sender = null;
                Optional<Utilizator> senderOptional = getUserById(senderId);
                if (senderOptional.isPresent())
                    sender = senderOptional.get();
                List<Utilizator> receivers = getReceiversForMessage(aLong);

                if ("ReplyMessage".equals(type)) {
                    Optional<Message> repliedMessageOptional = findOne(repliedMessageId);
                    if (repliedMessageOptional.isPresent()) {
                        Message repliedMessage = repliedMessageOptional.get();
                        message = new ReplyMessage(sender, receivers, messageText, sentAt, repliedMessage);
                        ((ReplyMessage) message).setRepliedMessageId(resultSet.getLong("replied_messageid"));
                    } else {
                        message = new Message(sender, receivers, messageText, sentAt);
                    }
                } else {
                    message = new Message(sender, receivers, messageText, sentAt);
                }
                message.setId(aLong);

                return Optional.of(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();
        try (
             PreparedStatement statementMessage = connection.prepareStatement("SELECT * FROM mesaje");
             ResultSet resultSet = statementMessage.executeQuery();
        ) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long senderId = resultSet.getLong("sender_id");
                String messageText = resultSet.getString("message_text");
                LocalDateTime sentAt = resultSet.getTimestamp("sent_at").toLocalDateTime();
                String type = resultSet.getString("message_type");
                Long repliedMessageId = resultSet.getLong("replied_messageid");

                Message message;
                Utilizator sender = null;
                Optional<Utilizator> senderOptional = getUserById(senderId);
                if (senderOptional.isPresent())
                    sender = senderOptional.get();
                List<Utilizator> receivers = getReceiversForMessage(id);

                if ("ReplyMessage".equals(type)) {
                    Optional<Message> repliedMessageOptional = findOne(repliedMessageId);
                    if (repliedMessageOptional.isPresent()) {
                        Message repliedMessage = repliedMessageOptional.get();
                        message = new ReplyMessage(sender, receivers, messageText, sentAt, repliedMessage);
                        ((ReplyMessage) message).setRepliedMessageId(resultSet.getLong("replied_messageid"));
                    } else {
                        message = new Message(sender, receivers, messageText, sentAt);
                    }
                } else {
                    message = new Message(sender, receivers, messageText, sentAt);
                }
                message.setId(id);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> save(Message entity) {

        try (
             PreparedStatement statementMessage = connection.prepareStatement("INSERT INTO mesaje (sender_id, message_text, sent_at, message_type, replied_messageid) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement statementMessageReceivers = connection.prepareStatement("INSERT INTO mesaje_destinatari (message_id, receiver_id) VALUES (?, ?)");
        ) {
            statementMessage.setLong(1, entity.getFrom().getId());
            statementMessage.setString(2, entity.getMessage());
            statementMessage.setTimestamp(3, Timestamp.valueOf(entity.getData()));
            statementMessage.setString(4, entity instanceof ReplyMessage ? "ReplyMessage" : "Message");
            if (entity instanceof ReplyMessage) {
                statementMessage.setLong(5, ((ReplyMessage) entity).getReplied_m().getId());

            } else {
                statementMessage.setNull(5, Types.BIGINT);
            }

            int result = statementMessage.executeUpdate();
            if (result == 0)
                return Optional.empty();

            try (ResultSet generatedKeys = statementMessage.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long generatedId = generatedKeys.getLong(1);
                    entity.setId(generatedId);
                    for (Utilizator user: entity.getTo()) {
                        statementMessageReceivers.setLong(1, entity.getId());
                        statementMessageReceivers.setLong(2, user.getId());
                        int resultReceivers = statementMessageReceivers.executeUpdate();
                        if (resultReceivers == 0)
                            return Optional.empty();
                    }
                    return Optional.of(entity);
                } else
                    throw new SQLException("Failed to retrieve the generated ID!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(Long messageId) {

        try (
             PreparedStatement statementDeleteMessage = connection.prepareStatement("DELETE FROM mesaje WHERE id = ?");
        ) {
            Optional<Message> deletedMessage = findOne(messageId);
            statementDeleteMessage.setLong(1, messageId);
            int resultDeleteMessage = statementDeleteMessage.executeUpdate();
            return resultDeleteMessage == 1 ? deletedMessage: Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    private List<Utilizator> getReceiversForMessage(Long messageId) throws SQLException {
        List<Utilizator> receivers = new ArrayList<>();
        try (
             PreparedStatement statementReceivers = connection.prepareStatement("SELECT * FROM mesaje_destinatari WHERE message_id = ?");

        ) {
            statementReceivers.setLong(1, messageId);
            ResultSet resultSet = statementReceivers.executeQuery();

            while (resultSet.next()) {
                Long receiverId = resultSet.getLong("receiver_id");
                Optional<Utilizator> receiver = getUserById(receiverId);
                receiver.ifPresent(receivers::add);
            }
        }
        return receivers;
    }

    private Optional<Utilizator> getUserById(Long utilizatorID) {

        try (
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM utilizatori WHERE id = ?");
        ) {
            statement.setLong(1, utilizatorID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String prenume = resultSet.getString("first_name");
                String nume = resultSet.getString("last_name");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                Utilizator utilizator = new Utilizator(prenume, nume, password, email, username);
                utilizator.setId(utilizatorID);
                return Optional.of(utilizator);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}
