package ite.jp.ak.lab07.utils.repository;

import lombok.Getter;
import pl.edu.pwr.tkubik.jp.shop.api.Item;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ItemRepository {

    private static ItemRepository instance;

    private final List<Item> items = new ArrayList<>();

    private ItemRepository() {

    }

    public static ItemRepository getInstance() {
        if (instance == null) {
            instance = new ItemRepository();
        }
        return instance;
    }

    public List<Item> getAvailableItems() {
        return items.stream().filter(item -> (item.getQuantity() > 0)).toList();
    }

    public Item findAvailableItemByDescription(String description) {
        Item tempItem = findItemByDescription(description);
        if (tempItem == null) {
            return null;
        }
        return tempItem.getQuantity() > 0 ? tempItem : null;
    }

    public Item findItemByDescription(String description) {
        return items.stream().filter(item -> (item.getDescription().equals(description))).findFirst().orElse(null);
    }
}
