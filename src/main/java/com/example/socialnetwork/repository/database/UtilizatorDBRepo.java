package com.example.socialnetwork.repository.database;

import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.domain.validators.Validator;
import com.example.socialnetwork.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UtilizatorDBRepo implements Repository<Long, Utilizator> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Utilizator> validator;
    Connection connection;
    public UtilizatorDBRepo(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> findOne(Long id) {
        try{
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM utilizatori WHERE id=?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet=preparedStatement.executeQuery();
            resultSet.next();

            Long utilizatorId = resultSet.getLong("id");
            String uFirstName = resultSet.getString("first_name");
            String uLastName = resultSet.getString("last_name");
            String uEmail = resultSet.getString("email");
            String uPassword = resultSet.getString("password");
            String uName = resultSet.getString("username");
            Utilizator utilizator=new Utilizator(uFirstName,uLastName,uPassword,uEmail,uName);
            utilizator.setId(utilizatorId);
            validator.validate(utilizator);
            return Optional.of(utilizator);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> utilizatori=new HashSet<>();
        try {
            PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM utilizatori");
            ResultSet resultSet=preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long utilizatorId = resultSet.getLong("id");
                String uFirstName = resultSet.getString("first_name");
                String uLastName = resultSet.getString("last_name");
                String uEmail = resultSet.getString("email");
                String uPassword = resultSet.getString("password");
                String uName = resultSet.getString("username");
                Utilizator utilizator=new Utilizator(uFirstName,uLastName,uPassword,uEmail,uName);
                utilizator.setId(utilizatorId);
                validator.validate(utilizator);
                utilizatori.add(utilizator);
            }
            return utilizatori;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilizatori;
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        validator.validate(entity);
        int result=-1;
        try (
                PreparedStatement preparedStatement= connection.prepareStatement("INSERT INTO utilizatori (first_name,last_name,email,password,username) VALUES (?,?,?,?,?)"))
        {
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setString(3, entity.getEmail());
            preparedStatement.setString(4, entity.getPassword());
            preparedStatement.setString(5, entity.getUsername());
            result=preparedStatement.executeUpdate();

        } catch (SQLException e) {
            return Optional.ofNullable(entity);
        }
        if(result>0)
            return Optional.empty();
        else
            return Optional.of(entity);

    }

    @Override
    public Optional<Utilizator> delete(Long id) {
        try(
                PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM utilizatori WHERE id=?")){
            Utilizator u=findOne(id).orElse(null);
            if(u==null)return Optional.empty();
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            return Optional.of(u);
        }catch (SQLException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        try(
                PreparedStatement statement= connection.prepareStatement("UPDATE utilizatori SET first_name=?, last_name=?, email=?,password=?,username=? WHERE id=? ")){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());
            statement.setString(5, entity.getUsername());
            statement.setLong(6, entity.getId());
            statement.executeUpdate();
            return Optional.empty();

        } catch (SQLException e) {
            return Optional.ofNullable(entity);
        }
    }
}
