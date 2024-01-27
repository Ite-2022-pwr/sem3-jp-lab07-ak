package ite.jp.ak.lab07.deliverer;

import ite.jp.ak.lab07.deliverer.service.DelivererService;
import ite.jp.ak.lab07.utils.service.DelivererImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.edu.pwr.tkubik.jp.shop.api.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.stream.Collectors;


public class DelivererController {
    // Keeper info
    @FXML private TextField keeperHostTextField;
    @FXML private TextField keeperPortTextField;

    // Order label
    @FXML private Label orderLabel;

    private final DelivererService deliverer = DelivererService.getInstance();

    public void initialize() {
        keeperHostTextField.setText("keeper");
        keeperPortTextField.setText("1099");
    }


    public void registerToKeeper(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Błąd rejestracji");
        try {
            var stubName = keeperHostTextField.getText();
            if (stubName.isBlank()) {
                throw new Exception("Nazwa namiastki rejestru RMI nie może być pusta");
            }
            var port = Integer.parseInt(keeperPortTextField.getText());

            Registry registry = LocateRegistry.getRegistry(port);
            IKeeper keeper = (IKeeper) registry.lookup(stubName);

            DelivererImpl iDeliverer = new DelivererImpl();
            iDeliverer.setResponseHandler(this::responseHandler);
            iDeliverer.setReturnOrderHandler(this::returnOrderHandler);

            deliverer.setDeliverer(iDeliverer);
            deliverer.setKeeper(keeper);
            deliverer.setStubName(stubName);
            deliverer.setRegistry(registry);

            var delivererId = keeper.register(deliverer.getDeliverer());
            deliverer.setDelivererId(delivererId);

        } catch (Exception e) {
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void responseHandler(ICallback ic, List<Item> itemList) {
        try {
            ((ICustomer) ic).putOrder(deliverer.getDeliverer(), itemList);
            Platform.runLater(() -> {
                orderLabel.setText("Obsługiwanie zamówienia z produktami:\n" + itemList.stream().map(Item::toString).collect(Collectors.joining("\n")));
            });
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void returnOrderHandler(List<Item> itemList) {
        try {
            deliverer.getKeeper().returnOrder(itemList);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Błąd zwracania zamówienia");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void getOrder(ActionEvent event) {
        try {
            deliverer.getKeeper().getOrder(deliverer.getDelivererId());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Błąd pobierania zamówienia");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}