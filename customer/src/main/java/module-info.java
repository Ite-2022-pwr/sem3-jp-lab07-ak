module ite.jp.ak.lab07.customer {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires shop;
//    requires utils;
    requires ite.jp.ak.lab07.utils;

    opens ite.jp.ak.lab07.customer to javafx.fxml;
    exports ite.jp.ak.lab07.customer;
    exports ite.jp.ak.lab07.customer.service;
}