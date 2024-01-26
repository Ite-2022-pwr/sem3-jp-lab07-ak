package ite.jp.ak.lab07.utils.model;

import ite.jp.ak.lab07.utils.enums.OrderStatus;
import lombok.Data;
import pl.edu.pwr.tkubik.jp.shop.api.ICallback;
import pl.edu.pwr.tkubik.jp.shop.api.Item;

import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private Integer id;
    private ICallback customer;
    private Integer customerId;
    private List<Item> items = new ArrayList<>();
    private OrderStatus status;
}
