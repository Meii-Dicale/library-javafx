package cda.bibliotheque.controller.reservation;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.ReservationDAO;
import cda.bibliotheque.dao.StockDAO;
import cda.bibliotheque.dao.UsersDAO;
import cda.bibliotheque.model.Reservation;
import cda.bibliotheque.model.Stock;
import cda.bibliotheque.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
        // --- Configuration de la recherche d'utilisateurs ---
        ObservableList<User> allUsers = FXCollections.observableArrayList(usersDAO.getAllUsers());
        FilteredList<User> filteredUsers = new FilteredList<>(allUsers, p -> true);

        userComboBox.setItems(filteredUsers);
        userComboBox.setEditable(true);
        userComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getUser_name() + " (" + user.getMail() + ")" : "";
            }
            @Override
            public User fromString(String string) {
                return userComboBox.getItems().stream().filter(u -> 
                    (u.getUser_name() + " (" + u.getMail() + ")").equals(string)
                ).findFirst().orElse(null);
            }
        });

        userComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {

            filteredUsers.setPredicate(user -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lowerCaseFilter = newVal.toLowerCase();
                String userString = user.getUser_name().toLowerCase() + " (" + user.getMail().toLowerCase() + ")";

                // Si le texte tapé est une sous-chaîne du nom/email, ou si le texte complet correspond
                return user.getUser_name().toLowerCase().contains(lowerCaseFilter) ||
                       user.getMail().toLowerCase().contains(lowerCaseFilter) ||
                       userString.equals(lowerCaseFilter);
            });

            // Afficher la liste déroulante si du texte est saisi
            if (userComboBox.isFocused() && !newVal.isEmpty()) {
                userComboBox.show();
            }
        });

        // --- Configuration de la recherche d'exemplaires ---
        ObservableList<Stock> allStock = FXCollections.observableArrayList(stockDAO.getAll().stream().filter(Stock::isAvailable).collect(Collectors.toList()));
        FilteredList<Stock> filteredStock = new FilteredList<>(allStock, p -> true);

        stockComboBox.setItems(filteredStock);
        stockComboBox.setEditable(true);
        stockComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Stock stock) {
                return stock != null ? stock.getMedia().getTitle() + " (ID: " + stock.getId() + ")" : "";
            }
            @Override
            public Stock fromString(String string) {
                return stockComboBox.getItems().stream().filter(s -> 
                    (s.getMedia().getTitle() + " (ID: " + s.getId() + ")").equals(string)
                ).findFirst().orElse(null);
            }
        });

        stockComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {

            filteredStock.setPredicate(stock -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lowerCaseFilter = newVal.toLowerCase();
                String stockString = stock.getMedia().getTitle().toLowerCase() + " (id: " + stock.getId() + ")";

                // Si le texte tapé est une sous-chaîne du titre/ID, ou si le texte complet correspond
                return stock.getMedia().getTitle().toLowerCase().contains(lowerCaseFilter) ||
                       String.valueOf(stock.getId()).contains(lowerCaseFilter) ||
                       stockString.equals(lowerCaseFilter);
            });

            // Afficher la liste déroulante si du texte est saisi
            if (stockComboBox.isFocused() && !newVal.isEmpty()) {
                stockComboBox.show();
            }
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