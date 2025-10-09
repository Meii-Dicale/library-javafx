package cda.bibliotheque.controller.media;

import java.io.IOException;

import cda.bibliotheque.dao.AuthorDAO;
import cda.bibliotheque.App;
import cda.bibliotheque.dao.MediaDAO;
import cda.bibliotheque.model.Author;
import cda.bibliotheque.model.Media;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EditMediaController {

    private final ObjectProperty<Media> media = new SimpleObjectProperty<>();
    private final MediaDAO mediaDAO = new MediaDAO();
    private final AuthorDAO authorDAO = new AuthorDAO();

    public EditMediaController() {

    }
    public void setMedia(Media media) {
        this.media.set(media);
    }

    public void getMedia(Media media) {
        this.media.set(media);
    }
    @FXML
    private TextField inputAuthor;

    @FXML
    private TextField inputEdition;

    @FXML
    private TextArea inputSummary;

    @FXML
    private TextField inputTitle;

    @FXML
    private TextField inputYear;

    @FXML
    void initialize() {
        media.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                inputTitle.setText(newValue.getTitle());
                inputEdition.setText(newValue.getEdition());
                inputYear.setText(String.valueOf(newValue.getYear()));
                inputSummary.setText(newValue.getSummary());
                inputAuthor.setText(String.valueOf(newValue.getAuthor().getId()));
            }
        });

    }

    @FXML
    void submit(ActionEvent event ) throws IOException {
        Media newMedia = media.get();
        newMedia.setTitle(inputTitle.getText());
        newMedia.setEdition(inputEdition.getText());
        newMedia.setYear(Integer.parseInt(inputYear.getText()));
        newMedia.setSummary(inputSummary.getText());
        
        int authorId = Integer.parseInt(inputAuthor.getText());
        Author author = authorDAO.getAuthorById(authorId); // Assuming getAuthorById exists
        newMedia.setAuthor(author);

        mediaDAO.updateMedia(newMedia);
        App.setRoot("medias/medias");
    }

}
