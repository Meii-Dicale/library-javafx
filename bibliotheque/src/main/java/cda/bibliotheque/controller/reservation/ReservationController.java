package cda.bibliotheque.controller.reservation;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.ReservationDAO;
import cda.bibliotheque.model.Reservation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReservationController {

    @FXML
    private TableView<Reservation> tableReservations;
    @FXML
    private TableColumn<Reservation, String> colMediaTitle;
    @FXML
    private TableColumn<Reservation, String> colUser;
    @FXML
    private TableColumn<Reservation, String> colStartDate;
    @FXML
    private TableColumn<Reservation, String> colEndDate;
    @FXML
    private TableColumn<Reservation, String> colStatus;
    @FXML
    private TableColumn<Reservation, Void> colActions;

    @FXML
    private TextField searchField;

    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Mettre à jour le DAO pour qu'il récupère les noms, pas juste les IDs
        colMediaTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStock().getMedia().getTitle()));
        colUser.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getUser_name()));

        colStartDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartedAtDate().format(formatter)));
        colEndDate.setCellValueFactory(cellData -> {
            LocalDate endDate = cellData.getValue().getEndedAtDate();
            return new SimpleStringProperty(endDate != null ? endDate.format(formatter) : "N/A");
        });
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isEnded() ? "Terminée" : "En cours"));

        colActions.setCellFactory(cellData -> new TableCell<>() {
            private final Button buttonEndReservation = new Button("Rendre");

            {
                buttonEndReservation.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    if (!reservation.isEnded()) {
                        reservationDAO.endReservation(reservation);
                        loadReservations();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()).isEnded()) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonEndReservation);
                }
            }
        });

        // Mettre en évidence les lignes des réservations en retard
        tableReservations.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("overdue-row"); // Toujours retirer l'ancien style
                if (item == null || empty) {
                    // Ne rien faire pour les lignes vides
                } else {
                    // Condition pour être en retard : non rendue ET date de retour prévue dépassée
                    if (!item.isEnded() && item.getEndedAtDate() != null && item.getEndedAtDate().isBefore(LocalDate.now())) {
                        if (!getStyleClass().contains("overdue-row")) {
                            getStyleClass().add("overdue-row");
                        }
                    }
                }
            }
        });

        loadReservations();

        // Logique de recherche
        if (searchField != null) {
            FilteredList<Reservation> filteredData = new FilteredList<>(reservationList, p -> true);
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(reservation -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return reservation.getStock().getMedia().getTitle().toLowerCase().contains(lowerCaseFilter) ||
                           reservation.getUser().getUser_name().toLowerCase().contains(lowerCaseFilter);
                });
            });
            tableReservations.setItems(filteredData);
        } else {
            tableReservations.setItems(reservationList);
        }
    }

    private void loadReservations() {
        // Il faudra améliorer getAll() dans ReservationDAO pour qu'il charge les objets complets
        reservationList.setAll(reservationDAO.getAll());
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    void switchToCreateReservation() throws IOException {
        App.setRoot("reservations/create-reservation");
    }
}