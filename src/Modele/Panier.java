package Modele;

import java.util.*;

/**
 * La classe Panier représente le panier d'achats d'un acheteur.
 * Elle contient une liste d'articles (produit + quantité) et offre des méthodes pour gérer le panier.
 */
public class Panier {


    /**
     * Classe interne représentant un article du panier,
     * c'est-à-dire une association entre un produit et une quantité.
     */
    public static class Articles {
        private Produit produit;
        private int quantite; //quantite


        /**
         * Constructeur pour créer un nouvel article du panier.
         * @param produit Produit concerné
         * @param quantite Quantité ajoutée
         */
        public Articles(Produit produit, int quantite) {
            this.produit = produit;
            this.quantite = quantite;
        }

        public Produit getProduit() {
            return produit;
        }

        public int getQuantite() {
            return quantite;
        }
    }

    private Acheteur acheteur;
    private List<Articles> items = new ArrayList<>();

    /**
     * Constructeur du Panier.
     * @param acheteur L'acheteur auquel est associé ce panier
     */
    public Panier(Acheteur acheteur) {
        this.acheteur = acheteur;
    }

    public List<Articles> getItems() {
        return items;
    }



    /**
     * Ajoute un article au panier. Si le produit existe déjà, augmente la quantité.
     * @param produit Produit à ajouter
     * @param quantite Quantité à ajouter
     */
    public void ajouterArticle(Produit produit, int quantite) {
        for (Articles item : items) {
            if (item.getProduit().equals(produit)) {
                item.quantite += quantite;
                return;
            }
        }
        items.add(new Articles(produit, quantite));
    }

    /**
     * Supprime un article du panier à partir du produit donné.
     * @param produit Produit à supprimer du panier
     */
    public void supprimerItem(Produit produit) {
        items.removeIf(item -> item.getProduit().equals(produit));
    }

    /**
     * Vide entièrement le panier.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Calcule le prix total du panier en tenant compte des promotions en gros.
     * @return Le prix total
     */
    public double getPrixTot() {
        double total = 0.0;
        for (Articles item : items) {
            Produit p = item.getProduit();
            int qte = item.getQuantite();

            if (p.estPromoEnGros() && qte >= p.getSeuilGros()) {
                int nbLots = qte / p.getSeuilGros();
                int reste = qte % p.getSeuilGros();
                total += nbLots * p.getPrixGros() + reste * p.getPrix();
            } else {
                total += qte * p.getPrix();
            }
        }
        return total;
    }


    /**
     * Retourne une représentation textuelle du panier :
     * liste des articles et prix total.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Contenu du panier:\n");

        if (items.isEmpty()) {
            sb.append("Le panier est vide.\n");
        } else {
            for (Articles item : items) {
                sb.append(item.getProduit().getNom())
                        .append(" x").append(item.getQuantite())
                        .append(" @ ").append(item.getProduit().getPrix())
                        .append("€\n");
            }
        }

        sb.append("\nTotal: ").append(getPrixTot()).append("€");
        return sb.toString();
    }

}