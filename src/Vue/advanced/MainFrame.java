package Vue.advanced;

import Modele.Acheteur;
import Modele.Produit;
import Modele.Utilisateur;
import Modele.Vendeur;
import Controller.AuthController;
import Controller.ProduitController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    // Système de navigation entre les panels
    private CardLayout cardLayout;
    private JPanel container;

    // Contrôleur d'authentification centralisé
    private AuthController authController;

    // Pour mémoriser l'utilisateur acheteur connecté
    private Acheteur acheteurConnecte;

    public MainFrame() {
        setTitle("🛒 Shopping App");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        authController = new AuthController();

        // Configuration du layout principal avec fond dégradé
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

        // --- Page d'accueil ---
        AccueilPanel accueil = new AccueilPanel(this);

        // --- Connexion utilisateur: ouverture de pop-ups pour saisir les identifiants ---
        accueil.setLoginAction(e -> {
            String email = JOptionPane.showInputDialog(this, "Email :");
            String mdp   = JOptionPane.showInputDialog(this, "Mot de passe :");
            Utilisateur u = authController.connexion(email, mdp);

            if (u == null) {
                JOptionPane.showMessageDialog(this, "Identifiants invalides", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Redirection selon le type d'utilisateur
            if (u instanceof Vendeur) {
                VendeurPanel vp = new VendeurPanel((Vendeur) u, this);
                addPanel(vp, "vendeur");
                showPanel("vendeur");
            } else {
                setAcheteurConnecte((Acheteur) u); // ✅ Stockage de l'acheteur connecté
                List<Produit> produits = new ProduitController().getAllProduits();
                AcheteurPanel ap = new AcheteurPanel(this, produits);
                addPanel(ap, "acheteur");
                showPanel("acheteur");
            }
        });

        // --- Inscription acheteur ---
        accueil.setAcheteurAction(e -> {
            String email = JOptionPane.showInputDialog(this, "Email :");
            String mdp   = JOptionPane.showInputDialog(this, "Mot de passe :");
            Acheteur a   = authController.registerAcheteur(email, mdp);
            setAcheteurConnecte(a); // ✅ Stockage après inscription
            List<Produit> produits = new ProduitController().getAllProduits();
            AcheteurPanel ap = new AcheteurPanel(this, produits);
            addPanel(ap, "acheteur");
            showPanel("acheteur");
        });

        // --- Inscription vendeur ---
        accueil.setVendeurAction(e -> {
            String email = JOptionPane.showInputDialog(this, "Email :");
            String mdp   = JOptionPane.showInputDialog(this, "Mot de passe :");
            Vendeur v    = authController.registerVendeur(email, mdp);
            VendeurPanel vp = new VendeurPanel(v, this);
            addPanel(vp, "vendeur");
            showPanel("vendeur");
        });

        // Ajout initial du panel d'accueil
        addPanel(accueil, "accueil");
        showPanel("accueil");
        setVisible(true);
    }

    /** Affiche le panel panier */
    public void showPanier(List<Produit> panier) {
        PanierPanel pp = new PanierPanel(this, panier);
        addPanel(pp, "panier");
        showPanel("panier");
    }

    /** Ajoute un panel au conteneur avec un nom */
    public void addPanel(JPanel panel, String name) {
        container.add(panel, name);
    }

    /** Affiche un panel selon son nom */
    public void showPanel(String name) {
        cardLayout.show(container, name);
    }

    /** Recharge et affiche l'accueil Acheteur */
    public void showAcheteurHome() {
        Acheteur a = getAcheteurConnecte();
        List<Produit> produits = new ProduitController().getAllProduits();
        AcheteurPanel ap = new AcheteurPanel(this, produits);
        addPanel(ap, "acheteur");
        showPanel("acheteur");
    }

    /** Affiche l’accueil Vendeur une fois connecté/inscrit */
    public void showVendeurHome(Vendeur v) {
        VendeurPanel vp = new VendeurPanel(v, this);
        addPanel(vp, "vendeur");
        showPanel("vendeur");
    }


    // Getters et setters pour le suivi de l'utilisateur connecté
    public Acheteur getAcheteurConnecte() {
        return acheteurConnecte;
    }

    public void setAcheteurConnecte(Acheteur acheteurConnecte) {
        this.acheteurConnecte = acheteurConnecte;
    }

    public AuthController getAuthController() {
        return authController;
    }

}