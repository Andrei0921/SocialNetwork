package com.example.socialnetwork.controller;

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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {
    Service service ;
    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private Button btnsignup;
    @FXML
    private Button btncancel;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;

    Stage stage;

    public void setController(Service service,Stage stage) {
        this.service = service;
        this.stage = stage;
    }

    public void clearfields(){
        txtUsername.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
        txtFirstName.clear();
        txtLastName.clear();
    }

    public void handleSignUp(ActionEvent actionEvent) {
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();

        if(!password.equals(confirmPassword)) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Passwords do not match");
            alert.showAndWait();
            return;
        }
        try {
            service.addUser(firstName, lastName, email, password, username);
            clearfields();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetwork/views/login_view.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Log In");
            stage.setScene(scene);
            LoginController controller = loader.getController();
            controller.setLoginController(service, stage);
        }catch (ServiceException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void handleCancel(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetwork/views/login_view.fxml"));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Log In");
        stage.setScene(scene);
        LoginController controller = loader.getController();
        controller.setLoginController(service,stage);
    }
}
