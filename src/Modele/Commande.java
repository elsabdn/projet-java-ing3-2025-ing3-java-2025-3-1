package Modele;

import java.util.List;

public class Commande {
    private int id;
    private double montant;
    private List<Panier.Item> items;

    public Commande(int id, double montant, List<Panier.Item> items) {
        this.id = id;
        this.montant = montant;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public double getMontant() {
        return montant;
    }

    public List<Panier.Item> getItems() {
        return items;
    }

    public void setItems(List<Panier.Item> items) {
        this.items = items;
    }
}
