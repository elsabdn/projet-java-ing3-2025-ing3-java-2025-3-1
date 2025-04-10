package Vue;

import Modele.Produit;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProduitListeVue extends JFrame {
    private JTextArea produitArea;

    public ProduitListeVue(List<Produit> produits) {
        setTitle("Produits disponibles");
        setSize(400, 300);
        setLocationRelativeTo(null);

        produitArea = new JTextArea();
        produitArea.setEditable(false);

        for (Produit p : produits) {
            produitArea.append(
                    String.format("ID: %d | %s | %.2f€ | Qté: %d\n",
                            p.getId(), p.getName(), p.getPrice(), p.getQuantite())
            );
        }

        add(new JScrollPane(produitArea), BorderLayout.CENTER);
    }
}
