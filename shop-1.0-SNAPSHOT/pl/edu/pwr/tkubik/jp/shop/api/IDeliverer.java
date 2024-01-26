package pl.edu.pwr.tkubik.jp.shop.api;

import java.rmi.RemoteException;
import java.util.List;

public interface IDeliverer extends ICallback {
    public void returnOrder(List<Item> itemList) throws RemoteException;
}
