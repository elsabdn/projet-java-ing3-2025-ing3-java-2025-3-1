package Modele;

import java.util.List;

public class Commande {
    private int id;
    private double montant;
    private int note; // ✅ Ajout de la note
    private List<Panier.Item> items;

    public Commande(int id, double montant, List<Panier.Item> items) {
        this.id = id;
        this.montant = montant;
        this.items = items;
    }

    public Commande(int id, double montant, int note, List<Panier.Item> items) {
        this.id = id;
        this.montant = montant;
        this.note = note;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public double getMontant() {
        return montant;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public List<Panier.Item> getItems() {
        return items;
    }

    public void setItems(List<Panier.Item> items) {
        this.items = items;
    }
}