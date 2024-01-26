package ite.jp.ak.lab07.customer;

import ite.jp.ak.lab07.customer.service.CustomerService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CustomerApplication.class.getResource("customer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Klient");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        var customer = CustomerService.getInstance();

        if (customer.getCustomerId() != null) {
            customer.getKeeper().unregister(customer.getCustomerId());
        }

        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}