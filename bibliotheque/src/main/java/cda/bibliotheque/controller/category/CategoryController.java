package cda.bibliotheque.controller.category;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.CategoryDAO;
import cda.bibliotheque.model.Category;
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

import java.io.IOException;

public class CategoryController {

    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, String> colName;
    @FXML
    private TableColumn<Category, Void> colActions;

    @FXML
    private TextField searchField;

    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTypeName()));
        colActions.setCellFactory(cellData -> new TableCell<>() {
            private final Button buttonDelete = new Button("Supprimer");
            private final Button buttonEdit = new Button("Modifier");
            private final HBox box = new HBox(10, buttonDelete, buttonEdit);

            {
                buttonDelete.getStyleClass().add("button-delete");
                buttonDelete.setOnAction(event -> {
                    Category category = getTableView().getItems().get(getIndex());
                    boolean confirmed = App.showConfirmationDialog("Confirmation de suppression", "Êtes-vous sûr de vouloir supprimer la catégorie '" + category.getTypeName() + "' ?");
                    if (confirmed) {
                        categoryDAO.delete(category.getId());
                        loadCategories();
                    }
                });
                buttonEdit.setOnAction(event -> {
                    Category categoryToEdit = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("/cda/bibliotheque/categories/edit-category.fxml"));
                        Parent parent = loader.load();
                        EditCategoryController controller = loader.getController();
                        controller.setCategory(categoryToEdit);
                        App.getScene().setRoot(parent);
                    } catch (IOException e) {
                        System.err.println("Erreur lors du chargement de la page de modification de catégorie : " + e.getMessage());
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
        loadCategories();

        // Logique de recherche
        if (searchField != null) {
            FilteredList<Category> filteredData = new FilteredList<>(categoryList, p -> true);
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(category -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return category.getTypeName().toLowerCase().contains(lowerCaseFilter);
                });
            });
            categoryTable.setItems(filteredData);
        } else {
            categoryTable.setItems(categoryList);
        }
    }

    private void loadCategories() {
        categoryList.setAll(categoryDAO.getAll());
        // categoryTable.setItems(categoryList); // This is now handled by the search logic
    }

    @FXML
    private void switchToCreate() throws IOException {
        App.setRoot("categories/create-category");
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("primary");
    }
}