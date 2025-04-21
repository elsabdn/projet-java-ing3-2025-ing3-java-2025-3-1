package Vue.advanced;

import Modele.Produit;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class PanierPanel extends JPanel {
    private JButton checkoutButton; // Bouton "Valider le panier"
    private List<Produit> panier; // Liste des produits dans le panier
    private JLabel totalLabel; // Affichage du total du panier


    public PanierPanel(List<Produit> panier) {
        this.panier = panier;

        // Créer un panneau pour le bandeau "Mon panier"
        JPanel bandeauPanier = new JPanel();
        bandeauPanier.setBackground(new Color(248, 188, 208)); // rose pâle
        bandeauPanier.setLayout(new FlowLayout(FlowLayout.LEFT));
        bandeauPanier.setPreferredSize(new Dimension(800, 50));

        JLabel labelBandeau = new JLabel("Mon panier");
        labelBandeau.setFont(new Font("SansSerif", Font.BOLD, 20));
        labelBandeau.setForeground(Color.WHITE);

// Ajouter le label au panneau bandeau
        bandeauPanier.add(labelBandeau);

// Vérification de l'ajout du panneau (débogage)
        System.out.println("Ajout du bandeau panier dans le panneau.");

        this.setLayout(new BorderLayout());

        SwingUtilities.invokeLater(() -> {
            JFrame fenetre = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (fenetre != null) {
                fenetre.repaint();
            }
        });

// Ajouter le bandeau dans la zone NORTH du BorderLayout
        add(bandeauPanier, BorderLayout.NORTH);

        // Forcer la mise à jour du contenu de la fenêtre
        revalidate();  // Recalcule la disposition des composants
        repaint();     // Raffraîchit l'affichage


        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // Panneau des produits dans le panier
        JPanel panierPanel = new JPanel();
        panierPanel.setLayout(new BoxLayout(panierPanel, BoxLayout.Y_AXIS));
        panierPanel.setBackground(new Color(255, 255, 255));

        // Vérifier si le panier est vide et afficher un message approprié
        if (panier.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);

            JLabel messageVide = new JLabel("🛒 Votre panier est vide !");
            messageVide.setFont(new Font("SansSerif", Font.BOLD, 28));
            messageVide.setForeground(new Color(120, 120, 120));

            emptyPanel.add(messageVide, new GridBagConstraints());
            add(emptyPanel, BorderLayout.CENTER); // Affiche le message vide
        } else {
            for (Produit p : panier) {
                JPanel productCard = creerCarteProduit(p);
                panierPanel.add(productCard);
            }
            JScrollPane scrollPane = new JScrollPane(panierPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            add(scrollPane, BorderLayout.CENTER); // Affiche les produits
        }

        // 📦 ENCADRÉ À DROITE : résumé + bouton
        JPanel resumePanel = new JPanel();
        resumePanel.setLayout(new BoxLayout(resumePanel, BoxLayout.Y_AXIS));
        resumePanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20, 20, 20, 20),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));
        resumePanel.setBackground(new Color(250, 250, 250));
        resumePanel.setMaximumSize(new Dimension(250, 220));

        int nombreArticles = panier.size();
        double total = panier.stream().mapToDouble(Produit::getPrix).sum();

        JLabel labelTitre = new JLabel("Résumé de la commande");
        labelTitre.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelTitre.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelTitre.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel labelArticles = new JLabel("Articles : " + nombreArticles);
        labelArticles.setFont(new Font("SansSerif", Font.PLAIN, 16));
        labelArticles.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelTotal = new JLabel(String.format("Total : %.2f €", total));
        labelTotal.setFont(new Font("SansSerif", Font.PLAIN, 16));
        labelTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelTotal.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Bouton retour (placé juste avant "Valider la commande")
        JButton boutonRetour = new JButton("⬅️ Retour aux produits");
        boutonRetour.setAlignmentX(Component.CENTER_ALIGNMENT);
        boutonRetour.setBackground(new Color(248, 188, 208)); // rose pâle
        boutonRetour.setForeground(Color.WHITE);
        boutonRetour.setFocusPainted(false);
        boutonRetour.setFont(new Font("SansSerif", Font.BOLD, 14));
        boutonRetour.setPreferredSize(new Dimension(200, 40));
        boutonRetour.setMaximumSize(new Dimension(200, 40));

        boutonRetour.addActionListener(e -> {
            Container parent = SwingUtilities.getWindowAncestor(this);
            if (parent instanceof JFrame) {
                JFrame fenetre = (JFrame) parent;
                fenetre.setContentPane(new AcheteurPanel(panier));
                fenetre.revalidate();
                fenetre.repaint();
            }
        });


        JButton boutonValider = new JButton("Valider la commande");
        boutonValider.setAlignmentX(Component.CENTER_ALIGNMENT);
        boutonValider.setBackground(new Color(34, 139, 34));
        boutonValider.setForeground(Color.WHITE);
        boutonValider.setFocusPainted(false);
        boutonValider.setFont(new Font("SansSerif", Font.BOLD, 14));
        boutonValider.setPreferredSize(new Dimension(200, 40));
        boutonValider.setMaximumSize(new Dimension(200, 40));

        boutonValider.addActionListener(e -> {
            // Action à définir
            JOptionPane.showMessageDialog(this, "Commande validée !");
        });

        resumePanel.add(labelTitre);
        resumePanel.add(Box.createVerticalStrut(10));
        resumePanel.add(labelArticles);
        resumePanel.add(labelTotal);
        resumePanel.add(Box.createVerticalGlue());
        resumePanel.add(Box.createVerticalStrut(20));
        resumePanel.add(boutonRetour);
        resumePanel.add(Box.createVerticalStrut(20));
        resumePanel.add(boutonValider);



        add(resumePanel, BorderLayout.EAST);



        // Panneau du total
       /* JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BorderLayout());
        totalLabel = new JLabel("Total : " + getTotalPanier() + " €", SwingConstants.CENTER);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        totalPanel.add(totalLabel, BorderLayout.CENTER);
        totalPanel.setBackground(new Color(245, 245, 245));

        add(totalPanel, BorderLayout.NORTH);

        // Panneau du bouton de validation
        JPanel buttonPanel = new JPanel();
        checkoutButton = createStyledButton("💳 Valider le panier");
        buttonPanel.add(checkoutButton);
        buttonPanel.setBackground(new Color(245, 245, 245));

        add(buttonPanel, BorderLayout.SOUTH); */
    }

    public JButton getCheckoutButton() {
        return checkoutButton;
    }

    private JPanel creerCarteProduit(Produit produit) {
        JPanel carte = new JPanel();
        carte.setLayout(new BorderLayout());
        carte.setPreferredSize(new Dimension(400, 150));
        carte.setMaximumSize(new Dimension(400, 150));  // Taille maximale
        carte.setMinimumSize(new Dimension(400, 150));  // Taille minimale
        carte.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        carte.setBackground(Color.WHITE);

        // 🖼️ Image du produit
        if (produit.getImagePath() != null && !produit.getImagePath().isEmpty()) {
            Image image = redimensionnerImage(produit.getImagePath(), 100, 100);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
            carte.add(imageLabel, BorderLayout.WEST);
        }

        // 📝 Infos produit
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nom = new JLabel(produit.getNom());
        nom.setFont(new Font("SansSerif", Font.BOLD, 16));
        nom.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel prix = new JLabel(String.format("%.2f €", produit.getPrix()));
        prix.setForeground(new Color(100, 100, 100));
        prix.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel stock = new JLabel("Stock : " + produit.getQuantite());
        stock.setForeground(new Color(150, 150, 150));
        stock.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Quantité (changer quantité)
        JPanel quantityPanel = new JPanel();
        quantityPanel.setBackground(Color.WHITE);
        quantityPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel quantityLabel = new JLabel("Quantité : ");
        JTextField quantityField = new JTextField(3);
        quantityField.setText(String.valueOf(produit.getQuantite()));
        quantityField.setHorizontalAlignment(JTextField.CENTER);
        quantityField.setPreferredSize(new Dimension(50, 25));

        quantityPanel.add(quantityLabel);
        quantityPanel.add(quantityField);

        // Ajouter bouton de suppression du produit
        JButton deleteButton = new JButton("❌");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panier.remove(produit);
                updatePanier();
            }
        });

        infoPanel.add(nom);
        infoPanel.add(prix);
        infoPanel.add(stock);
        infoPanel.add(quantityPanel);
        infoPanel.add(deleteButton);

        carte.add(infoPanel, BorderLayout.CENTER);

        return carte;
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

    private String getTotalPanier() {
        double total = 0;
        for (Produit produit : panier) {
            total += produit.getPrix() * produit.getQuantite(); // Total en fonction de la quantité
        }

        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(total);
    }

    private void updatePanier() {
        // Recalculer le total du panier
        totalLabel.setText("Total : " + getTotalPanier() + " €");

        // Mettre à jour l'affichage des produits
        JPanel panierPanel = (JPanel) getComponent(0); // Récupérer le panneau contenant les produits
        panierPanel.removeAll();
        for (Produit p : panier) {
            JPanel productCard = creerCarteProduit(p);
            panierPanel.add(productCard);
        }

        revalidate();
        repaint();
    }
}
