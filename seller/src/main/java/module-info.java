module ite.jp.ak.lab07.seller {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;

    requires ite.jp.ak.lab07.utils;
    requires java.rmi;
    requires shop;


    opens ite.jp.ak.lab07.seller to javafx.fxml;
    exports ite.jp.ak.lab07.seller;
    exports ite.jp.ak.lab07.seller.service;
}