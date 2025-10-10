package cda.bibliotheque.controller;

import cda.bibliotheque.App;
import cda.bibliotheque.controller.category.ValidationUtil;
import cda.bibliotheque.dao.UsersDAO;
import cda.bibliotheque.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CreateAccountController {

    @FXML
    private TextField inputUserName;

    @FXML
    private TextField inputMail;

    @FXML
    private TextField inputPhoneNumber;

    @FXML
    private PasswordField inputPassword;

    private final UsersDAO usersDAO = new UsersDAO();

    @FXML
    void createAccount() throws IOException {
        String userName = inputUserName.getText();
        String mail = inputMail.getText();
        String password = inputPassword.getText();
        String phoneNumber = inputPhoneNumber.getText();

        // Validation simple
        if (userName.isEmpty() || mail.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
            App.showAlert(Alert.AlertType.WARNING, "Champs requis", "Tous les champs sont obligatoires.");
            return;
        }

        if (!ValidationUtil.isEmailValid(mail)) {
            App.showAlert(Alert.AlertType.WARNING, "Email invalide", "Veuillez saisir une adresse email valide.");
            return;
        }

        // Hachage du mot de passe
        String hashedPassword = hashPassword(inputPassword.getText());

        // Création de l'utilisateur avec isAdmin à false par défaut
        User newUser = new User(userName, hashedPassword, false, mail, phoneNumber);
        usersDAO.addUser(newUser);

        App.showAlert(Alert.AlertType.INFORMATION, "Compte créé", "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter.");

        // Revenir à la page de connexion
        goBackToLogin();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithme de hachage non trouvé : " + e.getMessage());
            throw new RuntimeException("Erreur de configuration de la sécurité.", e);
        }
    }

    @FXML
    void goBackToLogin() throws IOException {
        App.setRoot("login");
    }
}