package cda.bibliotheque.controller.media;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.StockDAO;
import cda.bibliotheque.model.Media;
import cda.bibliotheque.model.Stock;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;

public class MediaStockController {

    @FXML
    private TableView<Stock> stockTable;
    @FXML
    private TableColumn<Stock, Integer> colId;
    @FXML
    private TableColumn<Stock, String> colState;
    @FXML
    private TableColumn<Stock, String> colAvailability;
    @FXML
    private Label mediaTitleLabel;

    private Media currentMedia;
    private final StockDAO stockDAO = new StockDAO();

    @FXML
    public void initialize() {
        // Configuration des colonnes du tableau
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colState.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhysicalState().getStateName()));
        colAvailability.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isAvailable() ? "Disponible" : "Emprunté"));
    }

    /**
     * Méthode appelée par le MediaController pour passer le média sélectionné.
     * @param media Le média dont on veut voir le stock.
     */
    public void setMedia(Media media) {
        this.currentMedia = media;
        updateView();
    }

    private void updateView() {
        if (currentMedia != null) {
            mediaTitleLabel.setText("Pour le média : " + currentMedia.getTitle());
            // Charger les exemplaires pour ce média
            stockTable.setItems(FXCollections.observableArrayList(stockDAO.findByMediaId(currentMedia.getId())));
        }
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("medias/medias");
    }
}