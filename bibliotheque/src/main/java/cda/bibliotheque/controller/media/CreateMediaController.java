package cda.bibliotheque.controller.media;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.MediaDAO;
import cda.bibliotheque.model.Media;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import javafx.scene.control.TextField;

public class CreateMediaController {
    
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

    private final MediaDAO mediaDAO = new MediaDAO();


    @FXML
    void createMedia(ActionEvent event) throws IOException {
        String title = inputTitle.getText();
        int author_id = (Integer.valueOf(inputAuthor.getText()));
        String edition = inputEdition.getText();
        int year = (Integer.valueOf(inputYear.getText()));
        String summary = inputSummary.getText();

        Media media = new Media( title,  edition,  year,  summary,  author_id);
        mediaDAO.addMedia(media);
        App.setRoot("medias/medias");

    }
}
