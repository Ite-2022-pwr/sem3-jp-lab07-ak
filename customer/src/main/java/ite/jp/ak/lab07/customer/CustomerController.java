package ite.jp.ak.lab07.customer;

import ite.jp.ak.lab07.customer.service.CustomerService;
import ite.jp.ak.lab07.utils.enums.OrderStatus;
import ite.jp.ak.lab07.utils.model.Order;
import ite.jp.ak.lab07.utils.service.CustomerImpl;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.ArrayList;
import java.util.List;

public class CustomerController {

    // Keeper info
    @FXML private TextField keeperHostTextField;
    @FXML private TextField keeperPortTextField;

    // TabPane
    @FXML private TabPane tabPane;

    // Products tab
    @FXML private Tab productsTab;

    // Products table
    @FXML private TableView<Item> productsTableView;
    @FXML private TableColumn<Item, String> productsQuantityTableColumn;
    @FXML private TableColumn<Item, String> productsNameTableColumn;

    // Product info
    @FXML private Label productNameLabel;
    @FXML private TextField productQuantityTextField;

    // Cart
    @FXML private TableView<Item> cartTableView;
    @FXML private TableColumn<Item, String> cartQuantityTableColumn;
    @FXML private TableColumn<Item, String> cartNameTableColumn;

    // Orders Tab
    @FXML private Tab ordersTab;

    // Orders table
    @FXML private TableView<Order> ordersTableView;
    @FXML private TableColumn<Order, String> ordersIdTableColumn;
    @FXML private TableColumn<Order, String> ordersStatusTableColumn;

    // To buy table
    @FXML private TableView<Item> toBuyTableView;
    @FXML private TableColumn<Item, String> toBuyQuantityTableColumn;
    @FXML private TableColumn<Item, String> toBuyNameTableColumn;

    // To return table
    @FXML private TableView<Item> toReturnTableView;
    @FXML private TableColumn<Item, String> toReturnQuantityTableColumn;
    @FXML private TableColumn<Item, String> toReturnNameTableColumn;

    // Receipts Tab
    @FXML private Tab receiptsTab;

    // Receipts table
    @FXML private TableView<String> receiptsTableView;
    @FXML private TableColumn<String, String> receiptsTableColumn;


    private final CustomerService customer = CustomerService.getInstance();
    private final List<Item> storeOffer = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<String> receipts = new ArrayList<>();

    private final List<Item> newOrderProductList = new ArrayList<>();
    private Item selectedProduct;
    private Integer nextOrderId = 1;

    private final List<Item> toBuyProductList = new ArrayList<>();
    private final List<Item> toReturnProductList = new ArrayList<>();

    public void initialize() {
        productsQuantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        productsNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        cartQuantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        ordersIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ordersStatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        toBuyQuantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        toBuyNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        toReturnQuantityTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        toReturnNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        receiptsTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        keeperHostTextField.setText("keeper");
        keeperPortTextField.setText("1099");
    }

    public void fillProductsTableView() {
        productsTableView.getItems().clear();
        ObservableList<Item> products = FXCollections.observableArrayList(storeOffer);
        productsTableView.setItems(products);
    }

    public void fillCartTableView() {
        cartTableView.getItems().clear();
        ObservableList<Item> products = FXCollections.observableArrayList(newOrderProductList);
        cartTableView.setItems(products);
    }

    public void fillOrdersTableView() {
        ordersTableView.getItems().clear();
        ObservableList<Order> orders = FXCollections.observableArrayList(this.orders);
        ordersTableView.setItems(orders);
    }

    public void fillToBuyTableView() {
        toBuyTableView.getItems().clear();
        ObservableList<Item> products = FXCollections.observableArrayList(toBuyProductList);
        toBuyTableView.setItems(products);
    }

    public void fillToReturnTableView() {
        toReturnTableView.getItems().clear();
        ObservableList<Item> products = FXCollections.observableArrayList(toReturnProductList);
        toReturnTableView.setItems(products);
    }

    public void fillReceiptsTableView() {
        System.out.println(this.receipts);
        receiptsTableView.getItems().clear();
        ObservableList<String> receipts = FXCollections.observableArrayList(this.receipts);
        receiptsTableView.setItems(receipts);
    }

