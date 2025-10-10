package cda.bibliotheque.controller.media;

import cda.bibliotheque.App;
import cda.bibliotheque.model.Category;
import cda.bibliotheque.dao.CategoryDAO;
import cda.bibliotheque.dao.MediaDAO;
import cda.bibliotheque.model.Media;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MediaController {

    @FXML
    private TableView<Media> tableMedias;
    @FXML
    private TableColumn<Media, String> colTitle;
    @FXML
    private TableColumn<Media, String> colAuthor;
    @FXML
    private TableColumn<Media, String> colEdition;
    @FXML
    private TableColumn<Media, String> colYear;
    @FXML
    private TableColumn<Media, String> colCategories;
    @FXML
    private TableColumn<Media, Void> colActions;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Category> categoryFilterComboBox;

    private final ObservableList<Media> mediaList = FXCollections.observableArrayList();
    private final MediaDAO mediaDAO = new MediaDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private FilteredList<Media> filteredData;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        colAuthor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor().getFirstname() + " " + cellData.getValue().getAuthor().getLastname()));
        colEdition.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEdition()));
        colYear.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getYear())));
        colCategories.setCellValueFactory(cellData -> {
            List<Category> categories = cellData.getValue().getCategories();
            if (categories == null || categories.isEmpty()) {
                return new SimpleStringProperty("");
            }
            String categoryNames = categories.stream()
                    .map(Category::getTypeName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(categoryNames);
        });

        // Ajout des boutons d'action
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button viewStockButton = new Button("Voir le stock");
            private final HBox pane = new HBox(10, viewStockButton, deleteButton);

            {
                deleteButton.getStyleClass().add("button-delete");
                deleteButton.setOnAction(event -> {
                    Media media = getTableView().getItems().get(getIndex());
                    if (App.showConfirmationDialog("Confirmation", "Voulez-vous vraiment supprimer le média '" + media.getTitle() + "' ?")) {
                        mediaDAO.deleteMedia(media.getId());
                        loadMedias();
                    }
                });

                viewStockButton.setOnAction(event -> {
                    Media media = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("/cda/bibliotheque/medias/media-stock.fxml"));
                        Parent parent = loader.load();
                        MediaStockController controller = loader.getController();
                        controller.setMedia(media);
                        App.getScene().setRoot(parent);
                    } catch (IOException e) {
                        System.err.println("Erreur lors du chargement de la page de stock du média : " + e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        // C'est ici que nous ajoutons l'infobulle pour le résumé
        tableMedias.setRowFactory(tv -> new TableRow<>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(Media item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty || item.getSummary() == null || item.getSummary().isEmpty()) {
                    setTooltip(null);
                } else {
                    tooltip.setText(item.getSummary());
                    tooltip.setWrapText(true); // Permet au texte de passer à la ligne
                    tooltip.setMaxWidth(400);  // Limite la largeur de l'infobulle
                    setTooltip(tooltip);
                }
            }
        });

        loadMedias();

        // Configurer le filtre par catégorie
        setupCategoryFilter();

        // Logique de recherche
        filteredData = new FilteredList<>(mediaList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());
        categoryFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        tableMedias.setItems(filteredData);
    }

    private void setupCategoryFilter() {
        // Charger les catégories et ajouter une option "Toutes"
        List<Category> categories = new ArrayList<>(categoryDAO.getAll());
        Category allCategories = new Category(0, "Toutes les catégories");
        categories.add(0, allCategories);
        categoryFilterComboBox.setItems(FXCollections.observableArrayList(categories));

        // Configurer l'affichage des noms de catégories
        categoryFilterComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category == null ? "" : category.getTypeName();
            }
            @Override
            public Category fromString(String string) {
                return null; // Non nécessaire pour un ComboBox non éditable
            }
        });

        // Sélectionner "Toutes les catégories" par défaut
        categoryFilterComboBox.setValue(allCategories);
    }

    private void updateFilter() {
        String searchText = searchField.getText();
        Category selectedCategory = categoryFilterComboBox.getValue();

        filteredData.setPredicate(media -> {
            // Filtre par texte
            boolean textMatch = true;
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                textMatch = media.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                            (media.getAuthor().getFirstname() + " " + media.getAuthor().getLastname()).toLowerCase().contains(lowerCaseFilter);
            }

            // Filtre par catégorie
            boolean categoryMatch = true;
            if (selectedCategory != null && selectedCategory.getId() != 0) { // 0 est l'ID pour "Toutes les catégories"
                categoryMatch = media.getCategories().stream().anyMatch(c -> c.getId() == selectedCategory.getId());
            }

            return textMatch && categoryMatch;
        });
    }

    private void loadMedias() {
        mediaList.setAll(mediaDAO.getAllMedias());
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    void switchToCreateMedia() throws IOException {
        App.setRoot("medias/create-media");
    }
}