package Modele;

import java.util.*;

public class Panier {

    public static class Item {
        private Produit produit;
        private int quantite;

        public Item(Produit produit, int quantite) {
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
    private List<Item> items = new ArrayList<>();

    public Panier(Acheteur acheteur) {
        this.acheteur = acheteur;
    }

    public List<Item> getItems() {
        return items;
    }



    public void addItem(Produit produit, int quantite) {
        for (Item item : items) {
            if (item.getProduit().equals(produit)) {
                item.quantite += quantite;
                return;
            }
        }
        items.add(new Item(produit, quantite));
    }

    public void removeItem(Produit produit) {
        items.removeIf(item -> item.getProduit().equals(produit));
    }

    public void clear() {
        items.clear();
    }

    public double getPrixTot() {
        double total = 0.0;
        for (Item item : items) {
            total += item.getProduit().getPrix() * item.getQuantite();
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Contenu du panier:\n");

        if (items.isEmpty()) {
            sb.append("Le panier est vide.\n");
        } else {
            for (Item item : items) {
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
