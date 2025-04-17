package Controller;

import Modele.Acheteur;
import Modele.Vendeur;
import Modele.Utilisateur;
import Database.DatabaseManager;

public class AuthController {
    private DatabaseManager db;

    public AuthController(DatabaseManager db) {
        this.db = db;
    }

    public Utilisateur connexion(String email, String mdp) {
        return db.getUtilisateurs().stream()
                .filter(utilisateur -> utilisateur.getEmail().equals(email) && utilisateur.getMdp().equals(mdp))
                .findFirst()
                .orElse(null);
    }

    public Acheteur registerAcheteur(String email, String mdp) {
        int id = db.genererIdUtilisateur();
        Acheteur buyer = new Acheteur(id, email, mdp);
        db.addUtilisateur(buyer);
        return buyer;
    }

    public Vendeur registerVendeur(String email, String mdp) {
        int id = db.genererIdUtilisateur();
        Vendeur vendeur = new Vendeur(id, email, mdp);
        db.addUtilisateur(vendeur);
        return vendeur;
    }
}
