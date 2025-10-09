module cda.bibliotheque {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;


    opens cda.bibliotheque.controller to javafx.fxml;
    opens cda.bibliotheque.controller.author to javafx.fxml;
    opens cda.bibliotheque.controller.media to javafx.fxml;
    opens cda.bibliotheque.controller.users to javafx.fxml;
    opens cda.bibliotheque.controller.reservation to javafx.fxml;
    opens cda.bibliotheque.controller.category to javafx.fxml;
    opens cda.bibliotheque.controller.stock to javafx.fxml;

    exports cda.bibliotheque;

}
