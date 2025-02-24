package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.FriendshipStatus;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.Service;
import com.example.socialnetwork.utils.events.ChangeTypeEvent;
import com.example.socialnetwork.utils.events.Event;
import com.example.socialnetwork.utils.events.FrEvent;
import com.example.socialnetwork.utils.observers.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class NotificationController implements Observer<Event>{


    @FXML
    private TableView<FriendRequest> requestsTable;

    @FXML
    private TableColumn<FriendRequest, String> requestUsernameColumn;
    
    @FXML
    Button rejbutton;
    @FXML
    private TableColumn<FriendRequest, String> requestDateColumn;

    @FXML
    private TableColumn<FriendRequest, String> requestStatusColumn;

    private ObservableList<FriendRequest> requestList = FXCollections.observableArrayList();

    Utilizator current;
    Service service;

    public void initialize(Utilizator user) {
        this.current= user;
        requestUsernameColumn.setCellValueFactory(cellData->{
            if(user.getId().equals(cellData.getValue().getToUserId())){
                Long sendID=cellData.getValue().getFromUserId();
                Utilizator u=service.findUtilizator(sendID);
                return new SimpleStringProperty(u.getUsername());
            }else{
                Long sendID=cellData.getValue().getToUserId();
                Utilizator u=service.findUtilizator(sendID);
                return new SimpleStringProperty(u.getUsername());
            }
        });

        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        requestStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        requestsTable.setItems(requestList);
    }

    public void setController(Service service) {
        this.service = service;
        this.service.addObserver(this);
        initmodel();
    }
    private void initmodel() {
        List<FriendRequest> requests = service.getReceivedFriendRequests(current.getId());
        requestList.setAll(requests);
    }

    @Override
    public void update(Event event) {
        if (event instanceof FrEvent) {
            FrEvent frEvent = (FrEvent) event;
            FriendRequest request = frEvent.getData();
            if (frEvent.getType() == ChangeTypeEvent.ADD ) {
                initmodel();
            }

            if (frEvent.getType() == ChangeTypeEvent.UPDATE && request.getStatus() == FriendshipStatus.ACCEPTED) {
               initmodel();
            }
        }

    }

    public void handleAcceptRequest(ActionEvent actionEvent) {
        FriendRequest selectedRequest = requestsTable.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            service.acceptFriendRequest(selectedRequest.getFromUserId(), selectedRequest.getToUserId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cererea de prietenie a fost acceptată!");
            alert.showAndWait();
            requestList.remove(selectedRequest);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selectați o cerere pentru a o accepta.");
            alert.showAndWait();
        }
    }

    public void handleRejectRequest(ActionEvent actionEvent) {
        FriendRequest selectedRequest = requestsTable.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            service.rejectFriendRequest(selectedRequest.getFromUserId(), selectedRequest.getToUserId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cererea de prietenie a fost respinsa!");
            alert.showAndWait();
            requestList.remove(selectedRequest);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selectați o cerere pentru a o refuza.");
            alert.showAndWait();
        }
    }
}
