package cda.bibliotheque.controller.author;

import java.io.IOException;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.AuthorDAO;
import cda.bibliotheque.model.Author;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class EditAuthorController {

    private final ObjectProperty<Author> author = new SimpleObjectProperty<>();
    private final AuthorDAO authorDAO = new AuthorDAO();

    public EditAuthorController() {
    }

    public void setAuthor(Author author) {
        this.author.set(author);

    }

    @FXML
    private TextField inputFirstname;

    @FXML
    private TextField inputLastname;

    @FXML
    public void initialize() {
        author.addListener((obs, oldAuthor, newAuthor) -> {
            if (newAuthor != null) {
                inputFirstname.setText(newAuthor.getFirstname());
                inputLastname.setText(newAuthor.getLastname());
            }
        });

    }

    @FXML
    void submit(ActionEvent event) throws IOException {
        Author newAuthor = author.get();
        newAuthor.setFirstname(inputFirstname.getText());
        newAuthor.setLastname(inputLastname.getText());
        authorDAO.updateAuthor(newAuthor);
        App.setRoot("authors/authors");
    }
}
