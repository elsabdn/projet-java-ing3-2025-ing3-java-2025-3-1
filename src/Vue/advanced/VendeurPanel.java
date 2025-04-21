package Vue.advanced;

import Modele.Vendeur;
import Controller.ProduitController;
import Modele.Produit;
import Vue.advanced.AccueilPanel;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class VendeurPanel extends JPanel {
    private ProduitController produitController;
    private JPanel produitDisplayPanel;
    private JButton addProduitBtn;
    private JButton refreshBtn;
    private JButton deconnexionBtn;
    private AccueilPanel accueilPanel;
    private float opacity = 0.0f;


    public VendeurPanel(Vendeur vendeur) {
        produitController = new ProduitController();

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
        JLabel title = new JLabel("Tableau de bord vendeur");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(92, 92, 92));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Zone de texte avec scroll
        produitDisplayPanel = new JPanel(); // très important
        produitDisplayPanel.setLayout(new GridLayout(0, 3, 20, 20)); // 3 colonnes
        produitDisplayPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        produitDisplayPanel.setBackground(new Color(245, 245, 245)); // optionnel : fond gris clair

        JScrollPane scrollPane = new JScrollPane(produitDisplayPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Pour un scroll fluide

        add(scrollPane, BorderLayout.CENTER);

        // Panneau de boutons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        addProduitBtn = createStyledButton("➕ Ajouter un produit");
        refreshBtn = createStyledButton("🔄 Rafraîchir");
        deconnexionBtn = createStyledButton("🚪 Déconnexion");

        bottomPanel.add(addProduitBtn);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(deconnexionBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action pour le bouton déconnexion
        deconnexionBtn.addActionListener(e -> {
            /// Crée une nouvelle instance de AccueilPanel
            AccueilPanel accueilPanel = new AccueilPanel();

            // Obtenez la fenêtre principale (JFrame) qui contient ce panneau
            JFrame fenetre = (JFrame) SwingUtilities.getWindowAncestor(this);

            // Changer le contenu de la fenêtre pour afficher l'AccueilPanel
            fenetre.setContentPane(accueilPanel);

            // Revalidate et repaint pour que la fenêtre se mette à jour avec le nouvel AccueilPanel
            fenetre.revalidate();
            fenetre.repaint();
            fenetre.setVisible(true);

        });


        // Affichage des produits
        updateProduitList(vendeur);

        addProduitBtn.addActionListener(ev -> {
            String nom = JOptionPane.showInputDialog("Nom du produit :");
            String marque = JOptionPane.showInputDialog("Marque du produit :");
            double prix = Double.parseDouble(JOptionPane.showInputDialog("Prix :"));
            int qte = Integer.parseInt(JOptionPane.showInputDialog("Quantité :"));

            // Demander à l'utilisateur de choisir une image
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choisir une image pour le produit");
            fileChooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
            fileChooser.addChoosableFileFilter(filter);

            int result = fileChooser.showOpenDialog(this);
            String imagePath = null;
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imagePath = selectedFile.getAbsolutePath();
            }

            // Ajouter le produit à la base de données, avec le chemin de l'image
            produitController.addProduit(vendeur, nom, prix, qte, imagePath, marque);

            // Mettre à jour la liste des produits après ajout
            updateProduitList(vendeur);
        });
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


    public void updateProduitList(Vendeur vendeur) {
        vendeur.setProduitList(produitController.getProduitsParVendeur(vendeur.getId()));
        produitDisplayPanel.removeAll();

        produitDisplayPanel.setLayout(new GridLayout(0, 3, 20, 20)); // 3 colonnes, espaces de 20px

        for (Produit p : vendeur.getProduitList()) {
            JPanel carte = new JPanel();
            carte.setLayout(new BorderLayout());
            carte.setPreferredSize(new Dimension(250, 350));
            carte.setMaximumSize(new Dimension(250, 350));  // Forcer la taille maximale
            carte.setMinimumSize(new Dimension(250, 350));  // Forcer la taille minimale
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

            JLabel nom = new JLabel(p.getNom());
            nom.setFont(new Font("SansSerif", Font.BOLD, 14));
            nom.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel id = new JLabel("ID : " + p.getId());
            id.setForeground(new Color(100, 100, 100));
            id.setAlignmentX(Component.CENTER_ALIGNMENT);

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

            // Panel pour les boutons "Modifier" et "Supprimer"
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setOpaque(false);  // Pour rendre le fond transparent

            JButton modifierBtn = createStyledButton("Modifier");
            modifierBtn.setPreferredSize(new Dimension(150, 30)); // Redimensionner le bouton "Modifier"

            modifierBtn.addActionListener(e -> {
                // Logique de modification (par exemple, demander de nouvelles infos)
                String newNom = JOptionPane.showInputDialog("Nom du produit :", p.getNom());
                double newPrix = Double.parseDouble(JOptionPane.showInputDialog("Prix :", p.getPrix()));
                int newStock = Integer.parseInt(JOptionPane.showInputDialog("Stock :", p.getQuantite()));
                String newMarque = JOptionPane.showInputDialog("Marque :", p.getMarque());

                // Demander si l'utilisateur veut changer l'image
                int response = JOptionPane.showConfirmDialog(this,
                        "Voulez-vous changer l'image du produit ?", "Modification de l'image",
                        JOptionPane.YES_NO_OPTION);

                String newImagePath = p.getImagePath(); // Garde l'ancien chemin d'image par défaut

                if (response == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Choisir une nouvelle image pour le produit");
                    fileChooser.setAcceptAllFileFilterUsed(false);
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg");
                    fileChooser.addChoosableFileFilter(filter);

                    int result = fileChooser.showOpenDialog(this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        newImagePath = selectedFile.getAbsolutePath(); // Mettre à jour avec le nouveau chemin
                    }
                }
                // Mettre à jour le produit avec les nouvelles informations
                p.setNom(newNom);
                p.setPrix(newPrix);
                p.setQuantite(newStock);
                p.setMarque(newMarque);
                p.setImagePath(newImagePath);

                // Mettre à jour la base de données si nécessaire
                produitController.updateProduit(p);

                // Recharger la liste des produits
                updateProduitList(vendeur);
            });

            JButton supprimerBtn = createStyledButton("Supprimer");
            supprimerBtn.setPreferredSize(new Dimension(150, 30));

            supprimerBtn.addActionListener(e -> {
                int confirmation = JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer ce produit ?", "Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    // Logique de suppression du produit
                    produitController.removeProduit(p.getVendeur(), p);
                    updateProduitList(vendeur); // Recharger la liste des produits
                }
            });

            buttonPanel.add(modifierBtn);
            buttonPanel.add(supprimerBtn);

            // Ajouter les boutons sous les informations
            carte.add(buttonPanel, BorderLayout.SOUTH);
            produitDisplayPanel.add(carte);

        }

        produitDisplayPanel.revalidate();
        produitDisplayPanel.repaint();
    }




    public JButton getAddProduitButton() {
        return addProduitBtn;
    }

    public JButton getRefreshButton() {
        return refreshBtn;
    }
}
