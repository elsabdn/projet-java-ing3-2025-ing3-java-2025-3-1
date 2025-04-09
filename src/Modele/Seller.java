package Modele;

import java.util.ArrayList;
import java.util.List;

public class Seller extends User {
    private List<Product> productList;

    public Seller(int id, String email, String password) {
        super(id, email, password, "seller");
        this.productList = new ArrayList<>();
    }

    public List<Product> getProductList() { return productList; }

    public void addProduct(Product product) {
        productList.add(product);
    }

    public void removeProduct(Product product) {
        productList.remove(product);
    }
}