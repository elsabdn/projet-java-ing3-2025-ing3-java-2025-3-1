package Controller;

import Modele.Acheteur;
import Modele.Vendeur;
import Modele.Utilisateur;
import DAO.UtilisateurDAO;

/**
 * AuthController gère l'authentification (connexion) et l'inscription
 * des utilisateurs (acheteurs et vendeurs).
 */
public class AuthController {
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    /**
     * Permet à un utilisateur de se connecter en vérifiant son email et son mot de passe.
     * @param email L'email de l'utilisateur
     * @param mdp Le mot de passe de l'utilisateur
     * @return L'utilisateur correspondant si les informations sont valides, sinon null
     */
    public Utilisateur connexion(String email, String mdp) {
        return utilisateurDAO.getParEmailEtMdp(email, mdp);
    }

    /**
     * Permet d'inscrire un nouvel acheteur dans la base de données.
     * @param email L'email du nouvel acheteur
     * @param mdp Le mot de passe du nouvel acheteur
     * @return L'objet Acheteur créé
     */
    public Acheteur inscrireAcheteur(String email, String mdp) {
        Acheteur acheteur = new Acheteur(0, email, mdp);
        utilisateurDAO.ajouter(acheteur);
        return acheteur;
    }

    /**
     * Permet d'inscrire un nouveau vendeur dans la base de données.
     * @param email L'email du nouveau vendeur
     * @param mdp Le mot de passe du nouveau vendeur
     * @return L'objet Vendeur créé
     */
    public Vendeur inscrireVendeur(String email, String mdp) {
        Vendeur vendeur = new Vendeur(0, email, mdp);
        utilisateurDAO.ajouter(vendeur);
        return vendeur;
    }
}