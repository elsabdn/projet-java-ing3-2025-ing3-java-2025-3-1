package Modele;
import java.util.List;

/**
 * La classe Acheteur représente un utilisateur ayant le rôle spécifique d'acheteur.
 * Elle hérite de la classe Utilisateur et possède un panier pour gérer ses achats.
 */
public class Acheteur extends Utilisateur {
    private Panier panier;
    private List<Produit> produitList;


    /**
     * Constructeur de la classe Acheteur.
     * Initialise l'identité de l'acheteur en appelant le constructeur de la classe Utilisateur,
     * puis crée un panier associé à cet acheteur.
     * @param id L'identifiant unique de l'acheteur
     * @param email L'email de l'acheteur
     * @param mdp Le mot de passe de l'acheteur
     */
    public Acheteur(int id, String email, String mdp) {
        super(id, email, mdp, "acheteur");
        this.panier = new Panier(this);
    }

    /**
     * Getter pour récupérer la liste de produits associée à l'acheteur.
     * @return La liste des produits
     */
    public List<Produit> getProduitList() {
        return produitList;
    }

    public void setProduitList(List<Produit> produitList) {
        this.produitList = produitList;
    }

    public Panier getPanier() { return panier; }


}

