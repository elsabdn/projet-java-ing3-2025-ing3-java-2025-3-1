package Vue;

import Modele.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductListView extends JFrame {
    private JTextArea productArea;

    public ProductListView(List<Product> products) {
        setTitle("Produits disponibles");
        setSize(400, 300);
        setLocationRelativeTo(null);

        productArea = new JTextArea();
        productArea.setEditable(false);

        for (Product p : products) {
            productArea.append(
                    String.format("ID: %d | %s | %.2f€ | Qté: %d\n",
                            p.getId(), p.getName(), p.getPrice(), p.getQuantity())
            );
        }

        add(new JScrollPane(productArea), BorderLayout.CENTER);
    }
}
