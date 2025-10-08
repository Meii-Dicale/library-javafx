package cda.bibliotheque.controller.author;

import java.io.IOException;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.AuthorDAO;
import cda.bibliotheque.model.Author;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AuthorController {

    @FXML
    private TableView<Author> AuthorTable;

    @FXML
    private TableColumn<Author, String> colFirstname;

    @FXML
    private TableColumn<Author, String> colLastname;

    @FXML
    private TableColumn<Author, Void> colActions;

    private final ObservableList<Author> authorList = FXCollections.observableArrayList();
    private final AuthorDAO authorDAO = new AuthorDAO();

    @FXML
    public void initialize() {
        colFirstname.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstname()));
        colLastname.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastname()));
        colActions.setCellFactory(cellData -> new TableCell<>() {
            private final Button buttonDelete = new Button("Delete");
            private final Button buttonEdit = new Button("Edit");
            private final HBox box = new HBox(buttonDelete, buttonEdit);

            {
                buttonDelete.setOnAction(event -> {
                    Author author = getTableView().getItems().get(getIndex());
                    authorDAO.deleteAuthor(author.getId());
                    loadAuthors();
                });
                buttonEdit.setOnAction(event -> {
                    int index = getIndex();
                    Author authorToEdit = AuthorTable.getItems().get(index);
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("authors/edit-author.fxml"));
                        Parent parent = loader.load();
                        EditAuthorController controller = loader.getController();
                        controller.setAuthor(authorToEdit);
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
        loadAuthors();
    }

    @FXML
    private void switchToCreate() throws IOException {
        App.setRoot("authors/create-author");
    }

    private void loadAuthors() {
        authorList.setAll(authorDAO.getAllAuthors());
        AuthorTable.setItems(authorList);
    }

}
