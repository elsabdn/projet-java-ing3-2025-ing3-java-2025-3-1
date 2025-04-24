package Vue.advanced;

import Modele.Produit;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Panneau de détail d’un produit unique, avec description,
 * sélection de quantité et bouton stylisé.
 */
public class ProduitDetailPanel extends JPanel {
    private final JButton retourBtn;
    private final JButton ajouterPanierBtn;

    public ProduitDetailPanel(MainFrame mainFrame, Produit produit, List<Produit> panier) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // ─── En‑tête avec bouton Retour ──────────────────────────────────
        retourBtn = createStyledButton("← Retour aux produits");
        retourBtn.setPreferredSize(new Dimension(160, 35));
        retourBtn.addActionListener(e -> mainFrame.showPanel("acheteur"));
        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        north.setOpaque(false);
        north.add(retourBtn);
        add(north, BorderLayout.NORTH);

        // ─── Centre : image + bloc d’informations / description ──────────
        JPanel center = new JPanel(new BorderLayout(20, 20));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Image agrandie du produit
        if (produit.getImagePath() != null && !produit.getImagePath().isEmpty()) {
            Image img = AcheteurPanel.redimensionnerImage(produit.getImagePath(), 300, 300);
            JLabel imgLabel = new JLabel(new ImageIcon(img));
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            center.add(imgLabel, BorderLayout.WEST);
        }

        // Bloc infos + description
        JPanel infos = new JPanel();
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.setBackground(new Color(255, 255, 255));
        infos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Nom
        JLabel lblNom = new JLabel(produit.getNom());
        lblNom.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblNom.setAlignmentX(Component.LEFT_ALIGNMENT);
        infos.add(lblNom);

        infos.add(Box.createVerticalStrut(10));
        // Marque
        JLabel lblMarque = new JLabel("Marque : " + produit.getMarque());
        lblMarque.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblMarque.setAlignmentX(Component.LEFT_ALIGNMENT);
        infos.add(lblMarque);

        infos.add(Box.createVerticalStrut(5));
        // Prix
        JLabel lblPrix = new JLabel(String.format("Prix : %.2f €", produit.getPrix()));
        lblPrix.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblPrix.setAlignmentX(Component.LEFT_ALIGNMENT);
        infos.add(lblPrix);

        infos.add(Box.createVerticalStrut(5));
        // Stock
        JLabel lblStock = new JLabel("Stock disponible : " + produit.getQuantite());
        lblStock.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblStock.setAlignmentX(Component.LEFT_ALIGNMENT);
        infos.add(lblStock);

        infos.add(Box.createVerticalStrut(15));
        // Description
        JTextArea txtDesc = new JTextArea(produit.getDescription());
        txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setBackground(new Color(245, 245, 245));
        txtDesc.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txtDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        infos.add(txtDesc);

        center.add(infos, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // ─── Sélecteur de quantité + bouton Ajouter ──────────────────────
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        // Sélecteur de quantité
        JPanel quantitePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        quantitePanel.setOpaque(false);
        int[] quantite = {1};
        JLabel lblQuantite = new JLabel(String.valueOf(quantite[0]));
        lblQuantite.setPreferredSize(new Dimension(30, 30));
        lblQuantite.setHorizontalAlignment(SwingConstants.CENTER);
        JButton btnMinus = new JButton("–");
        btnMinus.setPreferredSize(new Dimension(45, 30));
        btnMinus.addActionListener(e -> {
            if (quantite[0] > 1) {
                quantite[0]--;
                lblQuantite.setText(String.valueOf(quantite[0]));
            }
        });
        JButton btnPlus = new JButton("+");
        btnPlus.setPreferredSize(new Dimension(45, 30));
        btnPlus.addActionListener(e -> {
            if (quantite[0] < produit.getQuantite()) {
                quantite[0]++;
                lblQuantite.setText(String.valueOf(quantite[0]));
            }
        });
        quantitePanel.add(new JLabel("Quantité :"));
        quantitePanel.add(btnMinus);
        quantitePanel.add(lblQuantite);
        quantitePanel.add(btnPlus);

        // Bouton Ajouter au panier
        ajouterPanierBtn = createStyledButton("🛒 Ajouter au panier");
        ajouterPanierBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        ajouterPanierBtn.setPreferredSize(new Dimension(200, 40));
        ajouterPanierBtn.addActionListener(e -> {
            // Ajouter 'quantite[0]' exemplaires du produit au panier
            for (int i = 0; i < quantite[0]; i++) {
                panier.add(produit);
            }
            JOptionPane.showMessageDialog(
                    this,
                    quantite[0] + " × « " + produit.getNom() + " » ajouté(s) au panier.",
                    "Ajout au panier",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        bottomPanel.add(quantitePanel);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(ajouterPanierBtn);
        bottomPanel.add(Box.createVerticalStrut(20));

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /** Crée un bouton pastel cohérent avec les autres panels */
    private JButton createStyledButton(String texte) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(new Color(248, 187, 208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(244, 143, 177));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(248, 187, 208));
            }
        });
        return btn;
    }

    // Accesseurs éventuels
    public JButton getRetourButton()        { return retourBtn; }
    public JButton getAjouterPanierButton(){ return ajouterPanierBtn; }
}

