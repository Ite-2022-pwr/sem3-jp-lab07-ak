module ite.jp.ak.lab07.deliverer {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires shop;
    requires java.rmi;
    requires ite.jp.ak.lab07.utils;


    opens ite.jp.ak.lab07.deliverer to javafx.fxml;
    exports ite.jp.ak.lab07.deliverer;
    exports ite.jp.ak.lab07.deliverer.service;
}