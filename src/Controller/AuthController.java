package Controller;

import Modele.Acheteur;
import Modele.Vendeur;
import Modele.Utilisateur;
import DAO.UtilisateurDAO;

public class AuthController {
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Utilisateur connexion(String email, String mdp) {
        return utilisateurDAO.getByEmailAndMdp(email, mdp);
    }

    public Acheteur registerAcheteur(String email, String mdp) {
        Acheteur acheteur = new Acheteur(0, email, mdp);
        utilisateurDAO.ajouter(acheteur);
        return acheteur;
    }

    public Vendeur registerVendeur(String email, String mdp) {
        Vendeur vendeur = new Vendeur(0, email, mdp);
        utilisateurDAO.ajouter(vendeur);
        return vendeur;
    }
}
