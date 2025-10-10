package cda.bibliotheque.controller;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.UsersDAO;
import cda.bibliotheque.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UsersDAO usersDAO = new UsersDAO();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("L'email et le mot de passe sont requis.");
            return;
        }

        Optional<User> userOpt = usersDAO.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String hashedPassword = hashPassword(password);

            if (user.getPassword().equals(hashedPassword)) {
                App.setLoggedInUser(user);
                try {
                    if (user.getIs_admin()) {
                        App.setRoot("primary");
                    } else {
                        App.setRoot("user-dashboard");
                    }
                } catch (IOException e) {
                    errorLabel.setText("Erreur de chargement de la page.");
                }
            } else {
                System.err.println("Login failed: Password mismatch for user: " + email);
                System.err.println("  - Entered (hashed): " + hashedPassword);
                System.err.println("  - Stored (hashed):  " + user.getPassword());
                errorLabel.setText("Email ou mot de passe incorrect.");
            }
        } else {
            System.err.println("Login failed: No user found with email: " + email);
            errorLabel.setText("Email ou mot de passe incorrect.");
        }
    }

    @FXML
    private void handleCreateAccount() throws IOException {
        App.setRoot("create-account");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithme de hachage non trouvé : " + e.getMessage());
            // In a real application, logging this error is crucial.
            throw new RuntimeException("Erreur de configuration de la sécurité.", e);
        }
    }
}