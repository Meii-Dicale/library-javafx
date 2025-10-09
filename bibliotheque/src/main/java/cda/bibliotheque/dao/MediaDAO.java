package cda.bibliotheque.dao;

import cda.bibliotheque.model.Author;
import cda.bibliotheque.model.Category;
import cda.bibliotheque.model.Media;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaDAO {

    private Connection connection;

    public MediaDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Media> getAllMedias() {
        List<Media> medias = new ArrayList<>();
        Map<Integer, Media> mediaMap = new HashMap<>();
    
        String sql = "SELECT m.id as media_id, m.title, m.edition, m.year, m.summary, " +
                     "a.id as author_id, a.firstname, a.lastname, " +
                     "c.id as category_id, c.type_name " +
                     "FROM media m " +
                     "JOIN author a ON m.author_id = a.id " +
                     "LEFT JOIN media_category mc ON m.id = mc.media_id " +
                     "LEFT JOIN category c ON mc.category_id = c.id " +
                     "ORDER BY m.id;";
    
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int mediaId = resultSet.getInt("media_id");
                Media media = mediaMap.get(mediaId);
    
                if (media == null) {
                    media = new Media();
                    media.setId(mediaId);
                    media.setTitle(resultSet.getString("title"));
                    media.setEdition(resultSet.getString("edition"));
                    media.setYear(resultSet.getInt("year"));
                    media.setSummary(resultSet.getString("summary"));
    
                    int authorId = resultSet.getInt("author_id");
                    String authorFirstname = resultSet.getString("firstname");
                    String authorLastname = resultSet.getString("lastname");
                    Author author = new Author(authorId, authorFirstname, authorLastname);
                    media.setAuthor(author);
                    media.setCategories(new ArrayList<>()); // Initialiser la liste des catégories
                    mediaMap.put(mediaId, media);
                }
    
                int categoryId = resultSet.getInt("category_id");
                if (categoryId > 0) { // resultSet.getInt retourne 0 si la valeur est NULL
                    Category category = new Category(categoryId, resultSet.getString("type_name"));
                    media.getCategories().add(category);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des médias :" + e);
        }
        return new ArrayList<>(mediaMap.values());
    }

    public void addMedia(Media media) {
        String insertMediaSql = "INSERT INTO media (title, edition, year, summary, author_id) VALUES (?, ?, ?, ?, ?);";
        String insertMediaCategorySql = "INSERT INTO media_category (media_id, category_id) VALUES (?, ?);";
        Savepoint savepoint = null;
    
        try {
            // Désactiver l'auto-commit pour gérer la transaction manuellement
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint("BeforeAddMedia");
    
            // 1. Insérer le média et récupérer son ID généré
            try (PreparedStatement mediaStmt = connection.prepareStatement(insertMediaSql, Statement.RETURN_GENERATED_KEYS)) {
                mediaStmt.setString(1, media.getTitle());
                mediaStmt.setString(2, media.getEdition());
                mediaStmt.setInt(3, media.getYear());
                mediaStmt.setString(4, media.getSummary());
                mediaStmt.setInt(5, media.getAuthor().getId());
                mediaStmt.executeUpdate();
    
                try (ResultSet generatedKeys = mediaStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        media.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("La création du média a échoué, aucun ID obtenu.");
                    }
                }
            }
    
            // 2. Insérer les associations dans media_category
            try (PreparedStatement mediaCategoryStmt = connection.prepareStatement(insertMediaCategorySql)) {
                for (Category category : media.getCategories()) {
                    mediaCategoryStmt.setInt(1, media.getId());
                    mediaCategoryStmt.setInt(2, category.getId());
                    mediaCategoryStmt.addBatch();
                }
                mediaCategoryStmt.executeBatch();
            }
    
            connection.commit(); // Valider la transaction
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du média et de ses catégories : " + e.getMessage());
            try {
                if (savepoint != null) {
                    connection.rollback(savepoint);
                    System.err.println("Transaction annulée.");
                }
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback de la transaction : " + ex.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true); // Rétablir l'auto-commit
            } catch (SQLException ex) {
                System.err.println("Erreur lors de la réactivation de l'auto-commit : " + ex.getMessage());
            }
        }
    }

    public void updateMedia(Media media) {
        String sql = "UPDATE media SET title = ?, edition = ?, year = ?, summary = ?, author_id = ? WHERE id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, media.getTitle());
            preparedStatement.setString(2, media.getEdition());
            preparedStatement.setInt(3, media.getYear());
            preparedStatement.setString(4, media.getSummary());
            preparedStatement.setInt(5, media.getAuthor().getId());
            preparedStatement.setInt(6, media.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du média :" + e);
        }
    }

    public void deleteMedia(int id) {
        String sql = "DELETE FROM media WHERE id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du média :" + e);
        }
    }
}
