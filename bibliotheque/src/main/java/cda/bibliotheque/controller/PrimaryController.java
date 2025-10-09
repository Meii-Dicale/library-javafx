package cda.bibliotheque.controller;

import java.io.IOException;

import cda.bibliotheque.App;
import javafx.fxml.FXML;

public class PrimaryController {

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
}
