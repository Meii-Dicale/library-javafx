package cda.bibliotheque.controller.users;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.UsersDAO;
import cda.bibliotheque.model.User;
import javafx.event.ActionEvent;
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
        String mail = inputMail.getText();
        String password = hashPassword(inputPassword.getText());
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