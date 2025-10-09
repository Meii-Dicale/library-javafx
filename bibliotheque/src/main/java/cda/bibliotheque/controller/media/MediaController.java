package cda.bibliotheque.controller.media;

import java.io.IOException;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.MediaDAO;
import cda.bibliotheque.model.Author;
import cda.bibliotheque.model.Media;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class MediaController {

    @FXML
    private TableView<Media> tableMedia;

    @FXML
    private TableColumn<Media, Void> colActions;

    @FXML
    private TableColumn<Media, Integer> colAuthor;

    @FXML
    private TableColumn<Media, String> colEdition;

    @FXML
    private TableColumn<Media, String> colTitle;

    @FXML
    private TableColumn<Media, Integer> colYear;

    private final ObservableList<Media> mediaList = FXCollections.observableArrayList();
    private final MediaDAO mediaDAO = new MediaDAO();

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        colEdition.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEdition()));
        colYear.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getYear()).asObject());
        colAuthor.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAuthor_id()).asObject());
        colActions.setCellFactory(cellData -> new TableCell<>() {
            private final Button buttonDelete = new Button("Delete");
            private final Button buttonEdit = new Button("Edit");
            private final HBox box = new HBox(buttonDelete, buttonEdit);

            {
                buttonDelete.setOnAction(event -> {
                    Media media = getTableView().getItems().get(getIndex());
                    mediaDAO.deleteMedia(media.getId());
                    loadMedias();
                });
                buttonEdit.setOnAction(event -> {
                    int index = getIndex();
                    Media mediaToEdit = tableMedia.getItems().get(index);
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("medias/edit-media.fxml"));
                        Parent parent = loader.load();
                        EditMediaController controller = loader.getController();
                        controller.setMedia(mediaToEdit);
                        App.getScene().setRoot(parent);

                    } catch (IOException e) {
                       System.err.println(e.getMessage());
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }



        });
        loadMedias();
    }

    @FXML
    private void switchToCreateMedia() throws IOException {
        App.setRoot("medias/create-media");

    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("primary");
    }

    private void loadMedias() {
        mediaList.setAll(mediaDAO.getAllMedias());
        tableMedia.setItems(mediaList);

    }
}
