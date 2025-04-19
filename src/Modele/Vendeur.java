package Modele;

import java.util.ArrayList;
import java.util.List;

public class Vendeur extends Utilisateur {
    private List<Produit> produitList;

    public Vendeur(int id, String email, String mdp) {
        super(id, email, mdp, "vendeur");
        this.produitList = new ArrayList<>();
    }

    public List<Produit> getProduitList() { return produitList; }

    public void setProduitList(List<Produit> produitList) { this.produitList = produitList;}

    public void addProduit(Produit produit) {
        produitList.add(produit);
    }

    public void removeProduit(Produit produit) {
        produitList.remove(produit);
    }
}