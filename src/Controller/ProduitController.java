package Controller;

import Modele.Produit;
import Modele.Vendeur;
import DAO.ProduitDAO;

import java.util.List;

public class ProduitController {
    private ProduitDAO produitDAO = new ProduitDAO();

    public List<Produit> getAllProduits() {
        return produitDAO.getAll();
    }

    public void addProduit(Vendeur vendeur, String name, double price, int quantite) {
        Produit produit = new Produit(0, name, price, quantite, vendeur);
        vendeur.addProduit(produit);
        produitDAO.ajouter(produit);
    }

    public void removeProduit(Vendeur vendeur, Produit produit) {
        vendeur.removeProduit(produit);
        produitDAO.supprimer(produit);
    }
}
