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
import java.util.Optional;

public class AccountController {

    @FXML
    private TextField inputUserName;
    @FXML
    private TextField inputMail;
    @FXML
    private TextField inputPhoneNumber;
    @FXML
    private PasswordField inputCurrentPassword;
    @FXML
    private PasswordField inputNewPassword;
    @FXML
    private PasswordField inputConfirmPassword;

    private User currentUser;
    private final UsersDAO usersDAO = new UsersDAO();

    @FXML
    public void initialize() {
        currentUser = App.getLoggedInUser();
        if (currentUser != null) {
            inputUserName.setText(currentUser.getUser_name());
            inputMail.setText(currentUser.getMail());
            inputPhoneNumber.setText(currentUser.getPhone_number());
        }
    }

    @FXML
    void handleUpdateInfo() {
        String userName = inputUserName.getText();
        String mail = inputMail.getText();
        String phoneNumber = inputPhoneNumber.getText();

        if (userName.isEmpty() || mail.isEmpty() || phoneNumber.isEmpty()) {
            App.showAlert(Alert.AlertType.WARNING, "Champs requis", "Tous les champs d'information sont obligatoires.");
            return;
        }

        if (!ValidationUtil.isEmailValid(mail)) {
            App.showAlert(Alert.AlertType.WARNING, "Email invalide", "Veuillez saisir une adresse email valide.");
            return;
        }

        // Vérifier si le nouvel email est déjà pris par un autre utilisateur
        if (!mail.equals(currentUser.getMail())) {
            Optional<User> userWithSameEmail = usersDAO.findByEmail(mail);
            if (userWithSameEmail.isPresent()) {
                App.showAlert(Alert.AlertType.ERROR, "Email déjà utilisé", "Cette adresse email est déjà associée à un autre compte.");
                return;
            }
        }

        currentUser.setUser_name(userName);
        currentUser.setMail(mail);
        currentUser.setPhone_number(phoneNumber);

        usersDAO.updateUser(currentUser);
        App.showAlert(Alert.AlertType.INFORMATION, "Succès", "Vos informations ont été mises à jour.");
    }

    @FXML
    void handleChangePassword() {
        String currentPassword = inputCurrentPassword.getText();
        String newPassword = inputNewPassword.getText();
        String confirmPassword = inputConfirmPassword.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            App.showAlert(Alert.AlertType.WARNING, "Champs requis", "Tous les champs de mot de passe sont obligatoires.");
            return;
        }

        // 1. Vérifier si le mot de passe actuel est correct
        if (!currentUser.getPassword().equals(hashPassword(currentPassword))) {
            App.showAlert(Alert.AlertType.ERROR, "Erreur", "Le mot de passe actuel est incorrect.");
            return;
        }

        // 2. Valider la force du nouveau mot de passe
        if (!ValidationUtil.isPasswordStrong(newPassword)) {
            App.showAlert(Alert.AlertType.WARNING, "Nouveau mot de passe faible", ValidationUtil.getPasswordPolicy());
            return;
        }

        // 2. Vérifier si les nouveaux mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            App.showAlert(Alert.AlertType.ERROR, "Erreur", "Les nouveaux mots de passe ne correspondent pas.");
            return;
        }

        // 3. Mettre à jour le mot de passe
        String hashedNewPassword = hashPassword(newPassword);
        usersDAO.updatePassword(currentUser.getId(), hashedNewPassword);

        // Mettre à jour l'objet utilisateur en mémoire
        currentUser.setPassword(hashedNewPassword);

        App.showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre mot de passe a été changé.");
        inputCurrentPassword.clear();
        inputNewPassword.clear();
        inputConfirmPassword.clear();
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("user-dashboard");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de configuration de la sécurité.", e);
        }
    }
}