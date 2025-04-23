package Modele;
import java.util.List;

public class Acheteur extends Utilisateur {
    private Panier panier;
    private List<Produit> produitList;

    public Acheteur(int id, String email, String mdp) {
        super(id, email, mdp, "acheteur");
        this.panier = new Panier(this);
    }

    public List<Produit> getProduitList() {
        return produitList;
    }

    public void setProduitList(List<Produit> produitList) {
        this.produitList = produitList;
    }

    public Panier getPanier() { return panier; }


}

