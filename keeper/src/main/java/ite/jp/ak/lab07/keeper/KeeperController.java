package ite.jp.ak.lab07.keeper;

import ite.jp.ak.lab07.keeper.service.KeeperService;
import ite.jp.ak.lab07.utils.enums.UserRole;
import ite.jp.ak.lab07.utils.model.Order;
import ite.jp.ak.lab07.utils.model.User;
import ite.jp.ak.lab07.utils.repository.ItemRepository;
import ite.jp.ak.lab07.utils.service.KeeperImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import pl.edu.pwr.tkubik.jp.shop.api.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class KeeperController {

    // Server info
    @FXML private TextField registryNameTextField;
    @FXML private TextField portTextField;
    @FXML private Label listeningLabel;

    // TabPane
    @FXML private TabPane tabPane;

    // Users tab
    @FXML private Tab usersTab;

    @FXML private TableView<User> usersTableView;
    @FXML private TableColumn<User, String> usersIdTableColumn;
    @FXML private TableColumn<User, String> usersRoleTableColumn;

    // Products tab
    @FXML private Tab productsTab;

    // Products table
    @FXML private TableView<Item> productsTableView;
    @FXML private TableColumn<Item, String> productsQuantityTableColumn;
    @FXML private TableColumn<Item, String> productsNameTableColumn;

    // Product info
    @FXML private Label productQuantityLabel;
    @FXML private Label productDescriptionLabel;

    // Add new product
    @FXML private TextField newProductNameTextField;
    @FXML private TextField newProductQuantityTextField;

    // Orders tab
    @FXML private Tab ordersTab;

    // Orders table
    @FXML private TableView<Order> ordersTableView;
    @FXML private TableColumn<Order, String> orderIdTableColumn;
    @FXML private TableColumn<Order, String> orderClientTableColumn;
    @FXML private TableColumn<Order, String> orderProductsTableColumn;
    @FXML private TableColumn<Order, String> orderStatusTableColumn;

    private final ItemRepository itemRepository = ItemRepository.getInstance();
    private final List<User> registeredUsers = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    private final KeeperService keeper = KeeperService.getInstance();

    public KeeperController() throws RemoteException {
    }

    public void initialize() {
        usersIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usersRoleTableColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        productsQuantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        productsNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        orderIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderClientTableColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        orderProductsTableColumn.setCellValueFactory(new PropertyValueFactory<>("items"));
        orderStatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        registryNameTextField.setText("keeper");
        portTextField.setText("1099");  // default RMI port

        itemRepository.getItems().add(new Item("towar1", 10));
        itemRepository.getItems().add(new Item("towar2", 20));
        itemRepository.getItems().add(new Item("towar3", 30));

        fillProductsTableView();
    }

    public void fillUsersTableView() {
        usersTableView.getItems().clear();
        ObservableList<User> users = FXCollections.observableArrayList(registeredUsers);
        usersTableView.setItems(users);
    }

    public void fillProductsTableView() {
        productsTableView.getItems().clear();
        ObservableList<Item> products = FXCollections.observableArrayList(itemRepository.getAvailableItems());
        productsTableView.setItems(products);
    }

    public void fillOrdersTableView() {
        ordersTableView.getItems().clear();
        ObservableList<Order> orders = FXCollections.observableArrayList(this.orders);
        ordersTableView.setItems(orders);
    }

    public void refreshView() {
        fillUsersTableView();
        fillProductsTableView();
        fillOrdersTableView();
        productQuantityLabel.setText("ID: ");
        productDescriptionLabel.setText("Nazwa: ");
    }

    public void registerHandler(Integer id, ICallback callback) {
        UserRole role;
        if (callback instanceof ICustomer) {
            role = UserRole.Customer;
        } else if (callback instanceof ISeller) {
            role = UserRole.Seller;
        } else if (callback instanceof IKeeper) {
            role = UserRole.Keeper;
        } else if (callback instanceof IDeliverer) {
            role = UserRole.Deliverer;
        } else {
            role = UserRole.Unknown;
        }
        registeredUsers.add(new User() {{
            setId(id);
            setRole(role);
        }});
        Platform.runLater(this::refreshView);
    }

    public void unregisterHandler(Integer id) {
        registeredUsers.removeIf(user -> user.getId().equals(id));
        refreshView();
    }

    public void putOrderHandler(Order order) {
        orders.add(order);
        refreshView();
    }

    public void startListening(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Błąd uruchamiania serwera");
        try {
            var stubName = registryNameTextField.getText();
            if (stubName.isBlank()) {
                throw new Exception("Nazwa namiastki w rejestrze RMI nie może być pusta");
            }
            var port = Integer.parseInt(portTextField.getText());

            IKeeper newKeeper = new KeeperImpl();
            ((KeeperImpl) newKeeper).setRegisterHandler(this::registerHandler);
            ((KeeperImpl) newKeeper).setUnregisterHandler(this::unregisterHandler);
            ((KeeperImpl) newKeeper).setPutOrderHandler(this::putOrderHandler);

            Registry rmiRegistry = LocateRegistry.createRegistry(port);
            rmiRegistry.rebind(stubName, newKeeper);

            keeper.setKeeper(newKeeper);
            keeper.setRegistry(rmiRegistry);
            keeper.setStubName(stubName);

            listeningLabel.setText("Nasłuchiwanie na porcie: " + port + " [Aktywne]");
        } catch (Exception e) {
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void addProduct(ActionEvent event) {
        var name = newProductNameTextField.getText();
        if (name.isBlank()) {
            return;
        }

        var quantity = Integer.parseInt(newProductQuantityTextField.getText());
        if (quantity <= 0) {
            return;
        }

        var newItem = new Item(name, quantity);
        itemRepository.getItems().add(newItem);
        newProductNameTextField.setText("");
        refreshView();
    }

    public void removeProduct(ActionEvent event) {
        var product = productsTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }
        itemRepository.getItems().remove(product);
        refreshView();
    }

    public void onSelectedProduct(MouseEvent event) {
        var product = productsTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }
        productQuantityLabel.setText("Ilość: " + product.getQuantity());
        productDescriptionLabel.setText("Opis: " + product.getDescription());
    }

    public void stop() {
        try {
            UnicastRemoteObject.unexportObject(keeper.getKeeper(), true);
            keeper.getRegistry().unbind(keeper.getStubName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}