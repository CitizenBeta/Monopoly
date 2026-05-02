module ie.ucd.monopolydeal {
    requires javafx.controls;
    requires javafx.fxml;

    exports ie.ucd.monopolydeal.app;
    opens ie.ucd.monopolydeal.ui to javafx.fxml;
}
