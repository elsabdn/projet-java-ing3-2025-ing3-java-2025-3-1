import Controller.AuthController;
import Controller.PanierController;
import Controller.ProduitController;
import DAO.DatabaseManager;
import Modele.Acheteur;
import Modele.Utilisateur;
import Modele.Vendeur;
import Modele.Produit;
import Vue.advanced.*;

import javax.swing.*;
import java.util.List;

public class Main {

    /**
     * Classe Main, point d'entrée de l'application.
     * Lance l'interface graphique (Swing) et initialise les contrôleurs, modèles et vues.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // ─── Initialisation des contrôleurs et de la base ───────────────
            DatabaseManager db            = new DatabaseManager();
            AuthController auth           = new AuthController();
            ProduitController produitCtrl = new ProduitController();
            Acheteur dummyAcheteur        = new Acheteur(1, "acheteur@example.com", "motdepasse");
            PanierController panierCtrl   = new PanierController(dummyAcheteur);

            // ─── Création de la fenêtre principale ───────────────────────────
            MainFrame mainFrame = new MainFrame();

            // ─── Panneaux de base : accueil et connexion ─────────────────────
            AccueilPanel   accueilPanel   = new AccueilPanel(mainFrame);
            ConnexionLabel connexionLabel = new ConnexionLabel(); //test

            mainFrame.ajouterPanel(accueilPanel,   "accueil");
            mainFrame.ajouterPanel(connexionLabel, "connexion");
            mainFrame.afficherPanel("accueil");
            mainFrame.setVisible(true);

            // === Actions sur AccueilPanel ===
            // wiring AccueilPanel
            accueilPanel.definirActionConnexion(e -> mainFrame.afficherPanel("connexion"));
            accueilPanel.definirActionAcheteur(e ->
                    mainFrame.afficherPanel("inscription_acheteur")
            );
            accueilPanel.definirActionVendeur(e ->
                    mainFrame.afficherPanel("inscription_vendeur")
            );

            // === Actions sur ConnexionLabel ===
            connexionLabel.setActionRetour(e -> mainFrame.afficherPanel("accueil"));

            connexionLabel.setActionConnexion(e -> {
                String email = connexionLabel.getEmail();
                String mdp   = connexionLabel.getMdp();
                Utilisateur u = auth.connexion(email, mdp);
                if (u == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Identifiants incorrects !");
                    return;
                }

                // --- Si c'est un acheteur ---
                if (u instanceof Acheteur acheteur) {
                    mainFrame.definirAcheteurConnecte(acheteur); // ✅ ajoute cette ligne

                    List<Produit> produits = db.obtenirProduits();
                    AcheteurPanel ap = new AcheteurPanel(mainFrame, produits);
                    mainFrame.ajouterPanel(ap, "acheteur");

                    ap.obtenirBoutonRafraichir().addActionListener(ev ->
                            ap.mettreAJourListeProduits(db.obtenirProduits())
                    );

                    mainFrame.afficherPanel("acheteur");
                }
                else if (u instanceof Vendeur vendeur) {
                    VendeurPanel vp = new VendeurPanel(vendeur, mainFrame);
                    mainFrame.ajouterPanel(vp, "vendeur");

                    vp.getRefreshButton().addActionListener(ev ->
                            vp.mettreAJourProduitList(vendeur)
                    );

                    mainFrame.afficherPanel("vendeur");
                }
            });
        });
    }
}