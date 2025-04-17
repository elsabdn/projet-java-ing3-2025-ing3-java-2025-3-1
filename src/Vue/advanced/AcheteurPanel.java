package Vue.advanced;

import Modele.Produit;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AcheteurPanel extends JPanel {
    private JTextArea produitArea;
    private JButton refreshBtn;
    private JButton viewPanierBtn;
    private JButton buyProduitBtn;
    private JButton checkoutButton;



    public AcheteurPanel(List<Produit> produits) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("🛒 Produits disponibles");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        produitArea = new JTextArea();
        produitArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(produitArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        refreshBtn = new JButton("🔄 Rafraîchir");
        viewPanierBtn = new JButton("🧺 Voir le panier");
        buyProduitBtn = new JButton("🛍️ Acheter un produit");
        checkoutButton = new JButton("💳 Valider le panier");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewPanierBtn);
        buttonPanel.add(buyProduitBtn);
        buttonPanel.add(checkoutButton);

        add(buttonPanel, BorderLayout.SOUTH);

        updateProduitList(produits);
    }

    public void updateProduitList(List<Produit> produits) {
        produitArea.setText("");
        for (Produit p : produits) {
            produitArea.append("🆔 " + p.getId() + " | " + p.getNom() + " - " + p.getPrix() + "€ (Stock: " + p.getQuantite() + ")\n");
        }
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
