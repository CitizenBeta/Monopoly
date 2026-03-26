package ie.ucd.monopolydeal.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MonopolyDealApplication extends Application {
    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1180, 760);
        stage.setTitle("Monopoly Deal FX");
        stage.setScene(scene);
        stage.show();
    }
}
