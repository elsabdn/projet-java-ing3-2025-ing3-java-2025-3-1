package Vue;

import Modele.Produit;
import Modele.Vendeur;

import javax.swing.*;
import java.awt.*;

public class VendeurTabDeBordVue extends JFrame {
    private JTextArea produitListArea;

    public VendeurTabDeBordVue(Vendeur vendeur) {
        setTitle("Tableau de bord vendeur");
        setSize(400, 300);
        setLocationRelativeTo(null);

        produitListArea = new JTextArea();
        produitListArea.setEditable(false);

        for (Produit p : vendeur.getProduitList()) {
            produitListArea.append(
                    String.format("%s - %.2f€ - Qté: %d\n", p.getNom(), p.getPrix(), p.getQuantite())
            );
        }

        add(new JScrollPane(produitListArea), BorderLayout.CENTER);
    }
}

