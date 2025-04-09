package Vue;

import Modele.Cart;
import Modele.Product;

import javax.swing.*;
import java.awt.*;

public class CartView extends JFrame {
    private JTextArea cartArea;
    private JLabel totalLabel;

    public CartView(Cart cart) {
        setTitle("Mon Panier");
        setSize(400, 300);
        setLocationRelativeTo(null);

        cartArea = new JTextArea();
        cartArea.setEditable(false);
        totalLabel = new JLabel();

        for (Product p : cart.getItems().keySet()) {
            int qte = cart.getItems().get(p);
            cartArea.append(p.getName() + " x" + qte + "\n");
        }

        totalLabel.setText("Total : " + cart.getTotalPrice() + "€");

        setLayout(new BorderLayout());
        add(new JScrollPane(cartArea), BorderLayout.CENTER);
        add(totalLabel, BorderLayout.SOUTH);
    }
}
