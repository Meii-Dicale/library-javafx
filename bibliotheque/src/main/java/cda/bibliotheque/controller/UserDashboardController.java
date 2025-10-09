package cda.bibliotheque.controller;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.ReservationDAO;
import cda.bibliotheque.model.Reservation;
import cda.bibliotheque.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class UserDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Reservation> reservationsTable;

    @FXML
    private TableColumn<Reservation, String> colMediaTitle;

    @FXML
    private TableColumn<Reservation, String> colStartDate;

    @FXML
    private TableColumn<Reservation, String> colEndDate;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        User loggedInUser = App.getLoggedInUser();
        if (loggedInUser != null) {
            welcomeLabel.setText("Bienvenue, " + loggedInUser.getUser_name() + " !");

            colMediaTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStock().getMedia().getTitle()));
            colStartDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartedAtDate().format(formatter)));
            colEndDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getEndedAtDate() != null ? cellData.getValue().getEndedAtDate().format(formatter) : "N/A"
            ));

            // Charger uniquement les réservations de l'utilisateur connecté
            ObservableList<Reservation> userReservations = FXCollections.observableArrayList(reservationDAO.getReservationsByUserId(loggedInUser.getId()));
            reservationsTable.setItems(userReservations);
        }
    }

    @FXML
    private void logout() {
        try {
            App.setLoggedInUser(null);
            App.setRoot("login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}