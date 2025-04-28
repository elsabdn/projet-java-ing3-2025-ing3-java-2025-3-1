package Modele;

import java.util.ArrayList;
import java.util.List;
import DAO.DatabaseManager;


/**
 * La classe Vendeur représente un utilisateur ayant le rôle spécifique de vendeur.
 * Elle hérite de la classe Utilisateur et gère une liste de produits à vendre.
 */
public class Vendeur extends Utilisateur {
    private List<Produit> produitListe;


    /**
     * Constructeur de la classe Vendeur.
     * Initialise l'identité du vendeur et sa liste de produits vide.
     *
     * @param id Identifiant unique du vendeur
     * @param email Email du vendeur
     * @param mdp Mot de passe du vendeur
     */
    public Vendeur(int id, String email, String mdp) {
        super(id, email, mdp, "vendeur");
        this.produitListe = new ArrayList<>();
    }


    // ─── Getters et Setters ───────────────────────────────────────────

    /**
     * Récupère la liste des produits du vendeur.
     * @return Liste des produits
     */
    public List<Produit> getProduitListe() { return produitListe; }


    /**
     * Définit la liste des produits du vendeur.
     * @param produitListe Nouvelle liste de produits
     */
    public void setProduitListe(List<Produit> produitListe) { this.produitListe = produitListe;}


    // ─── Méthodes de gestion des produits ──────────────────────────────

    /**
     * Ajoute un produit à la liste du vendeur.
     * @param produit Produit à ajouter
     */
    public void ajouterProduit(Produit produit) {
        produitListe.add(produit);
    }

    public void supprimerProduit(Produit produit) {
        produitListe.remove(produit);
    }

    // ─── Méthode pour la gestion de la base de données ───────────────────

    /**
     * Permet au vendeur d'obtenir une connexion à la base de données via DatabaseManager.
     * @return Instance unique de DatabaseManager
     */
    // Ajout de la méthode pour obtenir la connexion à la base de données
    public DatabaseManager getConnection() {
        return DatabaseManager.obtenirInstance();  // Utilise DatabaseManager pour obtenir la connexion
    }

}