package Modele;

public class Produit {
    private int id;
    private String nom;
    private double prix;
    private int quantite;
    private Vendeur vendeur;
    private boolean promoEnGros;
    private int seuilGros;
    private double prixGros;


    public Produit(int id, String nom, double prix, int quantite, Vendeur vendeur) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.vendeur = vendeur;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public double getPrix() {
        return prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public Vendeur getVendeur() {
        return vendeur;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite; }
    public boolean isPromoEnGros() {
        return promoEnGros;
    }

    public int getSeuilGros() {
        return seuilGros;
    }

    public double getPrixGros() {
        return prixGros;
    }

}