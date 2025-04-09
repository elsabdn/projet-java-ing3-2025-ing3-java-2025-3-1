package Controller;

import Modele.Product;
import Modele.Seller;
import Database.DatabaseManager;

import java.util.List;

public class ProductController {
    private DatabaseManager db;

    public ProductController(DatabaseManager db) {
        this.db = db;
    }

    public List<Product> getAllProducts() {
        return db.getProducts();
    }

    public void addProduct(Seller seller, String name, double price, int quantity) {
        int id = db.generateProductId();
        Product product = new Product(id, name, price, quantity, seller);
        seller.addProduct(product);
        db.addProduct(product);
    }

    public void removeProduct(Seller seller, Product product) {
        seller.removeProduct(product);
        db.removeProduct(product);
    }
}
