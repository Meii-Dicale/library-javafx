package cda.bibliotheque.dao;

import cda.bibliotheque.model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private final Connection connection;

    public CategoryDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Category;";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                categories.add(new Category(resultSet.getInt("id"), resultSet.getString("type_name")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories : " + e.getMessage());
        }
        return categories;
    }

    public void save(Category category) {
        String sql = "INSERT INTO Category (type_name) VALUES (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getTypeName());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la catégorie : " + e.getMessage());
        }
    }

    public void update(Category category) {
        String sql = "UPDATE Category SET type_name = ? WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getTypeName());
            statement.setInt(2, category.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la catégorie : " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Category WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la catégorie : " + e.getMessage());
        }
    }
}