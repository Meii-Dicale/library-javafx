package cda.bibliotheque.dao;

import cda.bibliotheque.model.Media;
import cda.bibliotheque.model.PhysicalState;
import cda.bibliotheque.model.Stock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    private final Connection connection;

    public StockDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Stock> getAll() {
        List<Stock> stockList = new ArrayList<>();
        // Jointure pour récupérer les informations complètes
        String sql = "SELECT s.id, s.is_available, s.media_id, m.title, s.physic_state_id, ps.state_name " +
                     "FROM Stock s " +
                     "JOIN Media m ON s.media_id = m.id " +
                     "JOIN PhysicalState ps ON s.physic_state_id = ps.id;";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Stock stock = new Stock();
                stock.setId(rs.getInt("id"));
                stock.setAvailable(rs.getBoolean("is_available"));

                Media media = new Media();
                media.setId(rs.getInt("media_id"));
                media.setTitle(rs.getString("title"));
                stock.setMedia(media);

                PhysicalState state = new PhysicalState();
                state.setId(rs.getInt("physic_state_id"));
                state.setStateName(rs.getString("state_name"));
                stock.setPhysicalState(state);

                stockList.add(stock);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du stock : " + e.getMessage());
        }
        return stockList;
    }

    public void save(Stock stock) {
        String sql = "INSERT INTO Stock (is_available, media_id, physic_state_id) VALUES (?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, stock.isAvailable());
            statement.setInt(2, stock.getMedia().getId());
            statement.setInt(3, stock.getPhysicalState().getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout au stock : " + e.getMessage());
        }
    }

    public void update(Stock stock) {
        String sql = "UPDATE Stock SET is_available = ?, media_id = ?, physic_state_id = ? WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, stock.isAvailable());
            statement.setInt(2, stock.getMedia().getId());
            statement.setInt(3, stock.getPhysicalState().getId());
            statement.setInt(4, stock.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du stock : " + e.getMessage());
        }
    }
}