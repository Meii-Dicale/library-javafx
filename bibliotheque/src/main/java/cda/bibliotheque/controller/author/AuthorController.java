package cda.bibliotheque.controller.author;

import java.io.IOException;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.AuthorDAO;
import cda.bibliotheque.model.Author;
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

public class AuthorController {

    @FXML
    private TableView<Author> AuthorTable;

    @FXML
    private TableColumn<Author, String> colFirstname;

    @FXML
    private TableColumn<Author, String> colLastname;

    @FXML
    private TableColumn<Author, Void> colActions;

    @FXML
    private TextField searchField;

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
                    boolean confirmed = App.showConfirmationDialog("Confirmation de suppression", "Êtes-vous sûr de vouloir supprimer l'auteur " + author.getFirstname() + " " + author.getLastname() + " ?");
                    if (confirmed) {
                        authorDAO.deleteAuthor(author.getId());
                        loadAuthors();
                    }
                });
                buttonEdit.setOnAction(event -> {
                    int index = getIndex();
                    Author authorToEdit = AuthorTable.getItems().get(index);
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("/cda/bibliotheque/authors/edit-author.fxml"));
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

        // Logique de recherche
        if (searchField != null) {
            FilteredList<Author> filteredData = new FilteredList<>(authorList, p -> true);
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(author -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return (author.getFirstname() + " " + author.getLastname()).toLowerCase().contains(lowerCaseFilter);
                });
            });
            AuthorTable.setItems(filteredData);
        } else {
            AuthorTable.setItems(authorList);
        }
    }

    @FXML
    private void switchToCreate() throws IOException {
        App.setRoot("authors/create-author");
    }
    @FXML
    void goBack() throws IOException {
        App.setRoot("primary");
    }

    private void loadAuthors() {
        authorList.setAll(authorDAO.getAllAuthors());
    }

}
