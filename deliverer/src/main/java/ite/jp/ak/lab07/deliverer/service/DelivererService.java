package ite.jp.ak.lab07.deliverer.service;

import lombok.Getter;
import lombok.Setter;
import pl.edu.pwr.tkubik.jp.shop.api.IDeliverer;
import pl.edu.pwr.tkubik.jp.shop.api.IKeeper;

import java.rmi.registry.Registry;

@Getter
@Setter
public class DelivererService {

    private static DelivererService instance;

    private IKeeper keeper;
    private IDeliverer deliverer;
    private Integer delivererId;
    private Registry registry;
    private String stubName;

    private DelivererService() {}

    public static DelivererService getInstance() {
        if (instance == null) {
            instance = new DelivererService();
        }
        return instance;
    }


}
