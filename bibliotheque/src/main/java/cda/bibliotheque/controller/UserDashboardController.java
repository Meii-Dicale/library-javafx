package cda.bibliotheque.controller;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.ReservationDAO;
import cda.bibliotheque.model.Reservation;
import cda.bibliotheque.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.time.LocalDate;
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

    @FXML
    public void initialize() {
        User loggedInUser = App.getLoggedInUser();
        if (loggedInUser != null) {
            welcomeLabel.setText("Bienvenue, " + loggedInUser.getUser_name() + " !");

            // Utiliser des CellValueFactory personnalisées pour un affichage correct
            colMediaTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStock().getMedia().getTitle()));
            colStartDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartedAtDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            colEndDate.setCellValueFactory(cellData -> {
                LocalDate endDate = cellData.getValue().getEndedAtDate();
                return new SimpleStringProperty(endDate != null ? endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "En cours");
            });

            // Mettre en évidence les réservations en retard
            reservationsTable.setRowFactory(tv -> new TableRow<Reservation>() {
                @Override
                protected void updateItem(Reservation item, boolean empty) {
                    super.updateItem(item, empty);
                    getStyleClass().remove("overdue-row"); // Toujours retirer l'ancien style
                    if (item != null && !empty) {
                        // Une réservation est en retard si elle n'est pas terminée ET que la date de fin est passée
                        LocalDate dueDate = item.getEndedAtDate();
                        if (!item.isEnded() && dueDate != null && dueDate.isBefore(LocalDate.now())) {
                            if (!getStyleClass().contains("overdue-row")) {
                                getStyleClass().add("overdue-row");
                            }
                        }
                    }
                }
            });

            // Correction du nom de la méthode
            reservationsTable.getItems().setAll(reservationDAO.getReservationsByUserId(loggedInUser.getId()));
        }
    }

    @FXML
    private void logout() throws IOException {
        App.logout();
    }

    @FXML
    private void switchToAccount() throws IOException {
        App.setRoot("account");
    }
}