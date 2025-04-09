package Modele;

import java.util.*;

public class Cart {

    public static class Item {
        private Product product;
        private int quantity;

        public Item(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    private Buyer buyer;
    private List<Item> items = new ArrayList<>();

    public Cart(Buyer buyer) {
        this.buyer = buyer;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Product product, int quantity) {
        for (Item item : items) {
            if (item.getProduct().equals(product)) {
                item.quantity += quantity;
                return;
            }
        }
        items.add(new Item(product, quantity));
    }

    public void removeItem(Product product) {
        items.removeIf(item -> item.getProduct().equals(product));
    }

    public void clear() {
        items.clear();
    }
}
