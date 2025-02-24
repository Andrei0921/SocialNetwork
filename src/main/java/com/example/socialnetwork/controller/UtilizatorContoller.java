package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.Service;
import com.example.socialnetwork.utils.events.Event;
import com.example.socialnetwork.utils.events.FrEvent;
import com.example.socialnetwork.utils.observers.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class UtilizatorContoller implements Observer<Event> {

    Service service;
    @FXML
    StackPane stackPane;
    @FXML
    Button profilButton;
    @FXML
    Button searchButton;
    @FXML
    Button mButton;
    @FXML
    Button notButton;
    @FXML
    Button logoutButton;
    @FXML
    Button pButton;
    @FXML
    Label defaultLabel;
    @FXML
    private ImageView profileImage;
    Utilizator current;

    public void initialize(Utilizator utilizator) {
        this.current = utilizator;
        setProfileImage(utilizator);

    }

    private void setProfileImage(Utilizator user) {
        String imagePath = "";

        if (user.getUsername().equals("marcablanca")) {
            imagePath = "/com/example/socialnetwork/images/Imagine WhatsApp 2025-02-18 la 13.58.02_ff71cd92.jpg";
        }else if (user.getUsername().equals("giuseppe")) {
            imagePath = "/com/example/socialnetwork/images/Imagine WhatsApp 2025-02-18 la 13.59.02_fc734314.jpg";
        }
        else{
            imagePath="/com/example/socialnetwork/images/descărcare.png";
        }
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        profileImage.setImage(image);
    }

    public void setController(Service service) {
        this.service = service;
        this.service.addObserver(this);
        updateRequestBtn();

    }

    public void updateRequestBtn(){
        long pendingRequests = service.pendingreq(current.getId());
        if (pendingRequests > 0) {
            notButton.setStyle("-fx-background-color: red;");
        }else{
            notButton.setStyle("");
        }
    }

    public void handleProfile(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/views/profil_view.fxml"));
            Node newView = loader.load();
            stackPane.getChildren().setAll(newView);
            ProfilController profilController = loader.getController();
            profilController.initialize(current);
            profilController.setController(service);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleSearch(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/views/cauta_view.fxml"));
            Node newView = loader.load();
            stackPane.getChildren().setAll(newView);
            SearchController controller = loader.getController();
            controller.initialize(current);
            controller.setService(service);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleLogout(ActionEvent actionEvent) throws IOException {
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

    public void handleMessages(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/views/chat_view.fxml"));
            Node newView = loader.load();
            stackPane.getChildren().setAll(newView);
            MainChatController controller=loader.getController();
            controller.initialize(current);
            controller.setController(service);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleNotifications(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/views/notificari_view.fxml"));
            Node newView = loader.load();
            stackPane.getChildren().setAll(newView);
            NotificationController controller=loader.getController();
            controller.initialize(current);
            controller.setController(service);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlePrieteni(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/views/prieteni_view.fxml"));
            Node newView = loader.load();
            stackPane.getChildren().setAll(newView);
            PrieteniController controller = loader.getController();
            controller.initialize(current);
            controller.setController(service);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Event event) {
        if (event instanceof FrEvent) {
            updateRequestBtn();
        }

    }
}
