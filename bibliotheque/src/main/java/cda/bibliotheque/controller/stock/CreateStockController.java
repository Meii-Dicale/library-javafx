package cda.bibliotheque.controller.stock;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.MediaDAO;
import cda.bibliotheque.dao.PhysicalStateDAO;
import cda.bibliotheque.dao.StockDAO;
import cda.bibliotheque.model.Media;
import cda.bibliotheque.model.PhysicalState;
import cda.bibliotheque.model.Stock;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.io.IOException;

public class CreateStockController {

    @FXML
    private ComboBox<Media> mediaComboBox;
    @FXML
    private ComboBox<PhysicalState> stateComboBox;

    private final MediaDAO mediaDAO = new MediaDAO();
    private final PhysicalStateDAO physicalStateDAO = new PhysicalStateDAO();
    private final StockDAO stockDAO = new StockDAO();

    @FXML
    public void initialize() {
        // Charger les médias
        mediaComboBox.getItems().setAll(mediaDAO.getAllMedias());
        mediaComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Media media) {
                return media != null ? media.getTitle() : "";
            }
            @Override
            public Media fromString(String string) { return null; }
        });

        // Charger les états physiques
        stateComboBox.getItems().setAll(physicalStateDAO.getAll());
        stateComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PhysicalState state) {
                return state != null ? state.getStateName() : "";
            }
            @Override
            public PhysicalState fromString(String string) { return null; }
        });
    }

    @FXML
    void createStock() throws IOException {
        Media selectedMedia = mediaComboBox.getValue();
        PhysicalState selectedState = stateComboBox.getValue();

        if (selectedMedia != null && selectedState != null) {
            Stock newStock = new Stock();
            newStock.setMedia(selectedMedia);
            newStock.setPhysicalState(selectedState);
            newStock.setAvailable(true); // Un nouvel exemplaire est toujours disponible

            stockDAO.save(newStock);

            App.setRoot("stock/stock");
        } else {
            App.showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez sélectionner un média et un état physique.");
        }
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("stock/stock");
    }
}