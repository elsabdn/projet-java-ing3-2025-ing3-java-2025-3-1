package Modele;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private Seller seller;

    public Product(int id, String name, double price, int quantity, Seller seller) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.seller = seller;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity; }

}