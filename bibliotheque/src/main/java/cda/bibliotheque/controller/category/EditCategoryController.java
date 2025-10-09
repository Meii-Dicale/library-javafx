package cda.bibliotheque.controller.category;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.CategoryDAO;
import cda.bibliotheque.model.Category;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class EditCategoryController {

    @FXML
    private TextField inputName;

    private final ObjectProperty<Category> category = new SimpleObjectProperty<>();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public void setCategory(Category category) {
        this.category.set(category);
    }

    @FXML
    public void initialize() {
        category.addListener((obs, oldCategory, newCategory) -> {
            if (newCategory != null) {
                inputName.setText(newCategory.getTypeName());
            }
        });
    }

    @FXML
    void updateCategory(ActionEvent event) throws IOException {
        String name = inputName.getText();
        if (name != null && !name.trim().isEmpty()) {
            Category categoryToUpdate = category.get();
            categoryToUpdate.setTypeName(name);
            categoryDAO.update(categoryToUpdate);
            App.setRoot("categories/categories");
        }
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("categories/categories");
    }
}