package Controller;

import Modele.Produit;
import Modele.Vendeur;
import Database.DatabaseManager;

import java.util.List;

public class ProduitController {
    private DatabaseManager db;

    public ProduitController(DatabaseManager db) {
        this.db = db;
    }

    public List<Produit> getAllProduits() {
        return db.getProduits();
    }

    public void addProduit(Vendeur vendeur, String name, double price, int quantite) {
        int id = db.generateProduitId();
        Produit produit = new Produit(id, name, price, quantite, vendeur);
        vendeur.addProduit(produit);
        db.addProduit(produit);
    }

    public void removeProduit(Vendeur vendeur, Produit produit) {
        vendeur.removeProduit(produit);
        db.removeProduit(produit);
    }
}
