package cda.bibliotheque.controller.stock;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.ReservationDAO;
import cda.bibliotheque.model.Reservation;
import cda.bibliotheque.model.Stock;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StockHistoryController {

    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableColumn<Reservation, String> colUser;
    @FXML
    private TableColumn<Reservation, String> colStartDate;
    @FXML
    private TableColumn<Reservation, String> colEndDate;
    @FXML
    private TableColumn<Reservation, String> colStatus;
    @FXML
    private Label stockItemLabel;

    private Stock currentStockItem;
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        colUser.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getUser_name()));
        colStartDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartedAtDate().format(formatter)));
        colEndDate.setCellValueFactory(cellData -> {
            LocalDate endDate = cellData.getValue().getEndedAtDate();
            return new SimpleStringProperty(endDate != null ? endDate.format(formatter) : "N/A");
        });
        colStatus.setCellValueFactory(cellData -> {
            if (cellData.getValue().isEnded()) {
                return new SimpleStringProperty("Terminée");
            }
            if (cellData.getValue().getEndedAtDate() != null && cellData.getValue().getEndedAtDate().isBefore(LocalDate.now())) {
                return new SimpleStringProperty("En retard");
            }
            return new SimpleStringProperty("En cours");
        });
    }

    /**
     * Méthode appelée par le StockController pour passer l'exemplaire sélectionné.
     * @param stockItem L'exemplaire dont on veut voir l'historique.
     */
    public void setStockItem(Stock stockItem) {
        this.currentStockItem = stockItem;
        updateView();
    }

    private void updateView() {
        if (currentStockItem != null) {
            stockItemLabel.setText("Pour l'exemplaire ID: " + currentStockItem.getId() + " - " + currentStockItem.getMedia().getTitle());
            reservationsTable.setItems(FXCollections.observableArrayList(reservationDAO.findByStockId(currentStockItem.getId())));
        }
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("stock/stock");
    }
}