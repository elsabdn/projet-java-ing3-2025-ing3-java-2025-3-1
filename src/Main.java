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
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.List;

public class Main {
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
            ConnexionLabel connexionLabel = new ConnexionLabel();

            mainFrame.addPanel(accueilPanel,   "accueil");
            mainFrame.addPanel(connexionLabel, "connexion");
            mainFrame.showPanel("accueil");
            mainFrame.setVisible(true);

            // === Actions sur AccueilPanel ===
            // wiring AccueilPanel
            accueilPanel.setLoginAction(e -> mainFrame.showPanel("connexion"));
            accueilPanel.setAcheteurAction(e ->
                    mainFrame.showPanel("inscription_acheteur")
            );
            accueilPanel.setVendeurAction(e ->
                    mainFrame.showPanel("inscription_vendeur")
            );

            // === Actions sur ConnexionLabel ===
            connexionLabel.setBackAction(e -> mainFrame.showPanel("accueil"));

            connexionLabel.setLoginAction(e -> {
                String email = connexionLabel.getEmail();
                String mdp   = connexionLabel.getMdp();
                Utilisateur u = auth.connexion(email, mdp);
                if (u == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Identifiants incorrects !");
                    return;
                }

                // --- Si c'est un acheteur ---
                if (u instanceof Acheteur acheteur) {
                    mainFrame.setAcheteurConnecte(acheteur); // ✅ ajoute cette ligne

                    List<Produit> produits = db.getProduits();
                    AcheteurPanel ap = new AcheteurPanel(mainFrame, produits);
                    mainFrame.addPanel(ap, "acheteur");

                    ap.getRefreshButton().addActionListener(ev ->
                            ap.updateProduitList(db.getProduits())
                    );

                    mainFrame.showPanel("acheteur");
                }
                else if (u instanceof Vendeur vendeur) {
                    VendeurPanel vp = new VendeurPanel(vendeur, mainFrame);
                    mainFrame.addPanel(vp, "vendeur");

                    vp.getRefreshButton().addActionListener(ev ->
                            vp.updateProduitList(vendeur)
                    );

                    mainFrame.showPanel("vendeur");
                }
            });
        });
    }
}