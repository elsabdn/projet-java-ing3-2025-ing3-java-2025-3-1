package Vue;

import Modele.Panier;
import Modele.Produit;

import javax.swing.*;
import java.awt.*;

public class PanierVue extends JFrame {
    private JTextArea panierArea;
    private JLabel totalLabel;

    public PanierVue(Panier panier) {
        setTitle("Mon Panier");
        setSize(400, 300);
        setLocationRelativeTo(null);

        panierArea = new JTextArea();
        panierArea.setEditable(false);
        totalLabel = new JLabel();

        for (Panier.Item item : panier.getItems()) {
            Produit p = item.getProduit();
            int qte = item.getQuantite();
            panierArea.append(p.getNom() + " x" + qte + "\n");
        }


        totalLabel.setText("Total : " + panier.getPrixTot() + "€");

        setLayout(new BorderLayout());
        add(new JScrollPane(panierArea), BorderLayout.CENTER);
        add(totalLabel, BorderLayout.SOUTH);
    }
}
