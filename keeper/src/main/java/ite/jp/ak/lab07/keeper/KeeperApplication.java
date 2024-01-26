package ite.jp.ak.lab07.keeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class KeeperApplication extends Application {

    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws IOException {
        fxmlLoader = new FXMLLoader(KeeperApplication.class.getResource("keeper-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Magazynier");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (fxmlLoader != null) {
            KeeperController keeperController = fxmlLoader.getController();
            keeperController.stop();
        }
        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}