package cda.bibliotheque.dao;

import cda.bibliotheque.model.Media;
import cda.bibliotheque.model.Reservation;
import cda.bibliotheque.model.Stock;
import cda.bibliotheque.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationDAO {

    private final Connection connection;

    public ReservationDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.id as reservation_id, r.started_at_date, r.ended_at_date, r.is_ended, " +
                     "u.id as user_id, u.user_name, u.is_admin, u.mail, u.phone_number, " +
                     "s.id as stock_id, s.is_available, " +
                     "m.id as media_id, m.title " +
                     "FROM Reservation r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "JOIN stock s ON r.stock_id = s.id " +
                     "JOIN media m ON s.media_id = m.id " +
                     "ORDER BY r.started_at_date DESC;";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("reservation_id"));
                reservation.setStartedAtDate(rs.getDate("started_at_date").toLocalDate());
                Date endedAt = rs.getDate("ended_at_date");
                if (endedAt != null) {
                    reservation.setEndedAtDate(endedAt.toLocalDate());
                }
                reservation.setEnded(rs.getBoolean("is_ended"));
    
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getBoolean("is_admin"),
                        rs.getString("mail"),
                        rs.getString("phone_number")
                );
                reservation.setUser(user);
    
                Media media = new Media();
                media.setId(rs.getInt("media_id"));
                media.setTitle(rs.getString("title"));
    
                Stock stock = new Stock();
                stock.setId(rs.getInt("stock_id"));
                stock.setAvailable(rs.getBoolean("is_available"));
                stock.setMedia(media);
                reservation.setStock(stock);
    
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réservations : " + e.getMessage());
        }
        return reservations;
    }

    public void save(Reservation reservation) {
        String insertReservationSql = "INSERT INTO Reservation (started_at_date, ended_at_date, is_ended, user_id, stock_id) VALUES (?, ?, ?, ?, ?);";
        String updateStockSql = "UPDATE stock SET is_available = false WHERE id = ? AND is_available = true;";
        Savepoint savepoint = null;

        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint("BeforeSaveReservation");

            // 1. Insérer la réservation
            // 2. Mettre à jour la disponibilité du stock
            try (PreparedStatement stockStmt = connection.prepareStatement(updateStockSql)) {
                stockStmt.setInt(1, reservation.getStock().getId());
                int rowsAffected = stockStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("L'exemplaire n'est pas disponible pour la réservation.");
                }
            }

            // 3. Insérer la réservation
            try (PreparedStatement reservationStmt = connection.prepareStatement(insertReservationSql)) {
                reservationStmt.setDate(1, Date.valueOf(reservation.getStartedAtDate()));
                reservationStmt.setDate(2, reservation.getEndedAtDate() != null ? Date.valueOf(reservation.getEndedAtDate()) : null);
                reservationStmt.setBoolean(3, reservation.isEnded());
                reservationStmt.setInt(4, reservation.getUser().getId());
                reservationStmt.setInt(5, reservation.getStock().getId());
                reservationStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la réservation : " + e.getMessage());
            try {
                if (savepoint != null) {
                    connection.rollback(savepoint);
                    System.err.println("Transaction de réservation annulée.");
                }
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback de la transaction de réservation : " + ex.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Erreur lors de la réactivation de l'auto-commit : " + ex.getMessage());
            }
        }
    }

    public void endReservation(Reservation reservation) {
        String updateReservationSql = "UPDATE Reservation SET is_ended = true, ended_at_date = ? WHERE id = ?;";
        String updateStockSql = "UPDATE stock SET is_available = true WHERE id = ?;";
        Savepoint savepoint = null;

        try {
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint("BeforeEndReservation");

            // 1. Mettre à jour la réservation
            try (PreparedStatement reservationStmt = connection.prepareStatement(updateReservationSql)) {
                reservationStmt.setDate(1, Date.valueOf(java.time.LocalDate.now())); // Utiliser la date actuelle pour la fin
                reservationStmt.setInt(2, reservation.getId());
                reservationStmt.executeUpdate();
            }

            // 2. Rendre le stock de nouveau disponible
            try (PreparedStatement stockStmt = connection.prepareStatement(updateStockSql)) {
                stockStmt.setInt(1, reservation.getStock().getId());
                stockStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la clôture de la réservation : " + e.getMessage());
            try {
                if (savepoint != null) {
                    connection.rollback(savepoint);
                    System.err.println("Transaction de clôture de réservation annulée.");
                }
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback : " + ex.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Erreur lors de la réactivation de l'auto-commit : " + ex.getMessage());
            }
        }
    }

    public List<Reservation> getReservationsByUserId(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.id as reservation_id, r.started_at_date, r.ended_at_date, r.is_ended, " +
                     "u.id as user_id, u.user_name, u.is_admin, u.mail, u.phone_number, " +
                     "s.id as stock_id, s.is_available, " +
                     "m.id as media_id, m.title " +
                     "FROM Reservation r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "JOIN stock s ON r.stock_id = s.id " +
                     "JOIN media m ON s.media_id = m.id " +
                     "WHERE r.user_id = ? " +
                     "ORDER BY r.started_at_date DESC;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId(rs.getInt("reservation_id"));
                    reservation.setStartedAtDate(rs.getDate("started_at_date").toLocalDate());
                    Date endedAtDate = rs.getDate("ended_at_date");
                    reservation.setEndedAtDate(endedAtDate != null ? endedAtDate.toLocalDate() : null);
                    reservation.setEnded(rs.getBoolean("is_ended"));

                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("user_name"),
                            rs.getBoolean("is_admin"),
                            rs.getString("mail"),
                            rs.getString("phone_number")
                    );
                    reservation.setUser(user);

                    Media media = new Media();
                    media.setId(rs.getInt("media_id"));
                    media.setTitle(rs.getString("title"));

                    Stock stock = new Stock();
                    stock.setId(rs.getInt("stock_id"));
                    stock.setAvailable(rs.getBoolean("is_available"));
                    stock.setMedia(media);
                    reservation.setStock(stock);

                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réservations pour l'utilisateur " + userId + " : " + e.getMessage());
        }
        return reservations;
    }

    public List<Reservation> findByStockId(int stockId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.id as reservation_id, r.started_at_date, r.ended_at_date, r.is_ended, " +
                     "u.id as user_id, u.user_name, u.is_admin, u.mail, u.phone_number, " +
                     "s.id as stock_id, s.is_available, " +
                     "m.id as media_id, m.title " +
                     "FROM Reservation r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "JOIN stock s ON r.stock_id = s.id " +
                     "JOIN media m ON s.media_id = m.id " +
                     "WHERE r.stock_id = ? " +
                     "ORDER BY r.started_at_date DESC;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, stockId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId(rs.getInt("reservation_id"));
                    reservation.setStartedAtDate(rs.getDate("started_at_date").toLocalDate());
                    Date endedAtDate = rs.getDate("ended_at_date");
                    reservation.setEndedAtDate(endedAtDate != null ? endedAtDate.toLocalDate() : null);
                    reservation.setEnded(rs.getBoolean("is_ended"));

                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("user_name"),
                            rs.getBoolean("is_admin"),
                            rs.getString("mail"),
                            rs.getString("phone_number")
                    );
                    reservation.setUser(user);

                    Media media = new Media();
                    media.setId(rs.getInt("media_id"));
                    media.setTitle(rs.getString("title"));

                    Stock stock = new Stock();
                    stock.setId(rs.getInt("stock_id"));
                    stock.setMedia(media);
                    reservation.setStock(stock);

                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'historique pour l'exemplaire " + stockId + " : " + e.getMessage());
        }
        return reservations;
    }
}