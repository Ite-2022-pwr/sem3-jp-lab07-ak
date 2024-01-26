package ite.jp.ak.lab07.customer.service;

import lombok.Getter;
import lombok.Setter;
import pl.edu.pwr.tkubik.jp.shop.api.ICustomer;
import pl.edu.pwr.tkubik.jp.shop.api.IDeliverer;
import pl.edu.pwr.tkubik.jp.shop.api.IKeeper;

import java.rmi.registry.Registry;

@Getter
@Setter
public class CustomerService {
    private static CustomerService instance;

    private IKeeper keeper;
    private ICustomer customer;
    private Integer customerId;
    private Registry registry;
    private String stubName;
    private IDeliverer lastDeliverer;

    private CustomerService() {

    }

    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }
}
