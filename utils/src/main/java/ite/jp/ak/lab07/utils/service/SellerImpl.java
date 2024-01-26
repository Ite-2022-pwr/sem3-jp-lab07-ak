package ite.jp.ak.lab07.utils.service;

import lombok.Getter;
import lombok.Setter;
import pl.edu.pwr.tkubik.jp.shop.api.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

@Getter
@Setter
public class SellerImpl extends UnicastRemoteObject implements ISeller {

    private IKeeper keeper;

    public SellerImpl() throws RemoteException {
    }

    @Override
    public void acceptOrder(ICustomer iCustomer, List<Item> boughtItemList, List<Item> returnedItemList) throws RemoteException {
        if (keeper != null) {
            keeper.returnOrder(returnedItemList);
            assert iCustomer != null;
            iCustomer.returnReceipt("Zakupione towary: " + boughtItemList.toString());
        }
    }

    @Override
    public void response(ICallback iCallback, List<Item> list) throws RemoteException {

    }
}
