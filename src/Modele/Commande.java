package Modele;

import java.util.Date;
import java.util.List;

public class Commande {
    private int id;
    private double montant;
    private int note;           // note sur 10, 0 = pas de note
    private Date date;          // date de la commande
    private List<Panier.Articles> items;

    /**
     * Constructeur pour une commande sans note.
     */
    public Commande(int id, double montant, Date date, List<Panier.Articles> items) {
        this.id      = id;
        this.montant = montant;
        this.date    = date;
        this.items   = items;
        this.note    = 0;        // 0 signifie pas de note
    }

    /**
     * Constructeur pour une commande avec note.
     */
    public Commande(int id, double montant, int note, Date date, List<Panier.Articles> items) {
        this.id      = id;
        this.montant = montant;
        this.note    = note;
        this.date    = date;
        this.items   = items;
    }

    // ─── Getters & Setters ────────────────────────────────────────────

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Panier.Articles> getItems() {
        return items;
    }

    public void setItems(List<Panier.Articles> items) {
        this.items = items;
    }
}
