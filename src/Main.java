import Controller.AuthController;
import Controller.PanierController;
import Controller.ProduitController;
import Database.DatabaseManager;
import Modele.*;
import Modele.Acheteur;
import Vue.advanced.*;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialisation
            Acheteur acheteur1 = new Acheteur(1, "acheteur@example.com", "motdepasse");
            DatabaseManager db = new DatabaseManager();
            AuthController auth = new AuthController(db);
            ProduitController produitController = new ProduitController(db);
            PanierController panierController = new PanierController(acheteur1);

            // Fenêtre principale avec navigation
            MainFrame mainFrame = new MainFrame();

            // Panneaux
            AccueilPanel accueilPanel = new AccueilPanel();
            ConnexionLabel connexionLabel = new ConnexionLabel();

            // Ajout des panneaux à la fenêtre
            mainFrame.addPanel(accueilPanel, "accueil");
            mainFrame.addPanel(connexionLabel, "connexion");

            // Affichage accueil
            mainFrame.showPanel("accueil");
            mainFrame.setVisible(true);

            // === Navigation depuis Accueil ===

            accueilPanel.setLoginAction(e -> {
                mainFrame.showPanel("connexion");
            });

            accueilPanel.setAcheteurAction(e -> {
                String email = JOptionPane.showInputDialog("Email :");
                String mdp = JOptionPane.showInputDialog("Mot de passe:");

                auth.registerAcheteur(email, mdp);
                JOptionPane.showMessageDialog(mainFrame, "Compte acheteur créé !");
            });

            accueilPanel.setVendeurAction(e -> {
                String email = JOptionPane.showInputDialog("Email :");
                String mdp = JOptionPane.showInputDialog("Mot de passe :");

                auth.registerVendeur(email, mdp);
                JOptionPane.showMessageDialog(mainFrame, "Compte vendeur créé !");
            });

            // === Navigation depuis Login ===

            connexionLabel.setBackAction(e -> mainFrame.showPanel("accueil"));

            connexionLabel.setLoginAction(e -> {
                String email = connexionLabel.getEmail();
                String mdp = connexionLabel.getMdp();

                Utilisateur utilisateur = auth.connexion(email, mdp);
                if (utilisateur == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Identifiants incorrects !");
                    return;
                }

                if (utilisateur instanceof Acheteur acheteur) {
                    List<Produit> produits = db.getProduits();
                    AcheteurPanel acheteurPanel = new AcheteurPanel(produits);
                    mainFrame.addPanel(acheteurPanel, "acheteur");

                    acheteurPanel.getRefreshButton().addActionListener(ev -> {
                        acheteurPanel.updateProduitList(db.getProduits());
                    });

                    acheteurPanel.getViewPanierButton().addActionListener(ev -> {
                        Panier panier = acheteur.getPanier();
                        JOptionPane.showMessageDialog(mainFrame, panier.toString(), "Votre panier", JOptionPane.INFORMATION_MESSAGE);
                    });

                    acheteurPanel.getBuyProduitButton().addActionListener(ev -> {
                        try {
                            String idStr = JOptionPane.showInputDialog("Entrez l'ID du produit à acheter :");
                            int produitId = Integer.parseInt(idStr);

                            String qteStr = JOptionPane.showInputDialog("Quantité :");
                            int quantite = Integer.parseInt(qteStr);

                            Produit produitToBuy = db.getProduitById(produitId);

                            if (produitToBuy == null) {
                                JOptionPane.showMessageDialog(mainFrame, "Produit introuvable.");
                                return;
                            }

                            boolean success = panierController.addToPanier(produitToBuy, quantite);
                            if (success) {
                                JOptionPane.showMessageDialog(mainFrame, "Produit ajouté au panier !");
                                acheteurPanel.updateProduitList(db.getProduits()); // mise à jour affichage
                            } else {
                                JOptionPane.showMessageDialog(mainFrame, "Stock insuffisant !");
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(mainFrame, "Erreur d'entrée : " + ex.getMessage());
                        }
                    });

                    acheteurPanel.getCheckoutButton().addActionListener(ev -> {
                        panierController.checkout(acheteur);
                    });


                    mainFrame.showPanel("acheteur");

                } else if (utilisateur instanceof Vendeur vendeur) {
                    VendeurPanel vendeurPanel = new VendeurPanel(vendeur);
                    mainFrame.addPanel(vendeurPanel, "vendeur");

                    vendeurPanel.getRefreshButton().addActionListener(ev -> {
                        vendeurPanel.updateProduitList(vendeur);
                    });

                    vendeurPanel.getAddProduitButton().addActionListener(ev -> {
                        String nom = JOptionPane.showInputDialog("Nom du produit :");
                        double prix = Double.parseDouble(JOptionPane.showInputDialog("Prix :"));
                        int qte = Integer.parseInt(JOptionPane.showInputDialog("Quantité :"));

                        produitController.addProduit(vendeur, nom, prix, qte);
                        vendeurPanel.updateProduitList(vendeur);
                    });

                    mainFrame.showPanel("vendeur");
                }
            });
        });
    }
}