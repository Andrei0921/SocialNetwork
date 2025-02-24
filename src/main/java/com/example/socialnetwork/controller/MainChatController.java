package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.ReplyMessage;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.Service;
import com.example.socialnetwork.utils.events.Event;
import com.example.socialnetwork.utils.events.MessageEvent;
import com.example.socialnetwork.utils.observers.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MainChatController implements Observer<Event> {

    @FXML
    protected TableView<Utilizator> friendTable;
    @FXML
    private AnchorPane chatPane;    @FXML
    private TableColumn<Utilizator, String> friendUsernameColumn;
    private ObservableList<Utilizator> friendList= FXCollections.observableArrayList();
    Utilizator current;
    Service service;

    @FXML
    private ListView<Message> chat;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    private Utilizator currentUser;
    private Message selectedReplyMessage = null;
    private Utilizator chatUser;
    private final ObservableList<Message> messages= FXCollections.observableArrayList();


    private void chatHistory() {

        List<Message> chatHistory =new ArrayList<>( service.getMessagesBetween(currentUser, chatUser));


        for (Message message : chatHistory) {
            messages.add(message);
        }
        messages.sort(Comparator.comparing(Message::getData));
        chat.setItems(messages);
    }

    private String formatMessage(Message message) {
        String sender = message.getFrom().equals(currentUser) ? "Tu" : message.getFrom().getFirstName();
        String baseMessage = sender + ": " + message.getMessage();

        if (message instanceof ReplyMessage reply) {
            return reply.getReplied_m().getMessage() + "\n---\n" + baseMessage;
        }

        return baseMessage;
    }

    @FXML
    private void handleSendMessage() {
        String messageText = messageField.getText().trim();
        if (!messageText.isEmpty()) {
            Message newMessage;
            if (selectedReplyMessage != null){
                newMessage = service.sendMessage(currentUser, List.of(chatUser), messageText, selectedReplyMessage);}
            else {
                newMessage = service.sendMessage(currentUser, List.of(chatUser), messageText, null);
            }

            if (newMessage != null) {
                messages.add(newMessage);
            }
            selectedReplyMessage = null;
            messageField.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Mesajul nu poate fi gol!", ButtonType.OK);
            alert.showAndWait();
        }

    }

    @FXML
    public void handleMessageSelected(MouseEvent event) {
        Message selectedMessage = chat.getSelectionModel().getSelectedItem();
        if (selectedMessage != null && event.getClickCount() == 2) {
            selectedReplyMessage = selectedMessage;
        }
    }

    @Override
    public void update(Event event) {
        if(event instanceof MessageEvent){
            MessageEvent messageEvent = (MessageEvent) event;
            Message newMessage = messageEvent.getData();
            if ((newMessage.getFrom().equals(chatUser) && newMessage.getTo().contains(currentUser)) ||
                    (newMessage.getFrom().equals(currentUser) && newMessage.getTo().contains(chatUser))) {
                if(!newMessage.getFrom().equals(currentUser)){
                    messages.add(newMessage);
                }
            }
        }
    }
    public void initialize(Utilizator user) {

        this.current = user;
        friendUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        friendTable.setItems(friendList);
        friendTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Utilizator selectedFriend = friendTable.getSelectionModel().getSelectedItem();
                if (selectedFriend != null) {
                    openChatWindow(user,selectedFriend);
                }
            }
        });


    }
    public void setController(Service service) {
        this.service = service;
        this.service.addObserver(this);
        initModel();
    }
    private void initModel() {

        Iterable<Prietenie> pIterable = service.getPrietenieu(current.getId());

        List<Utilizator> utilizatorList = StreamSupport.stream(pIterable.spliterator(), false)
                .map(p -> {
                    Long id;
                    if (p.getIdUser1().equals(current.getId())) {
                        id = p.getIdUser2();
                    } else {
                        id = p.getIdUser1();
                    }
                    return service.findUtilizator(id);
                }).collect(Collectors.toList());

        friendList.setAll(utilizatorList);
    }

    private void openChatWindow(Utilizator me,Utilizator friend) {
        this.currentUser=me;
        this.chatUser = friend;
        chatPane.setVisible(true);
        chat.setCellFactory(listView -> new MessageCell(me));
        chat.setItems(messages);
        chat.setOnMouseClicked(this::handleMessageSelected);
        chatHistory();

    }

}
