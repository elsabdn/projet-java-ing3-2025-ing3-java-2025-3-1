package Vue.advanced;

import Modele.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BuyerPanel extends JPanel {
    private JTextArea productArea;
    private JButton refreshBtn;
    private JButton viewCartBtn;
    private JButton buyProductBtn;
    private JButton checkoutButton;



    public BuyerPanel(List<Product> products) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("🛒 Produits disponibles");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        productArea = new JTextArea();
        productArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(productArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        refreshBtn = new JButton("🔄 Rafraîchir");
        viewCartBtn = new JButton("🧺 Voir le panier");
        buyProductBtn = new JButton("🛍️ Acheter un produit");
        checkoutButton = new JButton("💳 Valider le panier");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewCartBtn);
        buttonPanel.add(buyProductBtn);
        buttonPanel.add(checkoutButton);

        add(buttonPanel, BorderLayout.SOUTH);

        updateProductList(products);
    }

    public void updateProductList(List<Product> products) {
        productArea.setText("");
        for (Product p : products) {
            productArea.append("🆔 " + p.getId() + " | " + p.getName() + " - " + p.getPrice() + "€ (Stock: " + p.getQuantity() + ")\n");
        }
    }

    public JButton getRefreshButton() {
        return refreshBtn;
    }

    public JButton getViewCartButton() {
        return viewCartBtn;
    }

    public JButton getBuyProductButton() {
        return buyProductBtn;
    }

    public JButton getCheckoutButton() {
        return checkoutButton;
    }


}
