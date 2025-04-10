package Vue.advanced;

import Modele.Vendeur;

import javax.swing.*;
import java.awt.*;

public class VendeurPanel extends JPanel {
    private JTextArea produitListArea;
    private JButton addProduitBtn;
    private JButton refreshBtn;

    public VendeurPanel(Vendeur vendeur) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("📦 Tableau de bord vendeur");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        produitListArea = new JTextArea();
        produitListArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(produitListArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        addProduitBtn = new JButton("➕ Ajouter un produit");
        refreshBtn = new JButton("🔄 Rafraîchir");
        bottomPanel.add(addProduitBtn);
        bottomPanel.add(refreshBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        updateProduitList(vendeur);
    }

    public void updateProduitList(Vendeur vendeur) {
        produitListArea.setText("");
        vendeur.getProduitList().forEach(p -> {
            produitListArea.append("🆔 " + p.getId() + " | " + p.getName() + " - " + p.getPrice() + "€ (Stock: " + p.getQuantite() + ")\n");
        });
    }

    public JButton getAddProduitButton() {
        return addProduitBtn;
    }

    public JButton getRefreshButton() {
        return refreshBtn;
    }
}