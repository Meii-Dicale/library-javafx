package cda.bibliotheque;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import cda.bibliotheque.model.User;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static User loggedInUser;

    @Override
    public void start(Stage stage) throws IOException {
        // Démarrer sur la page de connexion
        scene = new Scene(loadFXML("login"), 640, 480);
        scene.getStylesheets().add(App.class.getResource("/cda/bibliotheque/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    public static Scene getScene() {
        return scene;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/cda/bibliotheque/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void logout() throws IOException {
        loggedInUser = null;
        setRoot("login");
    }

    public static void main(String[] args) {
        launch();
    }
}