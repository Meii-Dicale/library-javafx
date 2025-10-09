package cda.bibliotheque.controller.author;


import cda.bibliotheque.App;
import cda.bibliotheque.dao.AuthorDAO;
import cda.bibliotheque.model.Author;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CreateAuthorController {

    @FXML
    private TextField inputFirstname;

    @FXML
    private TextField inputLastname;

    private final AuthorDAO authorDAO = new AuthorDAO();

    @FXML
    void submit(ActionEvent event) throws IOException {
        if (!validateFields()) {
            return;
        }

        String firstname = inputFirstname.getText();
        String lastname = inputLastname.getText();

        Author author = new Author(firstname, lastname);
        authorDAO.addAuthor(author);
        App.showAlert(Alert.AlertType.INFORMATION, "Succès", "L'auteur " + firstname + " " + lastname + " a été ajouté.");
        App.setRoot("authors/authors");
    }
    private boolean validateFields() {
        inputFirstname.getStyleClass().remove("invalid-field");
        inputLastname.getStyleClass().remove("invalid-field");
        boolean isValid = true;

        if (inputFirstname.getText() == null || inputFirstname.getText().trim().isEmpty()) {
            inputFirstname.getStyleClass().add("invalid-field");
            isValid = false;
        }
        if (inputLastname.getText() == null || inputLastname.getText().trim().isEmpty()) {
            inputLastname.getStyleClass().add("invalid-field");
            isValid = false;
        }

        if (!isValid) {
            App.showAlert(Alert.AlertType.WARNING, "Champs manquants", "Le prénom et le nom de l'auteur sont obligatoires.");
        }
        return isValid;
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("authors/authors");
    }

}
