package Controller;

import Modele.Produit;
import Modele.Vendeur;
import DAO.ProduitDAO;
import DAO.DatabaseManager;

import java.util.List;

/**
 * Contrôleur pour gérer les opérations liées aux produits,
 * y compris la description que le vendeur peut fournir.
 */
public class ProduitController {
    private final ProduitDAO produitDAO;
    private final DatabaseManager databaseManager;

    public ProduitController() {
        this.produitDAO = new ProduitDAO();
        this.databaseManager = new DatabaseManager();
    }

    /** Récupère tous les produits existants. */
    public List<Produit> getAllProduits() {
        return produitDAO.getAll();
    }

    /** Récupère les produits correspondant à un vendeur. */
    public List<Produit> getProduitsParVendeur(int vendeurId) {
        return databaseManager.getProduitsParVendeur(vendeurId);
    }

    /**
     * Crée et enregistre un nouveau produit, en incluant sa description.
     */
    public void addProduit(
            Vendeur vendeur,
            String nom,
            double prix,
            int quantite,
            String imagePath,
            String marque,
            String description,
            boolean promoEnGros,
            int seuilGros,
            double prixGros
    ) {
        // Création de l'objet Produit (id = 0 pour auto‑incrément)
        Produit produit = new Produit(
                0,
                nom,
                prix,
                quantite,
                vendeur,
                imagePath,
                marque,
                description
        );
        // Liaison au vendeur et insertion en base
        produit.setPromoEnGros(promoEnGros);
        produit.setSeuilGros(seuilGros);
        produit.setPrixGros(prixGros);
        vendeur.addProduit(produit);
        produitDAO.ajouter(produit);
    }

    /**
     * Supprime un produit existant si son ID est valide.
     */
    public void removeProduit(Vendeur vendeur, Produit produit) {
        if (produit.getId() != 0) {
            vendeur.removeProduit(produit);
            produitDAO.supprimer(produit);
        } else {
            System.out.println("Impossible de supprimer : ID invalide.");
        }
    }

    /**
     * Met à jour un produit, y compris sa description.
     */
    public void updateProduit(Produit produit) {
        if (produit.getId() != 0) {
            produitDAO.mettreAJourProduit(produit);
        } else {
            System.out.println("Impossible de mettre à jour : ID invalide.");
        }
    }
}