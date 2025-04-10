package Modele;

public class Produit {
    private int id;
    private String name;
    private double price;
    private int quantite;
    private Vendeur vendeur;

    public Produit(int id, String name, double price, int quantite, Vendeur vendeur) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantite = quantite;
        this.vendeur = vendeur;
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

    public int getQuantite() {
        return quantite;
    }

    public Vendeur getVendeur() {
        return vendeur;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite; }

}