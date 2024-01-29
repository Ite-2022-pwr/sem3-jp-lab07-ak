package ite.jp.ak.lab07.seller;

import ite.jp.ak.lab07.seller.service.SellerService;
import ite.jp.ak.lab07.utils.service.SellerImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import pl.edu.pwr.tkubik.jp.shop.api.IKeeper;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SellerController {

    // Keeper info
    @FXML private TextField keeperHostTextField;
    @FXML private TextField keeperPortTextField;

    private final SellerService seller = SellerService.getInstance();

    public void initialize() {
        keeperHostTextField.setText("keeper");
        keeperPortTextField.setText("1099");
        System.setProperty("java.rmi.server.hostname","192.168.7.218");
    }


    public void registerToKeeper(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Błąd rejestracji");
        try {
            var stubName = keeperHostTextField.getText();
            if (stubName.isBlank()) {
                throw new Exception("Host nie może być pusty");
            }
            var port = Integer.parseInt(keeperPortTextField.getText());

            Registry registry = LocateRegistry.getRegistry("192.168.7.253", port);
            IKeeper keeper = (IKeeper) registry.lookup(stubName);

            SellerImpl iSeller = new SellerImpl();
            iSeller.setKeeper(keeper);

            seller.setKeeper(keeper);
            seller.setRegistry(registry);
            seller.setStubName(stubName);

            var sellerId = keeper.register(iSeller);
            seller.setSellerId(sellerId);

        } catch (Exception e) {
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

}