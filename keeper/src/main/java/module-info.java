module ite.jp.ak.lab07.keeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires shop;
    requires ite.jp.ak.lab07.utils;

    opens ite.jp.ak.lab07.keeper to javafx.fxml;
    exports ite.jp.ak.lab07.keeper;
    exports ite.jp.ak.lab07.keeper.service;
}