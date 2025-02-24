package com.example.socialnetwork.repository.database;

import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PrietenieDBRepo implements Repository<Long, Prietenie> {

    private final String url;
    private final String username;
    private final String password;
    Connection connection;
    public PrietenieDBRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Optional<Prietenie> findOne(Long longLongTuple) {
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM prieteni WHERE id_prietenie=?");
            statement.setLong(1,longLongTuple);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Long id = resultSet.getLong("id_prietenie");
            Long id1=resultSet.getLong("id_prieten1");
            Long id2=resultSet.getLong("id_prieten2");
            LocalDateTime friends_from=resultSet.getTimestamp("friends_from").toLocalDateTime();
            String status=resultSet.getString("status");
            Prietenie prietenie=new Prietenie(id1,id2,status);
            prietenie.setId(id);
            return Optional.of(prietenie);

        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public Optional<Prietenie> findOnep( Long id1, Long id2) {
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM prieteni WHERE (id_prieten1=? and id_prieten2=?) or (id_prieten1=? and id_prieten2=?) ");
            statement.setLong(1,id1);
            statement.setLong(2,id2);
            statement.setLong(3,id2);
            statement.setLong(4,id1);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Long id = resultSet.getLong("id_prietenie");
            Long Id1=resultSet.getLong("id_prieten1");
            Long Id2=resultSet.getLong("id_prieten2");
            LocalDateTime friends_from=resultSet.getTimestamp("friends_from").toLocalDateTime();
            String state=resultSet.getString("status");
            Prietenie prietenie=new Prietenie(Id1,Id2,state);
            prietenie.setId(id);
            return Optional.of(prietenie);

        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> prietenii=new HashSet<>();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM prieteni");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long pid=resultSet.getLong("id_prietenie");
                Long id1=resultSet.getLong("id_prieten1");
                Long id2=resultSet.getLong("id_prieten2");
                LocalDateTime friends_from=resultSet.getTimestamp("friends_from").toLocalDateTime();
                String status=resultSet.getString("status");
                Prietenie prietenie=new Prietenie(id1,id2,status);
                prietenie.setId(pid);
                prietenii.add(prietenie);
            }

        } catch (SQLException e) {
            return null;
        }
        return prietenii;
    }

    public Iterable<Prietenie> findAllp(Long idp) {
        Set<Prietenie> prietenii=new HashSet<>();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM prieteni where id_prieten1=? or id_prieten2=?");
            statement.setLong(1,idp);
            statement.setLong(2,idp);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long pid=resultSet.getLong("id_prietenie");
                Long id1=resultSet.getLong("id_prieten1");
                Long id2=resultSet.getLong("id_prieten2");
                LocalDateTime friends_from=resultSet.getTimestamp("friends_from").toLocalDateTime();
                String status=resultSet.getString("status");
                Prietenie prietenie=new Prietenie(id1,id2,status);
                prietenie.setId(pid);
                prietenii.add(prietenie);
            }

        } catch (SQLException e) {
            return null;
        }
        return prietenii;
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        int result=-1;
        try(
                PreparedStatement statement=connection.prepareStatement("INSERT INTO prieteni (id_prieten1,id_prieten2,friends_from,status) VALUES (?,?,?,?)")){
            statement.setLong(1,entity.getIdUser1());
            statement.setLong(2,entity.getIdUser2());
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(4, entity.getStatus());
            result=statement.executeUpdate();
        }catch (SQLException e){
            return Optional.ofNullable(entity);
        }
        if(result>0)
            return Optional.empty();
        else
            return Optional.of(entity);

    }

    @Override
    public Optional<Prietenie> delete( Long longLongTuple) {
        try(
                PreparedStatement statement=connection.prepareStatement("DELETE FROM prieteni WHERE id_prietenie=?")){
            Prietenie p=findOne(longLongTuple).orElse(null);
            if(p==null){ return Optional.empty(); }
            statement.setLong(1,longLongTuple);
            statement.executeUpdate();
            return Optional.of(p);
        } catch (SQLException e) {
            return Optional.empty();

        }
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        try(
                PreparedStatement statement=connection.prepareStatement("UPDATE prieteni SET id_prieten1=? ,id_prieten2=?,friends_from=? ,status=? WHERE id_prietenie=?")){
            statement.setLong(1,entity.getIdUser1());
            statement.setLong(2,entity.getIdUser2());
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(4,entity.getStatus());
            statement.setLong(5,entity.getId());
            statement.executeUpdate();
            return Optional.empty();

        }catch (SQLException e){
            return Optional.ofNullable(entity);
        }
    }
}