    public void refreshView() {
        fillProductsTableView();
        fillOrdersTableView();
        fillReceiptsTableView();
        productNameLabel.setText("Nazwa: ");
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

            CustomerImpl iCustomer = new CustomerImpl();
            iCustomer.setResponseHandler(this::responseHandler);
            iCustomer.setPutOrderHandler(this::putOrderHandler);
            iCustomer.setReturnReceiptHandler(this::returnReceiptHandler);
            customer.setCustomer(iCustomer);
            customer.setKeeper(keeper);
            customer.setStubName(stubName);
            customer.setRegistry(registry);

            var customerId = keeper.register(customer.getCustomer());
            customer.setCustomerId(customerId);

        } catch (Exception e) {
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void responseHandler(ICallback customer, List<Item> items) {
        storeOffer.clear();
        storeOffer.addAll(items);
        fillProductsTableView();
    }

    public void putOrderHandler(ICallback deliverer, List<Item> items) {
        customer.setLastDeliverer((IDeliverer) deliverer);
        var newOrder = new Order() {{
            setId(nextOrderId++);
            setCustomer(customer.getCustomer());
            setCustomerId(customer.getCustomerId());
            setItems(items);
            setStatus(OrderStatus.Accepted);
        }};
        orders.add(newOrder);
        fillOrdersTableView();
    }

    public void returnReceiptHandler(String receipt) {
        receipts.add(receipt);
        fillReceiptsTableView();
    }

    public void refreshOffer(ActionEvent event) {
        try {
            customer.getKeeper().getOffer(customer.getCustomerId());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToCart(ActionEvent event) {
        var product = productsTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }

        var quantity = Integer.parseInt(productQuantityTextField.getText());
        if (quantity <= 0) {
            return;
        }

        var newOrderItem = new Item(product.getDescription(), quantity);

        newOrderProductList.add(newOrderItem);
        fillCartTableView();
    }

    public void onSelectedProductFromOffer(MouseEvent event) {
        var product = productsTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }
        productNameLabel.setText("Opis: " + product.getDescription());
        this.selectedProduct = product;
    }

    public void putOrder(ActionEvent event) {
        try {
            customer.getKeeper().putOrder(customer.getCustomerId(), newOrderProductList);
            newOrderProductList.clear();
            fillCartTableView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void onSelectedOrder(MouseEvent event) {
        var order = ordersTableView.getSelectionModel().getSelectedItem();
        if (order == null) {
            return;
        }

        toBuyProductList.clear();
        toReturnProductList.clear();
        toBuyProductList.addAll(order.getItems());

        fillToBuyTableView();
        fillToReturnTableView();
    }

    public void onSelectedProductFromToBuy(MouseEvent event) {
        var product = toBuyTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }

        toBuyProductList.remove(product);
        toReturnProductList.add(product);
        fillToBuyTableView();
        fillToReturnTableView();
    }

    public void onSelectedProductFromToReturn(MouseEvent event) {
        var product = toReturnTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }

        toReturnProductList.remove(product);
        toBuyProductList.add(product);
        fillToBuyTableView();
        fillToReturnTableView();
    }

    public void returnOrder(ActionEvent event) {
        if (toReturnProductList.isEmpty()) {
            return;
        }

        try {
            if (customer.getLastDeliverer() == null) {
                return;
            }
            customer.getLastDeliverer().returnOrder(toReturnProductList);
            toReturnProductList.clear();
            fillToReturnTableView();
            fillToBuyTableView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void acceptOrder(ActionEvent event) {
        var order = ordersTableView.getSelectionModel().getSelectedItem();
        if (order == null) {
            return;
        }

        if (toBuyProductList.isEmpty() && toReturnProductList.isEmpty()) {
            return;
        }

        try {
            List<ISeller> sellers = customer.getKeeper().getSellers();

            if (sellers.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Nie udało się zrealizować zamówienia");
                alert.setContentText("Brak wolnego sprzedawcy");
                alert.showAndWait();
                return;
            }

            var seller = sellers.get(0);

            assert seller != null;
            seller.acceptOrder(customer.getCustomer(), toBuyProductList, toReturnProductList);

            orders.remove(order);
            toBuyProductList.clear();
            toReturnProductList.clear();
            refreshView();
            fillToBuyTableView();
            fillToReturnTableView();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


}