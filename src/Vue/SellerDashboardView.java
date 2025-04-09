package Vue;

import Modele.Product;
import Modele.Seller;

import javax.swing.*;
import java.awt.*;

public class SellerDashboardView extends JFrame {
    private JTextArea productListArea;

    public SellerDashboardView(Seller seller) {
        setTitle("Tableau de bord vendeur");
        setSize(400, 300);
        setLocationRelativeTo(null);

        productListArea = new JTextArea();
        productListArea.setEditable(false);

        for (Product p : seller.getProductList()) {
            productListArea.append(
                    String.format("%s - %.2f€ - Qté: %d\n", p.getName(), p.getPrice(), p.getQuantity())
            );
        }

        add(new JScrollPane(productListArea), BorderLayout.CENTER);
    }
}

