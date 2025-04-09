
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import Controller.AuthController;
import Controller.CartController;
import Controller.ProductController;
import Database.DatabaseManager;
import Modele.*;
import Modele.Buyer;
import Vue.advanced.*;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialisation
            DatabaseManager db = new DatabaseManager();
            AuthController auth = new AuthController(db);
            ProductController productController = new ProductController(db);
            CartController cartController = new CartController();

            // Fenêtre principale avec navigation
            MainFrame mainFrame = new MainFrame();

            // Panneaux
            AccueilPanel accueilPanel = new AccueilPanel();
            LoginPanel loginPanel = new LoginPanel();

            // Ajout des panneaux à la fenêtre
            mainFrame.addPanel(accueilPanel, "accueil");
            mainFrame.addPanel(loginPanel, "login");

            // Affichage accueil
            mainFrame.showPanel("accueil");
            mainFrame.setVisible(true);

            // === Navigation depuis Accueil ===

            accueilPanel.setLoginAction(e -> {
                mainFrame.showPanel("login");
            });

            accueilPanel.setBuyerAction(e -> {
                String email = JOptionPane.showInputDialog("Email :");
                String password = JOptionPane.showInputDialog("Mot de passe :");

                auth.registerBuyer(email, password);
                JOptionPane.showMessageDialog(mainFrame, "Compte acheteur créé !");
            });

            accueilPanel.setSellerAction(e -> {
                String email = JOptionPane.showInputDialog("Email :");
                String password = JOptionPane.showInputDialog("Mot de passe :");

                auth.registerSeller(email, password);
                JOptionPane.showMessageDialog(mainFrame, "Compte vendeur créé !");
            });

            // === Navigation depuis Login ===

            loginPanel.setBackAction(e -> mainFrame.showPanel("accueil"));

            loginPanel.setLoginAction(e -> {
                String email = loginPanel.getEmail();
                String password = loginPanel.getPassword();

                User user = auth.login(email, password);
                if (user == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Identifiants incorrects !");
                    return;
                }

                if (user instanceof Buyer buyer) {
                    List<Product> products = db.getProducts();
                    BuyerPanel buyerPanel = new BuyerPanel(products);
                    mainFrame.addPanel(buyerPanel, "buyer");

                    buyerPanel.getRefreshButton().addActionListener(ev -> {
                        buyerPanel.updateProductList(db.getProducts());
                    });

                    buyerPanel.getViewCartButton().addActionListener(ev -> {
                        Cart cart = buyer.getCart();
                        JOptionPane.showMessageDialog(mainFrame, cart.toString(), "Votre panier", JOptionPane.INFORMATION_MESSAGE);
                    });

                    buyerPanel.getBuyProductButton().addActionListener(ev -> {
                        try {
                            String idStr = JOptionPane.showInputDialog("Entrez l'ID du produit à acheter :");
                            int productId = Integer.parseInt(idStr);

                            String qtyStr = JOptionPane.showInputDialog("Quantité :");
                            int quantity = Integer.parseInt(qtyStr);

                            Product productToBuy = db.getProductById(productId);

                            if (productToBuy == null) {
                                JOptionPane.showMessageDialog(mainFrame, "Produit introuvable.");
                                return;
                            }

                            boolean success = cartController.addToCart(buyer, productToBuy, quantity);
                            if (success) {
                                JOptionPane.showMessageDialog(mainFrame, "Produit ajouté au panier !");
                                buyerPanel.updateProductList(db.getProducts()); // mise à jour affichage
                            } else {
                                JOptionPane.showMessageDialog(mainFrame, "Stock insuffisant !");
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(mainFrame, "Erreur d'entrée : " + ex.getMessage());
                        }
                    });

                    buyerPanel.getCheckoutButton().addActionListener(ev -> {
                        cartController.checkout(buyer);
                    });


                    mainFrame.showPanel("buyer");

                } else if (user instanceof Seller seller) {
                    SellerPanel sellerPanel = new SellerPanel(seller);
                    mainFrame.addPanel(sellerPanel, "seller");

                    sellerPanel.getRefreshButton().addActionListener(ev -> {
                        sellerPanel.updateProductList(seller);
                    });

                    sellerPanel.getAddProductButton().addActionListener(ev -> {
                        String name = JOptionPane.showInputDialog("Nom du produit :");
                        double price = Double.parseDouble(JOptionPane.showInputDialog("Prix :"));
                        int qty = Integer.parseInt(JOptionPane.showInputDialog("Quantité :"));

                        productController.addProduct(seller, name, price, qty);
                        sellerPanel.updateProductList(seller);
                    });

                    mainFrame.showPanel("seller");
                }
            });
        });
    }
}

