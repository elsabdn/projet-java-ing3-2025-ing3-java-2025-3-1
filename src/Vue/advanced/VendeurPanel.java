package Vue.advanced;

import Modele.Vendeur;

import javax.swing.*;
import java.awt.*;

public class VendeurPanel extends JPanel {
    private JTextArea produitListArea;
    private JButton addProduitBtn;
    private JButton refreshBtn;
    private float opacity = 0.0f;

    public VendeurPanel(Vendeur vendeur) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Animation de fade-in
        Timer timer = new Timer(15, e -> {
            if (opacity < 1.0f) {
                opacity += 0.02f;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();

        // Titre
        JLabel title = new JLabel("📦 Tableau de bord vendeur");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(92, 92, 92));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Zone de texte avec scroll
        produitListArea = new JTextArea();
        produitListArea.setEditable(false);
        produitListArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        produitListArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(produitListArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(248, 187, 208), 2));
        add(scrollPane, BorderLayout.CENTER);

        // Panneau de boutons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        addProduitBtn = createStyledButton("➕ Ajouter un produit");
        refreshBtn = createStyledButton("🔄 Rafraîchir");

        bottomPanel.add(addProduitBtn);
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Affichage des produits
        updateProduitList(vendeur);
    }

    // 🎨 Fond dégradé rose
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        Color color1 = new Color(253, 243, 247); // #fdf3f7
        Color color2 = new Color(252, 228, 236); // #fce4ec
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    // 🌸 Boutons pastel stylisés
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(248, 187, 208)); // #f8bbd0
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(244, 143, 177)); // #f48fb1
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(248, 187, 208)); // #f8bbd0
            }
        });

        return button;
    }

    public void updateProduitList(Vendeur vendeur) {
        produitListArea.setText("");
        vendeur.getProduitList().forEach(p -> {
            produitListArea.append("🆔 " + p.getId() + " | " + p.getNom() + " - " + p.getPrix() + "€ (Stock: " + p.getQuantite() + ")\n");
        });
    }

    public JButton getAddProduitButton() {
        return addProduitBtn;
    }

    public JButton getRefreshButton() {
        return refreshBtn;
    }
}
