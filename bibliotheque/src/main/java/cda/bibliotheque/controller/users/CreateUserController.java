package cda.bibliotheque.controller.users;

import cda.bibliotheque.App;
import cda.bibliotheque.controller.category.ValidationUtil;
import cda.bibliotheque.dao.UsersDAO;
import cda.bibliotheque.model.User;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CreateUserController {

    @FXML
    private TextField inputUserName;

    @FXML
    private TextField inputMail;

    @FXML
    private TextField inputPhoneNumber;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private CheckBox isAdminCheckbox;

    private final UsersDAO usersDAO = new UsersDAO();

    @FXML
    void createUser(ActionEvent event) throws IOException {
        String userName = inputUserName.getText();
        String mail = inputMail.getText().trim();
        String passwordText = inputPassword.getText();

        // Validation des champs
        if (userName.isEmpty() || mail.isEmpty() || passwordText.isEmpty()) {
            App.showAlert(Alert.AlertType.WARNING, "Champs requis", "Le nom, l'email et le mot de passe sont obligatoires.");
            return;
        }
        if (!ValidationUtil.isEmailValid(mail)) {
            App.showAlert(Alert.AlertType.WARNING, "Email invalide", "Veuillez saisir une adresse email valide.");
            return;
        }

        // Vérifier si l'email existe déjà
        if (usersDAO.findByEmail(mail).isPresent()) {
            App.showAlert(Alert.AlertType.ERROR, "Email déjà utilisé", "Cette adresse email est déjà associée à un compte.");
            return;
        }

        // Valider la force du mot de passe
        if (!ValidationUtil.isPasswordStrong(passwordText)) {
            App.showAlert(Alert.AlertType.WARNING, "Mot de passe faible", ValidationUtil.getPasswordPolicy());
            return;
        }

        String password = hashPassword(passwordText);
        String phoneNumber = inputPhoneNumber.getText();
        boolean isAdmin = isAdminCheckbox.isSelected();

        // Le mot de passe est maintenant haché
        User newUser = new User(userName, password, isAdmin, mail, phoneNumber);
        usersDAO.addUser(newUser);

        // Revenir à la liste des utilisateurs après la création
        App.setRoot("users/users");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Dans une application réelle, logguer cette erreur est crucial
            System.err.println("Algorithme de hachage non trouvé : " + e.getMessage());
            // Gérer l'erreur de manière appropriée, peut-être en empêchant la création de l'utilisateur
            throw new RuntimeException("Erreur de configuration de la sécurité.", e);
        }
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("users/users");
    }
}