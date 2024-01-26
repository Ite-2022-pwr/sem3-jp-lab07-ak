package ite.jp.ak.lab07.utils.service;

import lombok.Getter;
import lombok.Setter;
import pl.edu.pwr.tkubik.jp.shop.api.ICallback;
import pl.edu.pwr.tkubik.jp.shop.api.ICustomer;
import pl.edu.pwr.tkubik.jp.shop.api.Item;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
public class CustomerImpl extends UnicastRemoteObject implements ICustomer {

    private BiConsumer<ICallback, List<Item>> putOrderHandler;
    private BiConsumer<ICallback, List<Item>> responseHandler;
    private Consumer<String> returnReceiptHandler;
    public CustomerImpl() throws RemoteException {
    }

    // method called by deliverer
    @Override
    public void putOrder(ICallback idd, List<Item> list) throws RemoteException {
        if (putOrderHandler != null) {
            putOrderHandler.accept(idd, list);
        }
    }

    @Override
    public void returnReceipt(String s) throws RemoteException {
        System.out.println(s);
        if (returnReceiptHandler != null) {
            returnReceiptHandler.accept(s);
        }
    }

    @Override
    public void response(ICallback iCallback, List<Item> list) throws RemoteException {
        if (responseHandler != null) {
            responseHandler.accept(iCallback, list);
        }
    }
}
