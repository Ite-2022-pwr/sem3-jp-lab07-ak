package ite.jp.ak.lab07.keeper.service;

import lombok.Getter;
import lombok.Setter;
import pl.edu.pwr.tkubik.jp.shop.api.IKeeper;

import java.rmi.registry.Registry;


@Getter
@Setter
public class KeeperService {
    private static KeeperService instance;

    private IKeeper keeper;
    private Registry registry;
    private String stubName;

    private KeeperService() {}

    public static KeeperService getInstance() {
        if (instance == null) {
            instance = new KeeperService();
        }
        return instance;
    }



}
