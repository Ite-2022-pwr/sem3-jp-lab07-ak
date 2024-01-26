package ite.jp.ak.lab07.utils.service;

import lombok.Getter;
import lombok.Setter;
import pl.edu.pwr.tkubik.jp.shop.api.ICallback;
import pl.edu.pwr.tkubik.jp.shop.api.IDeliverer;
import pl.edu.pwr.tkubik.jp.shop.api.Item;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
public class DelivererImpl extends UnicastRemoteObject implements IDeliverer {

    private Consumer<List<Item>> returnOrderHandler;
    private BiConsumer<ICallback, List<Item>> responseHandler;

    public DelivererImpl() throws RemoteException {
    }

    @Override
    public void returnOrder(List<Item> list) throws RemoteException {
        if (returnOrderHandler != null) {
            returnOrderHandler.accept(list);
        }
    }

    @Override
    public void response(ICallback ic, List<Item> list) throws RemoteException {
        if (responseHandler != null) {
            responseHandler.accept(ic, list);
        }
    }
}
