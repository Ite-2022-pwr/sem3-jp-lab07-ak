package ite.jp.ak.lab07.deliverer;

import ite.jp.ak.lab07.deliverer.service.DelivererService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DelivererApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DelivererApplication.class.getResource("deliverer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Dostawca");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        DelivererService delivererService = DelivererService.getInstance();

        if (delivererService.getDelivererId() != null) {
            delivererService.getKeeper().unregister(delivererService.getDelivererId());
        }

        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}