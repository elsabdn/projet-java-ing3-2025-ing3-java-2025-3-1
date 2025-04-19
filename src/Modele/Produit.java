package Modele;

public class Produit {
    private int id;
    private String nom;
    private double prix;
    private int quantite;
    private String imagePath;
    private Vendeur vendeur;
    private String description;
    private String marque;

    private boolean promoEnGros;
    private int seuilGros;
    private double prixGros;


    public Produit(int id, String nom, double prix, int quantite, Vendeur vendeur, String imagePath, String marque) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.imagePath = imagePath;
        this.vendeur = vendeur;
        this.marque = marque;
        this.description = description;
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

    public String getImagePath() { return imagePath; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Vendeur getVendeur() {
        return vendeur;
    }

    public String getDescription() { return description;}

    public void setDescription(String description) { this.description = description;}

    public void setQuantite(int quantite) { this.quantite = quantite; }

    public boolean isPromoEnGros() {
        return promoEnGros;
    }

    public int getSeuilGros() {
        return seuilGros;
    }

    public double getPrixGros() {
        return prixGros;
    }

    public void setNom(String nom) {this.nom = nom;}

    public void setPrix(double prix) { this.prix = prix; }

    public String getMarque() { return marque; }

    public void setMarque(String marque) { this.marque = marque; }

}