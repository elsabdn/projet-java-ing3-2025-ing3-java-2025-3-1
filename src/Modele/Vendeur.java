package Modele;

import java.util.ArrayList;
import java.util.List;
import DAO.DatabaseManager;

public class Vendeur extends Utilisateur {
    private List<Produit> produitListe;

    public Vendeur(int id, String email, String mdp) {
        super(id, email, mdp, "vendeur");
        this.produitListe = new ArrayList<>();
    }

    public List<Produit> getProduitListe() { return produitListe; }

    public void setProduitListe(List<Produit> produitListe) { this.produitListe = produitListe;}

    public void ajouterProduit(Produit produit) {
        produitListe.add(produit);
    }

    public void supprimerProduit(Produit produit) {
        produitListe.remove(produit);
    }

    // Ajout de la méthode pour obtenir la connexion à la base de données
    public DatabaseManager getConnection() {
        return DatabaseManager.getInstance();  // Utilise DatabaseManager pour obtenir la connexion
    }

}