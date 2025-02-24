package com.example.socialnetwork;

import com.example.socialnetwork.controller.LoginController;
import com.example.socialnetwork.domain.validators.UtilizatorValiidator;
import com.example.socialnetwork.repository.database.FriendhipReqDBRepo;
import com.example.socialnetwork.repository.database.MessageDBRepo;
import com.example.socialnetwork.repository.database.PrietenieDBRepo;
import com.example.socialnetwork.repository.database.UtilizatorDBRepo;
import com.example.socialnetwork.service.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    Service service;
    @Override
    public void start(Stage stage) throws IOException {
        String username="postgres";
        String password="andrei921";
        String url="jdbc:postgresql://localhost:5432/postgres";

        UtilizatorDBRepo uRepo=new UtilizatorDBRepo(url,username,password,new UtilizatorValiidator());
        MessageDBRepo mRepo=new MessageDBRepo(url,username,password,uRepo);
        PrietenieDBRepo pRepo=new PrietenieDBRepo(url,username,password);
        FriendhipReqDBRepo fRepo=new FriendhipReqDBRepo(url,username,password);
        service=new Service(uRepo,pRepo,mRepo,fRepo);
        initView(stage);
        stage.setTitle("Log In");
        stage.show();
    }

    private void initView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/socialnetwork/views/login_view.fxml"));
        AnchorPane loginView = fxmlLoader.load();
        stage.setScene(new Scene(loginView));
        LoginController controller=fxmlLoader.getController();
        controller.setLoginController(service,stage);
    }

    public static void main(String[] args) {
        launch();
    }
}