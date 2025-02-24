package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.exceptions.ServiceException;
import com.example.socialnetwork.service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfilController {

    Service service;
    @FXML
    Button dltbtn;
    @FXML
    Label labelEmail;
    @FXML
    Label labelF;
    @FXML
    Label labelL;
    @FXML
    Label labelU;
    Utilizator current;

    public void initialize(Utilizator utilizator) {
        this.current=utilizator;
    }

    public void setController(Service service) {
        this.service = service;
        initModel();
    }
    public void initModel() {
        labelEmail.setText("Email: " +current.getEmail());
        labelF.setText("First Name: " +current.getFirstName());
        labelL.setText("Last Name: " +current.getLastName());
        labelU.setText("Username: " +current.getUsername());
    }

    public void handleDelete(ActionEvent actionEvent) {
        try {
            service.removeUtilizator(current.getId());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetwork/views/login_view.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Log In");
            stage.setScene(scene);
            LoginController controller = loader.getController();
            controller.setLoginController(service, stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
