package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.FriendshipStatus;
import com.example.socialnetwork.domain.Prietenie;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.Service;
import com.example.socialnetwork.utils.events.ChangeTypeEvent;
import com.example.socialnetwork.utils.events.Event;
import com.example.socialnetwork.utils.events.FrEvent;
import com.example.socialnetwork.utils.events.PrietenieEvent;
import com.example.socialnetwork.utils.observers.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PrieteniController implements Observer<Event> {

    @FXML
    protected TableView<Utilizator> friendTable;

    @FXML
    private TableColumn<Utilizator, String> friendFirstNameColumn;

    @FXML
    private TableColumn<Utilizator, String> friendLastNameColumn;

    @FXML
    private TableColumn<Utilizator, String> friendUsernameColumn;
    private ObservableList<Utilizator> friendList= FXCollections.observableArrayList();
    Utilizator current;
    Service service;
    @FXML
    Button deleteButton;

    public void initialize(Utilizator user) {

        this.current = user;
        friendFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        friendLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friendUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        friendTable.setItems(friendList);


    }

    public void setController(Service service) {
        this.service = service;
        this.service.addObserver(this);
        initModel();
    }
    private void initModel() {

        Iterable<Prietenie> pIterable = service.getPrietenieu(current.getId());

        List<Utilizator> utilizatorList = StreamSupport.stream(pIterable.spliterator(), false)
                .filter(p-> Objects.equals(p.getStatus(), "Accepted"))
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




    @FXML
    public void handleDelete(ActionEvent event) {
        Utilizator selectedFriendship = friendTable.getSelectionModel().getSelectedItem();
        if (selectedFriendship != null) {
            Prietenie p=service.findPrietenie(selectedFriendship.getId(), current.getId());
            service.stergePrietenie(p.getId());
            friendList.remove(selectedFriendship);
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Prietenia a fost ștearsă cu succes.");
            successAlert.showAndWait();

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selectați un prieten pentru a-l șterge.");
            alert.showAndWait();
        }
    }
    @Override
    public void update(Event event) {
            if(event instanceof PrietenieEvent) {
                PrietenieEvent Event = (PrietenieEvent) event;
                if (Event.getType() == ChangeTypeEvent.ADD ) {
                    initModel();
                }

                if (Event.getType() == ChangeTypeEvent.DELETE ) {
                    initModel();
                }
            }

    }
}
