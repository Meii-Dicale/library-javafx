package cda.bibliotheque.dao;

import cda.bibliotheque.model.Author;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {

    private Connection connection;

    public AuthorDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT id, firstname, lastname FROM author;";

        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fisrtname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                Author author = new Author(id, fisrtname, lastname);
                authors.add(author);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des auteurs : " + e.getMessage());
        }

        return authors;
    }

    public Author getAuthorById(int authorId) {
        String sql = "SELECT id, firstname, lastname FROM author WHERE id = ?;";
        Author author = null;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, authorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String firstname = resultSet.getString("firstname");
                    String lastname = resultSet.getString("lastname");
                    author = new Author(id, firstname, lastname);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'auteur : " + e.getMessage());
        }
        return author;
    }

    public void addAuthor(Author author) {
        String sql = "INSERT INTO author (firstname, lastname) VALUES (?, ?);";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, author.getFirstname());
            statement.setString(2, author.getLastname());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'auteur : " + e.getMessage());
        }
    }

    public void updateAuthor(Author author) {
        String sql = "UPDATE author SET firstname = ?, lastname = ? WHERE id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, author.getFirstname());
            statement.setString(2, author.getLastname());
            statement.setInt(3, author.getId());
            int row = statement.executeUpdate();
            if (row > 0) {
                System.out.println("L'auteur a été modifié avec succès.");
            } else {
                System.out.println("Aucun auteur n'a été trouvé.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'auteur : " + e.getMessage());
        }
    }
    public void deleteAuthor(int id) {
        String sql = "DELETE FROM author WHERE id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int row = statement.executeUpdate();
            if (row > 0) {
                System.out.println("L'auteur a été supprimé");
            } else {
                System.out.println("Aucun auteur n'a été trouvé");
            }
        }
        catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'auteur : " + e.getMessage());
        }
    }
}
