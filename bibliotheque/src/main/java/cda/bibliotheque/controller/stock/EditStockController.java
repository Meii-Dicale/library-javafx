package cda.bibliotheque.controller.stock;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.PhysicalStateDAO;
import cda.bibliotheque.dao.StockDAO;
import cda.bibliotheque.model.PhysicalState;
import cda.bibliotheque.model.Stock;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.io.IOException;

public class EditStockController {

    @FXML
    private TextField mediaField;
    @FXML
    private ComboBox<PhysicalState> stateComboBox;
    @FXML
    private CheckBox availabilityCheckBox;

    private final ObjectProperty<Stock> stock = new SimpleObjectProperty<>();
    private final StockDAO stockDAO = new StockDAO();
    private final PhysicalStateDAO physicalStateDAO = new PhysicalStateDAO();

    public void setStock(Stock stock) {
        this.stock.set(stock);
    }

    @FXML
    public void initialize() {
        // Load physical states into the ComboBox
        stateComboBox.getItems().setAll(physicalStateDAO.getAll());
        stateComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PhysicalState state) {
                return state != null ? state.getStateName() : "";
            }
            @Override
            public PhysicalState fromString(String string) { return null; }
        });

        // Listen for the stock item to be set and populate the form
        stock.addListener((obs, oldStock, newStock) -> {
            if (newStock != null) {
                mediaField.setText(newStock.getMedia().getTitle());
                stateComboBox.setValue(newStock.getPhysicalState());
                availabilityCheckBox.setSelected(newStock.isAvailable());
            }
        });
    }

    @FXML
    void updateStock() throws IOException {
        Stock stockToUpdate = stock.get();
        PhysicalState selectedState = stateComboBox.getValue();

        if (stockToUpdate != null && selectedState != null) {
            stockToUpdate.setPhysicalState(selectedState);
            stockToUpdate.setAvailable(availabilityCheckBox.isSelected());

            stockDAO.update(stockToUpdate);

            App.setRoot("stock/stock");
        } else {
            System.err.println("Impossible de mettre à jour, des informations sont manquantes.");
        }
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("stock/stock");
    }
}