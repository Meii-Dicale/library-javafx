package cda.bibliotheque.dao;

import cda.bibliotheque.model.PhysicalState;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PhysicalStateDAO {

    private final Connection connection;

    public PhysicalStateDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<PhysicalState> getAll() {
        List<PhysicalState> states = new ArrayList<>();
        String sql = "SELECT * FROM PhysicalState;";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                states.add(new PhysicalState(resultSet.getInt("id"), resultSet.getString("state_name")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des états physiques : " + e.getMessage());
        }
        return states;
    }

    public void save(PhysicalState state) {
        String sql = "INSERT INTO PhysicalState (state_name) VALUES (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, state.getStateName());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'état physique : " + e.getMessage());
        }
    }

    public void update(PhysicalState state) {
        String sql = "UPDATE PhysicalState SET state_name = ? WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, state.getStateName());
            statement.setInt(2, state.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'état physique : " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM PhysicalState WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'état physique : " + e.getMessage());
        }
    }
}