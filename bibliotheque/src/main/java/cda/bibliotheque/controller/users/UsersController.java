package cda.bibliotheque.controller.users;

import cda.bibliotheque.App;
import cda.bibliotheque.controller.reservation.ReservationController;
import cda.bibliotheque.dao.UsersDAO;
import cda.bibliotheque.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class UsersController {

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> colUserName;
    @FXML
    private TableColumn<User, String> colMail;
    @FXML
    private TableColumn<User, String> colPhoneNumber;
    @FXML
    private TableColumn<User, Void> colActions;
    @FXML
    private TextField searchField;

    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private final UsersDAO usersDAO = new UsersDAO();
    private FilteredList<User> filteredData;

    @FXML
    public void initialize() {
        colUserName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser_name()));
        colMail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMail()));
        colPhoneNumber.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone_number()));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button editButton = new Button("Modifier");
            private final Button reservationsButton = new Button("Réservations");
            private final HBox pane = new HBox(10, editButton, reservationsButton, deleteButton);

            {
                deleteButton.getStyleClass().add("button-delete");
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (App.showConfirmationDialog("Confirmation", "Voulez-vous vraiment supprimer l'utilisateur '" + user.getUser_name() + "' ?")) {
                        usersDAO.deleteUser(user.getId());
                        loadUsers();
                    }
                });

                editButton.setOnAction(event -> {
                    User userToEdit = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("/cda/bibliotheque/users/edit-user.fxml"));
                        Parent parent = loader.load();
                        EditUserController controller = loader.getController();
                        controller.setUser(userToEdit);
                        App.getScene().setRoot(parent);
                    } catch (IOException e) {
                        System.err.println("Erreur lors du chargement de la page de modification d'utilisateur : " + e.getMessage());
                    }
                });

                reservationsButton.setOnAction(event -> {
                    User selectedUser = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(App.class.getResource("/cda/bibliotheque/reservations/reservations.fxml"));
                        Parent parent = loader.load();
                        ReservationController controller = loader.getController();
                        controller.filterByUser(selectedUser); // On passe l'utilisateur au contrôleur des réservations
                        App.getScene().setRoot(parent);
                    } catch (IOException e) {
                        System.err.println("Erreur lors du chargement de la page des réservations : " + e.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        loadUsers();

        filteredData = new FilteredList<>(userList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());
        usersTable.setItems(filteredData);
    }

    private void loadUsers() {
        userList.setAll(usersDAO.getNonAdminUsers());
    }

    private void updateFilter() {
        String searchText = searchField.getText();
        filteredData.setPredicate(user -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return user.getUser_name().toLowerCase().contains(lowerCaseFilter) ||
                   user.getMail().toLowerCase().contains(lowerCaseFilter);
        });
    }

    @FXML
    void goBack() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    void switchToCreateUser() throws IOException {
        App.setRoot("users/create-user");
    }
}