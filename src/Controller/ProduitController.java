package Controller;

import Modele.Produit;
import Modele.Vendeur;
import DAO.ProduitDAO;
import DAO.DatabaseManager;

import java.util.List;

public class ProduitController {
    private ProduitDAO produitDAO = new ProduitDAO();
    private DatabaseManager databaseManager;

    // Constructeur de la classe
    public ProduitController() {
        this.databaseManager = new DatabaseManager();  // Assure-toi que DatabaseManager est bien instancié
    }
    public List<Produit> getAllProduits() {
        return produitDAO.getAll();
    }

    public List<Produit> getProduitsParVendeur(int vendeurId) {
        return databaseManager.getProduitsParVendeur(vendeurId);
    }

    public void addProduit(Vendeur vendeur, String nom, double prix, int quantite, String imagePath, String marque) {
        Produit produit = new Produit(0, nom, prix, quantite, vendeur, imagePath, marque);
        vendeur.addProduit(produit);
        produitDAO.ajouter(produit);
    }

    public void removeProduit(Vendeur vendeur, Produit produit) {
        // Vérifier si l'ID est valide (pas 0)
        if (produit.getId() != 0) {
            vendeur.removeProduit(produit);
            produitDAO.supprimer(produit);
        } else {
            System.out.println("Impossible de supprimer ce produit, ID invalide.");
        }
    }

    public void updateProduit(Produit produit) {
        if (produit.getId() != 0) {  // Vérifier que l'ID est valide avant de tenter de mettre à jour
            produitDAO.mettreAJourProduit(produit);
        } else {
            System.out.println("Impossible de mettre à jour ce produit, ID invalide.");
        }
    }
}
