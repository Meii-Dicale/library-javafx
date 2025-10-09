package cda.bibliotheque.controller.users;

import java.io.IOException;
import java.util.List;

import cda.bibliotheque.App;
import cda.bibliotheque.dao.UsersDAO;
import cda.bibliotheque.model.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class UsersController {

    @FXML
    private TableColumn<User, Void> colActions;

    @FXML
    private TableColumn<User, String> colMail;

    @FXML
    private TableColumn<User, Integer> colPhone;

    @FXML
    private TableColumn<User, String> colUserName;

    @FXML
    private TableView<User> tableUser;

    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private final UsersDAO usersDAO = new UsersDAO();

    @FXML
    public void initialize() {
        colUserName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser_name()));
        colMail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMail()));
        colPhone.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPhone_number()).asObject());

        colActions.setCellFactory(cellData -> new TableCell<>() {
            private final Button buttonDelete = new Button("Supprimer");
            private final Button buttonEdit = new Button("Modifier");
            private final HBox box = new HBox(buttonDelete, buttonEdit);

            {
                buttonDelete.getStyleClass().add("button-delete");
                buttonDelete.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    usersDAO.deleteUser(user.getId());
                    loadUsers();
                });
                buttonEdit.setOnAction(event -> {
                    User userToEdit = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("users/edit-user.fxml"));
                        Parent parent = loader.load();
                        EditUserController controller = loader.getController();
                        controller.setUser(userToEdit);
                        App.getScene().setRoot(parent);
                    } catch (IOException e) {
                        System.err.println("Erreur lors du chargement de la page de modification : " + e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
        loadUsers();
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    void switchToCreateUser(ActionEvent event) throws IOException {
        App.setRoot("users/create-user");

    }

    private void loadUsers() {
        userList.setAll(usersDAO.getAllUsers());
        tableUser.setItems(userList);
    }
}
