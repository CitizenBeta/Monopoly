module ie.ucd.monopoly {
    requires javafx.controls;
    requires javafx.fxml;


    opens ie.ucd.monopoly to javafx.fxml;
    exports ie.ucd.monopoly;
}