package cda.bibliotheque.controller.stock;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.ReservationDAO;
import cda.bibliotheque.dao.StockDAO;
import cda.bibliotheque.model.Stock;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.io.IOException;

import javafx.beans.property.SimpleIntegerProperty;

public class StockController {

    @FXML
    private TableView<Stock> stockTable;
    @FXML
    private TableColumn<Stock, Integer> colId;
    @FXML
    private TableColumn<Stock, String> colMedia;
    @FXML
    private TableColumn<Stock, String> colState;
    @FXML
    private TableColumn<Stock, String> colAvailability;
    @FXML
    private TableColumn<Stock, Void> colActions;

    @FXML
    private TextField searchField;

    private final ObservableList<Stock> stockList = FXCollections.observableArrayList();
    private final StockDAO stockDAO = new StockDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colMedia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMedia().getTitle()));
        colState.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhysicalState().getStateName()));
        colAvailability.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isAvailable() ? "Disponible" : "Emprunté"));

        colActions.setCellFactory(cellData -> new TableCell<>() {
            private final Button buttonDelete = new Button("Supprimer");
            private final Button buttonEdit = new Button("Modifier");
            private final Button buttonHistory = new Button("Historique");
            private final HBox box = new HBox(10, buttonHistory, buttonEdit, buttonDelete);

            {
                buttonDelete.getStyleClass().add("button-delete");
                buttonDelete.setOnAction(event -> {
                    // La suppression d'un stock peut être complexe (vérifier les réservations, etc.)
                    // Pour l'instant, on ne met pas la logique de suppression directe.
                    System.out.println("Suppression d'un exemplaire non implémentée pour la sécurité.");
                });
                buttonEdit.setOnAction(event -> {
                    Stock stockToEdit = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("/cda/bibliotheque/stock/edit-stock.fxml"));
                        Parent parent = loader.load();
                        EditStockController controller = loader.getController();
                        controller.setStock(stockToEdit);
                        App.getScene().setRoot(parent);
                    } catch (IOException e) {
                        System.err.println("Erreur lors du chargement de la page de modification de stock : " + e.getMessage());
                    }
                });
                buttonHistory.setOnAction(event -> {
                    Stock stock = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("/cda/bibliotheque/stock/stock-history.fxml"));
                        Parent parent = loader.load();
                        StockHistoryController controller = loader.getController();
                        controller.setStockItem(stock); // Passer l'exemplaire au nouveau contrôleur
                        App.getScene().setRoot(parent);
                    } catch (IOException e) {
                        System.err.println("Erreur lors du chargement de l'historique du stock : " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Mettre en évidence les exemplaires non disponibles
        stockTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Stock item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("unavailable-row"); // Toujours retirer l'ancien style
                if (item != null && !empty) {
                    if (!item.isAvailable()) {
                        if (!getStyleClass().contains("unavailable-row")) {
                            getStyleClass().add("unavailable-row");
                        }
                    }
                }
            }
        });

        loadStock();

        // Logique de recherche
        if (searchField != null) {
            FilteredList<Stock> filteredData = new FilteredList<>(stockList, p -> true);
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(stock -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    // Recherche par ID (doit correspondre exactement ou commencer par)
                    if (String.valueOf(stock.getId()).contains(newValue)) {
                        return true;
                    }
                    // Recherche par titre
                    return stock.getMedia().getTitle().toLowerCase().contains(lowerCaseFilter);
                });
            });
            stockTable.setItems(filteredData);
        } else {
            stockTable.setItems(stockList);
        }
    }

    private void loadStock() {
        stockList.setAll(stockDAO.getAll());
        stockTable.setItems(stockList);
    }

    @FXML
    private void switchToCreate() throws IOException {
        App.setRoot("stock/create-stock");
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("primary");
    }
}