package ite.jp.ak.lab07.seller;

import ite.jp.ak.lab07.seller.service.SellerService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SellerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SellerApplication.class.getResource("seller-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 400);
        stage.setTitle("Sprzedawca");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        var seller = SellerService.getInstance();

        if (seller.getSellerId() != null) {
            seller.getKeeper().unregister(seller.getSellerId());
        }

        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}