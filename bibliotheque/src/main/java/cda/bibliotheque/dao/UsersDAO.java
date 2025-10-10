package cda.bibliotheque.dao;

import cda.bibliotheque.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsersDAO {
    private Connection connection;
    public UsersDAO(){
        this.connection = DatabaseConnection.getConnection();
    }

    public List<User> getAllUsers(){
        List<User> users = new ArrayList<>();

        String sql = "SELECT id, user_name, is_admin, mail, phone_number FROM Users;";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUser_name(resultSet.getString("user_name"));
                user.setIs_admin(resultSet.getBoolean("is_admin"));
                user.setMail(resultSet.getString("mail"));
                user.setPhone_number(resultSet.getString("phone_number"));
                users.add(user);
            }
        }
        catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e);
        }
        return users;
    }

    public List<User> getNonAdminUsers(){
        List<User> users = new ArrayList<>();

        String sql = "SELECT id, user_name, is_admin, mail, phone_number FROM Users WHERE is_admin = false;";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUser_name(resultSet.getString("user_name"));
                user.setIs_admin(resultSet.getBoolean("is_admin"));
                user.setMail(resultSet.getString("mail"));
                user.setPhone_number(resultSet.getString("phone_number"));
                users.add(user);
            }
        }
        catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs non-admin : " + e);
        }
        return users;
    }


    public void addUser(User user){
        String sql = "INSERT INTO Users (user_name, password, is_admin, mail, phone_number) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUser_name());
            preparedStatement.setString(2, user.getPassword()); 
            preparedStatement.setBoolean(3, user.getIs_admin());
            preparedStatement.setString(4, user.getMail());
            preparedStatement.setString(5, user.getPhone_number());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur : " + e);
        }
    }

    public void updateUser(User user){
        String sql = "UPDATE Users SET user_name = ?, is_admin = ?, mail = ?, phone_number = ? WHERE id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUser_name());
            preparedStatement.setBoolean(2, user.getIs_admin());
            preparedStatement.setString(3, user.getMail());
            preparedStatement.setString(4, user.getPhone_number());
            preparedStatement.setInt(5, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'update de l'utilisateur : " + e);
        }
    }

    public void updatePassword(int userId, String newPassword) {
        String sql = "UPDATE Users SET password = ? WHERE id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe : " + e);
        }
    }

    public void deleteUser(int id){
        String sql = "DELETE FROM Users WHERE id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur :" + e);
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, user_name, password, is_admin, mail, phone_number FROM Users WHERE mail = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUser_name(resultSet.getString("user_name"));
                    user.setPassword(resultSet.getString("password"));
                    user.setIs_admin(resultSet.getBoolean("is_admin"));
                    user.setMail(resultSet.getString("mail"));
                    user.setPhone_number(resultSet.getString("phone_number"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur par email : " + e);
        }
        return Optional.empty();
    }


}
