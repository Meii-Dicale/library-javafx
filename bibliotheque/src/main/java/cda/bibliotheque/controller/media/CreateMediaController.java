package cda.bibliotheque.controller.media;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.AuthorDAO;
import cda.bibliotheque.dao.CategoryDAO;
import cda.bibliotheque.dao.MediaDAO;
import cda.bibliotheque.model.Author;
import cda.bibliotheque.model.Category;
import cda.bibliotheque.model.Media;
import cda.bibliotheque.controller.category.ValidationUtil;
import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class CreateMediaController {
    
    @FXML
    private ComboBox<Author> authorComboBox;

    @FXML
    private TextField inputEdition;

    @FXML
    private TextArea inputSummary;

    @FXML
    private TextField inputTitle;

    @FXML
    private TextField inputYear;

    @FXML
    private ListView<Category> categoriesListView;

    private final MediaDAO mediaDAO = new MediaDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final AuthorDAO authorDAO = new AuthorDAO();
    private ObservableList<Author> authorList;

    @FXML
    public void initialize() {
        if (categoriesListView != null) {
            // Configurer comment afficher chaque objet Category dans la liste
            categoriesListView.setCellFactory(listView -> new ListCell<Category>() {
                @Override
                protected void updateItem(Category category, boolean empty) {
                    super.updateItem(category, empty);
                    if (empty || category == null) {
                        setText(null);
                    } else {
                        setText(category.getTypeName());
                    }
                }
            });
            // Charger les catégories et permettre la sélection multiple
            categoriesListView.getItems().setAll(categoryDAO.getAll());
            categoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        } else {
            System.err.println("categoriesListView is null. Check the fx:id in create-media.fxml.");
        }

        // Charger les auteurs et configurer la ComboBox
        authorList = FXCollections.observableArrayList(authorDAO.getAllAuthors());
        authorComboBox.setItems(authorList);

        // Configurer comment afficher chaque objet Author dans la liste
        authorComboBox.setConverter(new StringConverter<Author>() {
            @Override
            public String toString(Author author) {
                return author == null ? "" : author.getFirstname() + " " + author.getLastname();
            }

            @Override
            public Author fromString(String string) {
                // Permet de retrouver un auteur à partir du texte, utile pour l'autocomplétion
                return authorComboBox.getItems().stream()
                        .filter(author -> (author.getFirstname() + " " + author.getLastname()).equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Filtrer la liste déroulante lorsque l'utilisateur tape du texte
        authorComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                authorComboBox.setItems(authorList); // Afficher tous les auteurs si le champ est vide
            } else {
                ObservableList<Author> filteredList = authorList.filtered(author ->
                        (author.getFirstname() + " " + author.getLastname()).toLowerCase().contains(newText.toLowerCase())
                );
                authorComboBox.setItems(filteredList);
            }
        });
    }

    @FXML
    void createMedia(ActionEvent event) throws IOException {
        if (!validateFields()) {
            return;
        }

        String title = inputTitle.getText();
        Author author = authorComboBox.getValue();
        String edition = inputEdition.getText();
        int year = Integer.parseInt(inputYear.getText());
        String summary = inputSummary.getText();
        List<Category> selectedCategories = categoriesListView.getSelectionModel().getSelectedItems();

        Media media = new Media(title, edition, year, summary, author.getId());
        media.setAuthor(author);
        media.setCategories(selectedCategories);
        mediaDAO.addMedia(media);
        App.showAlert(Alert.AlertType.INFORMATION, "Succès", "Le média '" + title + "' a été créé avec succès.");
        App.setRoot("medias/medias");
    }

    private boolean validateFields() {
        // Réinitialiser les styles
        resetFieldStyles();
        StringBuilder errors = new StringBuilder();
        boolean isValid = true;

        String title = inputTitle.getText();
        if (title == null || title.trim().isEmpty()) {
            errors.append("- Le titre est obligatoire.\n");
            inputTitle.getStyleClass().add("invalid-field");
            isValid = false;
        }

        Author author = authorComboBox.getValue();
        if (author == null) {
            errors.append("- L'auteur est obligatoire.\n");
            authorComboBox.getStyleClass().add("invalid-field");
            isValid = false;
        }

        String edition = inputEdition.getText();
        if (edition == null || edition.trim().isEmpty()) {
            errors.append("- L'édition est obligatoire.\n");
            inputEdition.getStyleClass().add("invalid-field");
            isValid = false;
        }

        String yearText = inputYear.getText();
        if (!ValidationUtil.isInteger(yearText)) {
            errors.append("- L'année de parution doit être un nombre valide.\n");
            inputYear.getStyleClass().add("invalid-field");
            isValid = false;
        }

        List<Category> selectedCategories = categoriesListView.getSelectionModel().getSelectedItems();
        if (selectedCategories.isEmpty()) {
            errors.append("- Au moins une catégorie doit être sélectionnée.\n");
            categoriesListView.getStyleClass().add("invalid-field");
            isValid = false;
        }

        if (!isValid) {
            App.showAlert(Alert.AlertType.ERROR, "Erreurs de validation", errors.toString());
        }
        return isValid;
    }

    private void resetFieldStyles() {
        inputTitle.getStyleClass().remove("invalid-field");
        authorComboBox.getStyleClass().remove("invalid-field");
        inputEdition.getStyleClass().remove("invalid-field");
        inputYear.getStyleClass().remove("invalid-field");
        categoriesListView.getStyleClass().remove("invalid-field");
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("medias/medias");
    }
}
