package ite.jp.ak.lab07.utils.service;

import ite.jp.ak.lab07.utils.enums.OrderStatus;
import ite.jp.ak.lab07.utils.model.Order;
import ite.jp.ak.lab07.utils.repository.ItemRepository;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pwr.tkubik.jp.shop.api.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
@Setter
public class KeeperImpl extends UnicastRemoteObject implements IKeeper {

    private Integer nextUserId = 1;
    private Integer nextOrderId = 1;
    private final Map<Integer, ICallback> users = new HashMap<>();
    private final Queue<Order> orders = new ArrayDeque<>();

    private final ItemRepository itemRepository = ItemRepository.getInstance();

    BiConsumer<Integer, ICallback> registerHandler;
    Consumer<Integer> unregisterHandler;
    Consumer<Order> putOrderHandler;

    public KeeperImpl() throws RemoteException {

    }

    @Override
    public int register(ICallback iCallback) throws RemoteException {
        users.put(nextUserId++, iCallback);

        if (registerHandler != null) {
            registerHandler.accept(nextUserId - 1, iCallback);
        }

        return nextUserId - 1;
    }

    @Override
    public boolean unregister(int i) throws RemoteException {
        ICallback callback = users.get(i);
        if (callback == null) {
            return false;
        }
        users.remove(i);

        if (unregisterHandler != null) {
            unregisterHandler.accept(i);
        }

        return true;
    }

    @Override
    public void getOffer(int i) throws RemoteException {
        ICallback callback = users.get(i);
        callback.response(null, itemRepository.getAvailableItems());
    }

    @Override
    public void putOrder(int i, List<Item> list) throws RemoteException {
        var customer = users.get(i);
        var orderItems = new ArrayList<Item>();
        for (var wantedItem : list) {
            var item = itemRepository.findAvailableItemByDescription(wantedItem.getDescription());
            if (item == null) {
                continue;
            }
            var quantity = Math.min(item.getQuantity(), wantedItem.getQuantity());
            item.setQuantity(item.getQuantity() - quantity);
            wantedItem.setQuantity(quantity);
            orderItems.add(wantedItem);
        }

        var newOrder = new Order() {{
            setId(nextOrderId++);
            setCustomer(customer);
            setCustomerId(i);
            setItems(orderItems);
            setStatus(OrderStatus.Ordered);
        }};
        orders.add(newOrder);

        if (putOrderHandler != null) {
            putOrderHandler.accept(newOrder);
        }
    }

    @Override
    public List<ISeller> getSellers() throws RemoteException {
        return users.values().stream().filter(user -> user instanceof ISeller).map(user -> (ISeller) user).toList();
    }

    @Override
    public void getOrder(int i) throws RemoteException {
        IDeliverer deliverer = (IDeliverer) users.get(i);
        if (deliverer == null) {
            return;
        }

        var nextOrder = orders.poll();
        if (nextOrder == null) {
            return;
        }

        nextOrder.setStatus(OrderStatus.Accepted);
        deliverer.response(nextOrder.getCustomer(), nextOrder.getItems());
    }

    @Override
    public void returnOrder(List<Item> list) throws RemoteException {
        for (var returnedItem : list) {
            var item = itemRepository.findItemByDescription(returnedItem.getDescription());
            if (item == null) {
                continue;
            }
            item.setQuantity(item.getQuantity() + returnedItem.getQuantity());
        }
    }
}
