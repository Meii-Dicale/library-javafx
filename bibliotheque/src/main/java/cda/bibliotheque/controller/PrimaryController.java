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
}
