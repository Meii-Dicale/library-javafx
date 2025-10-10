package cda.bibliotheque.controller;

import java.io.IOException;

import cda.bibliotheque.App;
import cda.bibliotheque.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PrimaryController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        User loggedInUser = App.getLoggedInUser();
        if (loggedInUser != null) {
            welcomeLabel.setText("Bienvenue, " + loggedInUser.getUser_name() + " !");
        }
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("authors/authors");
    }

    @FXML
    private void switchToMedias() throws IOException {
        App.setRoot("medias/medias");
    }

    @FXML
    private void switchToUsers() throws IOException {
        App.setRoot("users/users");
    }

    @FXML
    private void switchToCategories() throws IOException {
        App.setRoot("categories/categories");
    }

    @FXML
    private void switchToReservations() throws IOException {
        App.setRoot("reservations/reservations"); // This path is now correct
    }

    @FXML
    private void switchToStock() throws IOException {
        App.setRoot("stock/stock");
    }

    @FXML
    private void handleLogout() throws IOException {
        App.logout();
    }
}
