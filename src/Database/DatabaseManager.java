package Database;

import Modele.User;
import Modele.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {
    private List<User> users;
    private List<Product> products;

    private AtomicInteger userIdCounter;
    private AtomicInteger productIdCounter;

    public DatabaseManager() {
        users = new ArrayList<>();
        products = new ArrayList<>();
        userIdCounter = new AtomicInteger(1);
        productIdCounter = new AtomicInteger(1);
    }

    // === USERS ===
    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public int generateUserId() {
        return userIdCounter.getAndIncrement();
    }

    // === PRODUCTS ===
    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    public List<Product> getProducts() {
        return products;
    }

    public int generateProductId() {
        return productIdCounter.getAndIncrement();
    }

    public Product getProductById(int id) {
        for (Product p : products) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}
