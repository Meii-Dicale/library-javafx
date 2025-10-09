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
        String password = inputPassword.getText();
        String phoneNumber = inputPhoneNumber.getText();
        boolean isAdmin = isAdminCheckbox.isSelected();

        // Note: Vous devrez peut-être ajuster le constructeur de votre classe User
        User newUser = new User(userName, password, isAdmin, mail, String.valueOf(phoneNumber));
        usersDAO.addUser(newUser);

        // Revenir à la liste des utilisateurs après la création
        App.setRoot("users/users");
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("users/users");
    }
}