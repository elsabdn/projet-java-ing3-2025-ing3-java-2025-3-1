package Vue.advanced;

import Modele.Acheteur;
import Modele.Produit;
import Modele.Utilisateur;
import Modele.Vendeur;
import Controller.AuthController;
import Controller.ProduitController;      // pour récupérer la liste
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel container;
    private AuthController authController;

    public MainFrame() {
        setTitle("🛒 Shopping App");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- 1) AuthController + CardLayout + container ---
        authController = new AuthController();

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                Color color1 = new Color(253, 243, 247);
                Color color2 = new Color(252, 228, 236);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        container.setOpaque(false);
        setContentPane(container);

        // --- 2) Création et enregistrement de l’AccueilPanel ---
        AccueilPanel accueil = new AccueilPanel();

        // 2.1) Se connecter
        accueil.setLoginAction(e -> {
            String email = JOptionPane.showInputDialog(this, "Email :");
            String mdp   = JOptionPane.showInputDialog(this, "Mot de passe :");
            Utilisateur u = authController.connexion(email, mdp);

            if (u == null) {
                JOptionPane.showMessageDialog(this, "Identifiants invalides", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (u instanceof Vendeur) {
                VendeurPanel vp = new VendeurPanel((Vendeur) u, this);
                addPanel(vp, "vendeur");
                showPanel("vendeur");

            } else { // Acheteur connecté
                // On récupère la liste des produits
                List<Produit> produits = new ProduitController().getAllProduits();
                AcheteurPanel ap = new AcheteurPanel(this, produits);
                addPanel(ap, "acheteur");
                showPanel("acheteur");
            }
        });

        // 2.2) Nouvelle inscription Acheteur
        accueil.setAcheteurAction(e -> {
            String email = JOptionPane.showInputDialog(this, "Email :");
            String mdp   = JOptionPane.showInputDialog(this, "Mot de passe :");
            Acheteur a   = authController.registerAcheteur(email, mdp);

            List<Produit> produits = new ProduitController().getAllProduits();
            AcheteurPanel ap = new AcheteurPanel(this, produits);
            addPanel(ap, "acheteur");
            showPanel("acheteur");
        });

        // 2.3) Nouvelle inscription Vendeur
        accueil.setVendeurAction(e -> {
            String email = JOptionPane.showInputDialog(this, "Email :");
            String mdp   = JOptionPane.showInputDialog(this, "Mot de passe :");
            Vendeur v    = authController.registerVendeur(email, mdp);

            VendeurPanel vp = new VendeurPanel(v, this);
            addPanel(vp, "vendeur");
            showPanel("vendeur");
        });

        addPanel(accueil, "accueil");

        // --- 3) Affichage initial ---
        showPanel("accueil");
        setVisible(true);
    }

    public void showPanier(List<Produit> panier) {
        PanierPanel pp = new PanierPanel(this, panier);
        addPanel(pp, "panier");
        showPanel("panier");
    }


    /** Ajoute un JPanel au CardLayout sous l’identifiant name */
    public void addPanel(JPanel panel, String name) {
        container.add(panel, name);
    }

    /** Affiche le panneau enregistré sous l’identifiant name */
    public void showPanel(String name) {
        cardLayout.show(container, name);
    }
}
