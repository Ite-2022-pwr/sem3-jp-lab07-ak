package ite.jp.ak.lab07.seller.service;

import lombok.Getter;
import lombok.Setter;
import pl.edu.pwr.tkubik.jp.shop.api.IKeeper;
import pl.edu.pwr.tkubik.jp.shop.api.ISeller;

import java.rmi.registry.Registry;

@Getter
@Setter
public class SellerService {
    private static SellerService instance;

    private IKeeper keeper;
    private ISeller seller;
    private Integer sellerId;
    private Registry registry;
    private String stubName;

    private SellerService() {}

    public static SellerService getInstance() {
        if (instance == null) {
            instance = new SellerService();
        }
        return instance;
    }


}
