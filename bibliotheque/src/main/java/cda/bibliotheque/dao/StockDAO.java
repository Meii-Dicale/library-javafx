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

    public List<Stock> findByMediaId(int mediaId) {
        List<Stock> stockList = new ArrayList<>();
        // Assurez-vous que la requête JOIN les tables nécessaires pour construire les objets complets
        String sql = "SELECT s.id as stock_id, s.is_available, " +
                     "m.id as media_id, m.title, " +
                     "ps.id as state_id, ps.state_name " +
                     "FROM Stock s " +
                     "JOIN Media m ON s.media_id = m.id " +
                     "JOIN PhysicalState ps ON s.physic_state_id = ps.id " +
                     "WHERE s.media_id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, mediaId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    // Créer l'objet Media (même si c'est le même pour tous, c'est plus simple)
                    Media media = new Media();
                    media.setId(rs.getInt("media_id"));
                    media.setTitle(rs.getString("title"));

                    // Créer l'objet PhysicalState
                    PhysicalState state = new PhysicalState(rs.getInt("state_id"), rs.getString("state_name"));

                    // Créer l'objet Stock
                    Stock stock = new Stock();
                    stock.setId(rs.getInt("stock_id"));
                    stock.setAvailable(rs.getBoolean("is_available"));
                    stock.setMedia(media);
                    stock.setPhysicalState(state);

                    stockList.add(stock);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du stock pour le média ID " + mediaId + " : " + e.getMessage());
        }
        return stockList;
    }
}