package cda.bibliotheque.controller.users;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.UsersDAO;
import cda.bibliotheque.model.User;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.IOException;

public class EditUserController {

    private final ObjectProperty<User> user = new SimpleObjectProperty<>();
    private final UsersDAO usersDAO = new UsersDAO();

    @FXML
    private TextField inputUserName;

    @FXML
    private TextField inputMail;

    @FXML
    private TextField inputPhoneNumber;

    @FXML
    private CheckBox isAdminCheckbox;

    public void setUser(User user) {
        this.user.set(user);
    }

    @FXML
    public void initialize() {
        user.addListener((obs, oldUser, newUser) -> {
            if (newUser != null) {
                inputUserName.setText(newUser.getUser_name());
                inputMail.setText(newUser.getMail());
                inputPhoneNumber.setText(String.valueOf(newUser.getPhone_number()));
                isAdminCheckbox.setSelected(newUser.getIs_admin());
            }
        });
    }

    @FXML
    void submit(ActionEvent event) throws IOException {
        User userToUpdate = user.get();
        userToUpdate.setUser_name(inputUserName.getText());
        userToUpdate.setMail(inputMail.getText());
        userToUpdate.setPhone_number(Integer.parseInt(inputPhoneNumber.getText()));
        userToUpdate.setIs_admin(isAdminCheckbox.isSelected());

        usersDAO.updateUser(userToUpdate);
        App.setRoot("users/users");
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("users/users");
    }
}