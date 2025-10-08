package cda.bibliotheque.controller.author;

import java.io.IOException;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.AuthorDAO;
import cda.bibliotheque.model.Author;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateAuthorController {

    @FXML
    private TextField inputFirstname;

    @FXML
    private TextField inputLastname;

    private final AuthorDAO authorDAO = new AuthorDAO();

    @FXML
    void submit(ActionEvent event) throws IOException {
        String firstname = inputFirstname.getText();
        String lastname = inputLastname.getText();

        if (firstname.isEmpty()) {
            return;
        }

        Author author = new Author(firstname, lastname);
        authorDAO.addAuthor(author);
        App.setRoot("authors/authors");
    }

}
