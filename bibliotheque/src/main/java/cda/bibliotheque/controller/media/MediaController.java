package cda.bibliotheque.controller.media;

import java.io.IOException;

import java.util.stream.Collectors;
import cda.bibliotheque.App;
import cda.bibliotheque.dao.MediaDAO;
import cda.bibliotheque.model.Media;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class MediaController {

    @FXML
    private TableView<Media> tableMedia;

    @FXML
    private TableColumn<Media, Void> colActions;

    @FXML
    private TableColumn<Media, String> colAuthor;

    @FXML
    private TableColumn<Media, String> colCategories;

    @FXML
    private TableColumn<Media, String> colEdition;

    @FXML
    private TableColumn<Media, String> colTitle;

    @FXML
    private TableColumn<Media, Integer> colYear;

    @FXML
    private TextField searchField;

    private final ObservableList<Media> mediaList = FXCollections.observableArrayList();
    private final MediaDAO mediaDAO = new MediaDAO();

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        colEdition.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEdition()));
        colYear.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getYear()).asObject());
        colAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor().getFirstname() + " " + cellData.getValue().getAuthor().getLastname()));
        colCategories.setCellValueFactory(cellData -> {
            String categories = cellData.getValue().getCategories().stream()
                    .map(c -> c.getTypeName())
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(categories);
        });
        colActions.setCellFactory(cellData -> new TableCell<>() {
            private final Button buttonDelete = new Button("Supprimer");
            private final Button buttonEdit = new Button("Modifier");
            private final HBox box = new HBox(10, buttonDelete, buttonEdit);

            {
                buttonDelete.getStyleClass().add("button-delete");
                buttonDelete.setOnAction(event -> {
                    Media media = getTableView().getItems().get(getIndex());
                    boolean confirmed = App.showConfirmationDialog("Confirmation de suppression", "Êtes-vous sûr de vouloir supprimer le média '" + media.getTitle() + "' ?");
                    if (confirmed) {
                        mediaDAO.deleteMedia(media.getId());
                        loadMedias();
                    }
                });
                buttonEdit.setOnAction(event -> {
                    int index = getIndex();
                    Media mediaToEdit = tableMedia.getItems().get(index);
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("/cda/bibliotheque/medias/edit-media.fxml"));
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

        // Logique de recherche
        if (searchField != null) {
            FilteredList<Media> filteredData = new FilteredList<>(mediaList, p -> true);
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(media -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    String categories = media.getCategories().stream().map(c -> c.getTypeName()).collect(Collectors.joining(", ")).toLowerCase();

                    return media.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                           (media.getAuthor().getFirstname() + " " + media.getAuthor().getLastname()).toLowerCase().contains(lowerCaseFilter) ||
                           categories.contains(lowerCaseFilter);
                });
            });
            tableMedia.setItems(filteredData);
        } else {
            tableMedia.setItems(mediaList);
        }
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
    }
}
