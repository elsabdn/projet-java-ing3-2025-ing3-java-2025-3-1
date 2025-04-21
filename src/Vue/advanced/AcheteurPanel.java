package Vue.advanced;

import Modele.Produit;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AcheteurPanel extends JPanel {
    private JPanel produitPanel;
    private JButton refreshBtn;
    private JButton viewPanierBtn;
    private JButton addProduitBtn;
    private List<Produit> panier; // Liste des produits dans le panier


    public AcheteurPanel(List<Produit> produits) {
        setLayout(new BorderLayout());
        setOpaque(false);

        this.panier = new ArrayList<>();

        // Titre
        JLabel title = new JLabel("Bienvenue !");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(92, 92, 92));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);



        // Panneau des produits
        produitPanel = new JPanel();
        produitPanel.setLayout(new GridLayout(0, 3, 20, 20)); // 3 colonnes
        produitPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        produitPanel.setBackground(new Color(245, 245, 245)); // fond gris clair

        JScrollPane scrollPane = new JScrollPane(produitPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Pour un scroll fluide

        add(scrollPane, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(252, 242, 246)); // Rose pâle
        refreshBtn = createStyledButton("🔄 Rafraîchir");
        viewPanierBtn = createStyledButton("🧺 Voir le panier");
        addProduitBtn = createStyledButton("🛍️ Ajouter un produit");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewPanierBtn);
        buttonPanel.add(addProduitBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        updateProduitList(produits);

        // Ajouter l'action du bouton "Voir le panier"
        viewPanierBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Créer et afficher le PanierPanel
                PanierPanel panierPanel = new PanierPanel(panier);
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(AcheteurPanel.this);
                frame.setContentPane(panierPanel);
                frame.revalidate();
                frame.repaint();
            }
        });
    }

    public void updateProduitList(List<Produit> produits) {
        produitPanel.removeAll();

        for (Produit p : produits) {
            JPanel productCard = creerCarteProduit(p);
            produitPanel.add(productCard);
        }

        revalidate();
        repaint();
    }

    private JPanel creerCarteProduit(Produit p) {
        JPanel carte = new JPanel();
        carte.setLayout(new BorderLayout());
        carte.setPreferredSize(new Dimension(250, 300));
        carte.setMaximumSize(new Dimension(250, 300)); // Taille maximale
        carte.setMinimumSize(new Dimension(250, 300)); // Taille minimale
        carte.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        carte.setBackground(Color.WHITE);

        // 🖼️ Image du produit
        if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
            Image image = redimensionnerImage(p.getImagePath(), 150, 150);
            JLabel imageLabel = new JLabel(new ImageIcon(image));

            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
            carte.add(imageLabel, BorderLayout.NORTH);
        }

        // 📝 Infos produit
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel id = new JLabel("ID : " + p.getId());
        id.setForeground(new Color(100, 100, 100));
        id.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nom = new JLabel(p.getNom());
        nom.setFont(new Font("SansSerif", Font.BOLD, 14));
        nom.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel prix = new JLabel(String.format("%.2f €", p.getPrix()));
        prix.setForeground(new Color(100, 100, 100));
        prix.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel stock = new JLabel("Stock : " + p.getQuantite());
        stock.setForeground(new Color(150, 150, 150));
        stock.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel marque = new JLabel("Marque : " + p.getMarque());
        marque.setForeground(new Color(150, 150, 150));
        marque.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(nom);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(id);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(prix);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(stock);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(marque);

        carte.add(infoPanel, BorderLayout.CENTER);

        return carte;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(248, 187, 208));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(244, 143, 177));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(248, 187, 208));
            }
        });

        return button;
    }

    public static Image redimensionnerImage(String cheminImage, int largeur, int hauteur) {
        try {
            BufferedImage imageOriginale = ImageIO.read(new File(cheminImage));
            BufferedImage imageRedimensionnee = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = imageRedimensionnee.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawImage(imageOriginale, 0, 0, largeur, hauteur, null);
            g2d.dispose();

            return imageRedimensionnee;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JButton getRefreshButton() {
        return refreshBtn;
    }

    public JButton getViewPanierButton() {
        return viewPanierBtn;
    }

    public JButton getBuyProduitButton() {
        return addProduitBtn;
    }


}
