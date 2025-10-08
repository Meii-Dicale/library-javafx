package cda.bibliotheque.controller.media;

import java.io.IOException;

import cda.bibliotheque.App;
import cda.bibliotheque.model.Author;
import cda.bibliotheque.model.Media;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MediaController {
    
    @FXML
    private TableView<Media> tableMedia;

    @FXML
    private TableColumn<Media , Void> colActions;

    @FXML
    private TableColumn<Author, String> colAuthor;

    @FXML
    private TableColumn<Media, String> colEdition;

    @FXML
    private TableColumn<Media, String> colTitle;

    @FXML
    private TableColumn<Media, Integer> colYear;

    @FXML
    public void initialize(){
        colTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        colEdition.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEdition()));
        colYear.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getYear()).asObject());
    }

    @FXML
    void switchToCreateMedia() throws IOException {
        App.setRoot("medias/create-media");

    }
}
