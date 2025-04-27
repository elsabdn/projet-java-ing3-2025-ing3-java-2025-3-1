package Modele;

public class Produit {
    private int id;
    private String nom;
    private double prix;
    private int quantite;
    private String imageChemin;
    private Vendeur vendeur;
    private String marque;
    private String description;

    private boolean promoEnGros;
    private int seuilGros;
    private double prixGros;

    /**
     * Constructeur principal incluant la description.
     */
    public Produit(int id,
                   String nom,
                   double prix,
                   int quantite,
                   Vendeur vendeur,
                   String imagePath,
                   String marque,
                   String description) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.vendeur = vendeur;
        this.imageChemin = imagePath;
        this.marque = marque;
        this.description = description;
    }

    /**
     * Constructeur sans description (pour compatibilité si besoin).
     * La description reste nulle.
     */
    public Produit(int id,
                   String nom,
                   double prix,
                   int quantite,
                   Vendeur vendeur,
                   String imagePath,
                   String marque) {
        this(id, nom, prix, quantite, vendeur, imagePath, marque, null);
    }

    // Getters et setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getImageChemin() {
        return imageChemin;
    }

    public void setImageChemin(String imageChemin) {
        this.imageChemin = imageChemin;
    }

    public Vendeur getVendeur() {
        return vendeur;
    }

    public void setVendeur(Vendeur vendeur) {
        this.vendeur = vendeur;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPromoEnGros() {
        return promoEnGros;
    }

    public void setPromoEnGros(boolean promoEnGros) {
        this.promoEnGros = promoEnGros;
    }

    public int getSeuilGros() {
        return seuilGros;
    }

    public void setSeuilGros(int seuilGros) {
        this.seuilGros = seuilGros;
    }

    public double getPrixGros() {
        return prixGros;
    }

    public void setPrixGros(double prixGros) {
        this.prixGros = prixGros;
    }
}