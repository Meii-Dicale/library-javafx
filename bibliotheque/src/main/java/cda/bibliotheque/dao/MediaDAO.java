package cda.bibliotheque.dao;

import cda.bibliotheque.model.Media;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MediaDAO {

    private Connection connection;

    public MediaDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Media> getAllMedias() {
        List<Media> medias = new ArrayList<>();
        String sql = "SELECT id, title, edition, year, summary, author_id FROM medias;";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Media media = new Media();
                media.setId(resultSet.getInt("id"));
                media.setTitle(resultSet.getString("title"));
                media.setEdition(resultSet.getString("edition"));
                media.setYear(resultSet.getInt("year"));
                media.setSummary(resultSet.getString("summary"));
                media.setAuthor_id(resultSet.getInt("author_id"));
                medias.add(media);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des médias :" + e);
        }
        return medias;
    }

    public void addMedia(Media media) {
        String sql = "INSERT INTO medias (title, edition, year, summary, author_id) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, media.getTitle());
            preparedStatement.setString(2, media.getEdition());
            preparedStatement.setInt(3, media.getYear());
            preparedStatement.setString(4, media.getSummary());
            preparedStatement.setInt(5, media.getAuthor_id());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du média :" + e);
        }
    }

    public void updateMedia(Media media) {
        String sql = "UPDATE medias SET title = ?, edition = ?, year = ?, summary = ?, author_id = ? WHERE id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, media.getTitle());
            preparedStatement.setString(2, media.getEdition());
            preparedStatement.setInt(3, media.getYear());
            preparedStatement.setString(4, media.getSummary());
            preparedStatement.setInt(5, media.getAuthor_id());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du média :" + e);
        }
    }

    public void deleteMedia(int id) {
        String sql = "DELETE FROM medias WHERE id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du média :" + e);
        }
    }
}
