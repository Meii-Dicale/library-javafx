package cda.bibliotheque.controller.category;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.CategoryDAO;
import cda.bibliotheque.model.Category;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CreateCategoryController {

    @FXML
    private TextField inputName;

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @FXML
    void createCategory(ActionEvent event) throws IOException {
        String name = inputName.getText();
        if (name != null && !name.trim().isEmpty()) {
            Category category = new Category();
            category.setTypeName(name);
            categoryDAO.save(category);
            App.setRoot("categories/categories");
        }
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("categories/categories");
    }
}