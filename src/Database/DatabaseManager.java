package Database;

import Modele.Utilisateur;
import Modele.Produit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {
    private List<Utilisateur> utilisateurs;
    private List<Produit> produits;

    private AtomicInteger utilisateurIdCounter;
    private AtomicInteger produitIdCounter;

    public DatabaseManager() {
        utilisateurs = new ArrayList<>();
        produits = new ArrayList<>();
        utilisateurIdCounter = new AtomicInteger(1);
        produitIdCounter = new AtomicInteger(1);
    }

    // === UTILISATEURS ===
    public void addUtilisateur(Utilisateur utilisateur) {
        utilisateurs.add(utilisateur);
    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public int generateUtilisateurId() {
        return utilisateurIdCounter.getAndIncrement();
    }

    // === PRODUITS ===
    public void addProduit(Produit produit) {
        produits.add(produit);
    }

    public void removeProduit(Produit produit) {
        produits.remove(produit);
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public int generateProduitId() {
        return produitIdCounter.getAndIncrement();
    }

    public Produit getProduitById(int id) {
        for (Produit p : produits) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}
