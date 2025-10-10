package cda.bibliotheque.controller.reservation;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.ReservationDAO;
import cda.bibliotheque.model.User;
import cda.bibliotheque.model.Reservation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private TextField searchField;

    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private FilteredList<Reservation> filteredData;
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

        // Configurer le filtre par statut
        setupStatusFilter();

        // Logique de recherche
        filteredData = new FilteredList<>(reservationList, p -> true);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        statusFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());

        tableReservations.setItems(filteredData);
    }

    /**
     * Méthode publique pour filtrer les réservations pour un utilisateur spécifique.
     * Appelée depuis l'extérieur (ex: UsersController).
     * @param user L'utilisateur pour lequel filtrer les réservations.
     */
    public void filterByUser(User user) {
        statusFilterComboBox.setValue("En cours");
        searchField.setText(user.getUser_name());
    }

    private void setupStatusFilter() {
        ObservableList<String> statuses = FXCollections.observableArrayList("Toutes", "En cours", "En retard", "Terminée");
        statusFilterComboBox.setItems(statuses);
        statusFilterComboBox.setValue("Toutes");
    }

    private void updateFilter() {
        String searchText = searchField.getText();
        String status = statusFilterComboBox.getValue();

        filteredData.setPredicate(reservation -> {
            // Filtre par statut
            boolean statusMatch = false;
            if (status == null || status.equals("Toutes")) {
                statusMatch = true;
            } else {
                boolean isOverdue = !reservation.isEnded() && reservation.getEndedAtDate() != null && reservation.getEndedAtDate().isBefore(LocalDate.now());
                switch (status) {
                    case "En cours":
                        statusMatch = !reservation.isEnded() && !isOverdue;
                        break;
                    case "En retard":
                        statusMatch = isOverdue;
                        break;
                    case "Terminée":
                        statusMatch = reservation.isEnded();
                        break;
                }
            }

            // Filtre par texte
            boolean textMatch = true;
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                textMatch = reservation.getStock().getMedia().getTitle().toLowerCase().contains(lowerCaseFilter) ||
                            reservation.getUser().getUser_name().toLowerCase().contains(lowerCaseFilter);
            }

            return statusMatch && textMatch;
        });
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