package Vue.advanced;

import Modele.Produit;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AcheteurPanel extends JPanel {
    private JPanel produitPanel;
    private JButton refreshBtn;
    private JButton viewPanierBtn;
    private JButton buyProduitBtn;
    private JButton checkoutButton;

    public AcheteurPanel(List<Produit> produits) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Initialisation du panneau de produits
        produitPanel = new JPanel();
        produitPanel.setLayout(new GridLayout(0, 2, 10, 10)); // Grille de 2 colonnes
        produitPanel.setBackground(new Color(253, 243, 247)); // Fond rose pâle

        JScrollPane scrollPane = new JScrollPane(produitPanel);
        scrollPane.setPreferredSize(new Dimension(480, 350));  // Taille ajustée pour les produits
        add(scrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        refreshBtn = createStyledButton("🔄 Rafraîchir");
        viewPanierBtn = createStyledButton("🧺 Voir le panier");
        buyProduitBtn = createStyledButton("🛍️ Acheter un produit");
        checkoutButton = createStyledButton("💳 Valider le panier");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewPanierBtn);
        buttonPanel.add(buyProduitBtn);
        buttonPanel.add(checkoutButton);

        add(buttonPanel, BorderLayout.SOUTH);

        updateProduitList(produits); // Mise à jour de la liste des produits
    }

    // Mise à jour de la liste des produits
    public void updateProduitList(List<Produit> produits) {
        produitPanel.removeAll();  // Suppression des anciens produits pour réactualiser

        for (Produit p : produits) {
            JPanel productCard = createProductCard(p);
            produitPanel.add(productCard);
        }

        revalidate();
        repaint(); // Mise à jour de l'affichage
    }

    // Créer une carte de produit avec informations
    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // Utiliser BoxLayout pour une disposition verticale
        card.setBackground(new Color(248, 187, 208)); // Fond pastel
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setPreferredSize(new Dimension(200, 180));

        JLabel nameLabel = new JLabel(produit.getNom());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(Color.BLACK);

        JLabel idLabel = new JLabel("ID: " + produit.getId());  // Ajouter l'ID du produit
        idLabel.setFont(new Font("Arial", Font.ITALIC, 12));  // Police italique pour l'ID
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrer l'ID
        idLabel.setForeground(Color.BLACK);

        JLabel priceLabel = new JLabel("Prix: " + produit.getPrix() + "€");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceLabel.setForeground(Color.BLACK);

        JLabel stockLabel = new JLabel("Stock: " + produit.getQuantite());
        stockLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer le texte du stock
        stockLabel.setForeground(Color.BLACK);

        // Ajouter les informations dans la carte
        card.add(nameLabel);
        card.add(idLabel);
        card.add(priceLabel);
        card.add(stockLabel);


        return card;
    }

    // 🌸 Boutons pastel doux
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(248, 187, 208)); // #f8bbd0
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(244, 143, 177)); // #f48fb1
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(248, 187, 208)); // #f8bbd0
            }
        });

        return button;
    }

    public JButton getRefreshButton() {
        return refreshBtn;
    }

    public JButton getViewPanierButton() {
        return viewPanierBtn;
    }

    public JButton getBuyProduitButton() {
        return buyProduitBtn;
    }

    public JButton getCheckoutButton() {
        return checkoutButton;
    }
}
