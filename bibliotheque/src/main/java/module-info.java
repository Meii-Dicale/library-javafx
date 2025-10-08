module cda.bibliotheque {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens cda.bibliotheque.controller to javafx.fxml;
    opens cda.bibliotheque.controller.author to javafx.fxml;
    exports cda.bibliotheque;
}
