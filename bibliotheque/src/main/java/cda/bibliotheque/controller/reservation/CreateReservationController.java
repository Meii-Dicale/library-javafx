package cda.bibliotheque.controller.reservation;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.ReservationDAO;
import cda.bibliotheque.dao.StockDAO;
import cda.bibliotheque.dao.UsersDAO;
import cda.bibliotheque.model.Reservation;
import cda.bibliotheque.model.Stock;
import cda.bibliotheque.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class CreateReservationController {

    @FXML
    private ComboBox<User> userComboBox;
    @FXML
    private ComboBox<Stock> stockComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField dueDateField;

    private final UsersDAO usersDAO = new UsersDAO();
    private final StockDAO stockDAO = new StockDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    @FXML
    public void initialize() {
        // Charger les utilisateurs
        userComboBox.getItems().setAll(usersDAO.getAllUsers());
        userComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getUser_name() + " (" + user.getMail() + ")" : "";
            }
            @Override
            public User fromString(String string) { return null; }
        });

        // Charger les exemplaires de stock disponibles
        stockComboBox.getItems().setAll(
                stockDAO.getAll().stream().filter(Stock::isAvailable).collect(Collectors.toList())
        );
        stockComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Stock stock) {
                return stock != null ? stock.getMedia().getTitle() + " (ID: " + stock.getId() + ")" : "";
            }
            @Override
            public Stock fromString(String string) { return null; }
        });

        // Mettre la date du jour par défaut
        startDatePicker.setValue(LocalDate.now());
        updateDueDate();

        // Mettre à jour la date de retour prévue lorsque la date de début change
        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            updateDueDate();
        });
    }

    @FXML
    void createReservation() throws IOException {
        User selectedUser = userComboBox.getValue();
        Stock selectedStock = stockComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate dueDate = startDate.plusDays(7);

        if (selectedUser != null && selectedStock != null && startDate != null) {
            Reservation newReservation = new Reservation();
            newReservation.setUser(selectedUser);
            newReservation.setStock(selectedStock);
            newReservation.setStartedAtDate(startDate);
            newReservation.setEndedAtDate(dueDate); // Stocker la date de retour prévue
            newReservation.setEnded(false);

            reservationDAO.save(newReservation);

            App.setRoot("reservations/reservations");
        } else {
            App.showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez sélectionner un utilisateur, un média et une date de début.");
        }
    }

    private void updateDueDate() {
        LocalDate startDate = startDatePicker.getValue();
        if (startDate != null) {
            dueDateField.setText(startDate.plusDays(7).toString());
        }
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("reservations/reservations");
    }
}