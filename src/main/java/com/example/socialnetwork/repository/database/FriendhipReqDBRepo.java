package com.example.socialnetwork.repository.database;

import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.FriendshipStatus;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendhipReqDBRepo  {
    private final String url;
    private final String username;
    private final String password;
    Connection connection;
    public FriendhipReqDBRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Optional<FriendRequest> findOne(Long aLong,Long aLong1) {
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests WHERE from_id=? AND to_id=?");
            statement.setLong(1,aLong);
            statement.setLong(2,aLong1);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Long id = resultSet.getLong("id");
            Long id1=resultSet.getLong("from_id");
            Long id2=resultSet.getLong("to_id");
            LocalDateTime friends_from=resultSet.getTimestamp("request_from").toLocalDateTime();
            FriendshipStatus status= FriendshipStatus.valueOf(resultSet.getString("status"));
            FriendRequest req=new FriendRequest(id1,id2,status,friends_from);
            req.setId(id);
            return Optional.of(req);

        } catch (SQLException e) {
            return Optional.empty();
        }
    }


    public Optional<FriendRequest> findOnepending(Long aLong,Long aLong1,FriendshipStatus stat) {
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests WHERE from_id=? AND to_id=? AND status=?");
            statement.setLong(1,aLong);
            statement.setLong(2,aLong1);
            statement.setString(3,stat.toString());

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Long id = resultSet.getLong("id");
            Long id1=resultSet.getLong("from_id");
            Long id2=resultSet.getLong("to_id");
            LocalDateTime friends_from=resultSet.getTimestamp("request_from").toLocalDateTime();
            FriendshipStatus status= FriendshipStatus.valueOf(resultSet.getString("status"));
            FriendRequest req=new FriendRequest(id1,id2,status,friends_from);
            req.setId(id);
            return Optional.of(req);

        } catch (SQLException e) {
            return Optional.empty();
        }
    }



    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> prietenii=new HashSet<>();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                Long id1=resultSet.getLong("from_id");
                Long id2=resultSet.getLong("to_id");
                LocalDateTime friends_from=resultSet.getTimestamp("request_from").toLocalDateTime();
                FriendshipStatus status= FriendshipStatus.valueOf(resultSet.getString("status"));
                FriendRequest req=new FriendRequest(id1,id2,status,friends_from);
                req.setId(id);
                prietenii.add(req);
            }

        } catch (SQLException e) {
            return null;
        }
        return prietenii;
    }

    public long findAllpending(long userID) {
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM requests WHERE to_id = ? AND status = 'PENDING' ");
            statement.setLong(1,userID);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public Optional<FriendRequest> save(FriendRequest entity) {
        int result=-1;
        try(
                PreparedStatement statement=connection.prepareStatement("INSERT INTO requests (from_id,to_id,status,request_from) VALUES (?,?,?,?)")){
            statement.setLong(1,entity.getFromUserId());
            statement.setLong(2,entity.getToUserId());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(3, entity.getStatus().toString());
            result=statement.executeUpdate();
            if (result == 1) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
        }catch (SQLException e){
            return Optional.ofNullable(entity);
        }

    }

    public Optional<FriendRequest> delete(Long aLong,Long along) {
        try(
                PreparedStatement statement=connection.prepareStatement("DELETE FROM requests WHERE (from_id=? and to_id=?) or (from_id=? and to_id=?)")){
            statement.setLong(1,aLong);
            statement.setLong(2,along);
            statement.setLong(3,along);
            statement.setLong(4,aLong);
            statement.executeUpdate();
            return Optional.of(new FriendRequest(0L, 0L, FriendshipStatus.REJECTED, LocalDateTime.now()));
        } catch (SQLException e) {
            return Optional.empty();

        }
    }


    public Optional<FriendRequest> update(FriendRequest entity) {
        try(
                PreparedStatement statement=connection.prepareStatement("UPDATE requests SET status=? WHERE from_id=? and to_id=?")){
            statement.setString(1,entity.getStatus().toString());
            statement.setLong(2,entity.getFromUserId());
            statement.setLong(3,entity.getToUserId());
            statement.executeUpdate();
            return Optional.empty();

        }catch (SQLException e){
            return Optional.ofNullable(entity);
        }

    }
}
