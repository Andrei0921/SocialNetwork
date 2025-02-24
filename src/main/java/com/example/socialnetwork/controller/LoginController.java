package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    Service service;

    @FXML
    private TextField txtusername;
    @FXML
    private TextField txtpassword;

    @FXML
    private Button btnlogin;

    @FXML
    private Hyperlink signuplink;

    Stage stagem;

    public void setLoginController(Service service, Stage stage) {
        this.service = service;
        this.stagem = stage;
    }

    public void clearfield(){
        txtusername.clear();
        txtpassword.clear();
    }

    public void handleLogin(ActionEvent actionEvent)  {
        String username = txtusername.getText();
        String password = txtpassword.getText();

        Utilizator u=service.findbyUsername(username);
        if(u==null){
            Alert alert = new Alert(Alert.AlertType.ERROR,"Username not found");
            alert.showAndWait();
            return;
        }
        else if( Utilizator.checkPassword(password, u.getPassword())){

            try {
                FXMLLoader loader=new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/example/socialnetwork/views/user_view.fxml"));
                Parent pane=loader.load();

                Stage stage=new Stage();
                Scene scene=new Scene(pane);
                stage.setTitle("Home");
                stage.setScene(scene);

                UtilizatorContoller utilizatorController=loader.getController();
                utilizatorController.initialize(u);
                utilizatorController.setController(service);
                stage.show();
                //stagem.close();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING,"Wrong Password");
            alert.showAndWait();
            return;
        }



    }

    public void handleSignup(ActionEvent actionEvent) {
        try{
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetwork/views/signup_view.fxml"));
            AnchorPane pane=loader.load();
            Stage stage=new Stage();
            Scene scene=new Scene(pane);
            stage.setTitle("Sign Up");
            stage.setScene(scene);
            SignUpController controller=loader.getController();
            controller.setController(service,stage);
            stage.show();
            stagem.close();

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
