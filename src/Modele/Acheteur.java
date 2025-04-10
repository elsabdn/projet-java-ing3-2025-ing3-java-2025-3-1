package Modele;

public class Acheteur extends Utilisateur {
    private Panier panier;

    public Acheteur(int id, String email, String mdp) {
        super(id, email, mdp, "acheteur");
        this.panier = new Panier(this);
    }

    public Panier getPanier() { return panier; }
}
