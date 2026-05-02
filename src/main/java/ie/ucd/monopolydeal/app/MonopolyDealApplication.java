package ie.ucd.monopolydeal.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MonopolyDealApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MonopolyDealApplication.class.getResource("/ie/ucd/monopolydeal/game-view.fxml"));
        Scene scene = new Scene(loader.load(), 1180, 760);
        stage.setTitle("Monopoly Deal FX");
        stage.setScene(scene);
        stage.show();
    }
}
