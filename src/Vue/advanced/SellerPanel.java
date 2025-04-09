package Vue.advanced;

import Modele.Seller;

import javax.swing.*;
import java.awt.*;

public class SellerPanel extends JPanel {
    private JTextArea productListArea;
    private JButton addProductBtn;
    private JButton refreshBtn;

    public SellerPanel(Seller seller) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("📦 Tableau de bord vendeur");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        productListArea = new JTextArea();
        productListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(productListArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        addProductBtn = new JButton("➕ Ajouter un produit");
        refreshBtn = new JButton("🔄 Rafraîchir");
        bottomPanel.add(addProductBtn);
        bottomPanel.add(refreshBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        updateProductList(seller);
    }

    public void updateProductList(Seller seller) {
        productListArea.setText("");
        seller.getProductList().forEach(p -> {
            productListArea.append("🆔 " + p.getId() + " | " + p.getName() + " - " + p.getPrice() + "€ (Stock: " + p.getQuantity() + ")\n");
        });
    }

    public JButton getAddProductButton() {
        return addProductBtn;
    }

    public JButton getRefreshButton() {
        return refreshBtn;
    }
}