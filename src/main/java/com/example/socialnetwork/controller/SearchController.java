package com.example.socialnetwork.controller;

import com.example.socialnetwork.domain.FriendRequest;
import com.example.socialnetwork.domain.Utilizator;
import com.example.socialnetwork.service.Service;
import com.example.socialnetwork.utils.events.ChangeTypeEvent;
import com.example.socialnetwork.utils.events.FrEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class SearchController {
    Service service;
    @FXML
    private TextField txtSearch;
    @FXML
    private ListView<Utilizator> listView;
    @FXML
    Button addFr;
    Utilizator current;

    private ObservableList<Utilizator> searchResultList = FXCollections.observableArrayList();

    public void initialize(Utilizator utilizator) {
        this.current = utilizator;
        listView.setItems(searchResultList);
        listView.setVisible(false);
        listView.setOnMouseClicked(this::handleSearchResultClick);
    }

    public void setService(Service service) {
        this.service = service;
    }
    @FXML
    private void handleAddFriend() {
        Utilizator selectedUser = listView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            boolean alreadyFriends = service.areFriends(current.getId(), selectedUser.getId());
            boolean requestPending = service.requestExists(current.getId(), selectedUser.getId());

            if (alreadyFriends) {
                Alert alert = new Alert(Alert.AlertType.WARNING, selectedUser.getUsername() + " este deja prieten cu tine.");
                alert.showAndWait();
                txtSearch.clear();
                return;
            }

            if (requestPending) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Există deja o cerere de prietenie trimisă către " + selectedUser.getFirstName() + ".");
                alert.showAndWait();
                txtSearch.clear();
                return;
            }

            FriendRequest r =service.sendFriendRequest(current.getId(), selectedUser.getId());
            service.notifyObservers(new FrEvent(ChangeTypeEvent.ADD,r ));
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cerere trimisă către " + selectedUser.getFirstName());
            alert.showAndWait();
            txtSearch.clear();
            listView.setVisible(false);


        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selectați un utilizator!");
            alert.showAndWait();
        }
    }


    @FXML
    private void handleSearch() {
        String searchText = txtSearch.getText().toLowerCase();

        if (searchText.isEmpty()) {
            listView.setVisible(false);
            searchResultList.clear();
            return;
        }
        List<Utilizator> matchingUsers = service.findUsersByPartialName(searchText);
        searchResultList.setAll(matchingUsers);
        listView.setVisible(!matchingUsers.isEmpty());
    }
    private void handleSearchResultClick(MouseEvent event) {
        Utilizator selectedUser = listView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            txtSearch.setText(selectedUser.getUsername());
            listView.setVisible(false);
        }
    }

}
