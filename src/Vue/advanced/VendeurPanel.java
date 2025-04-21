package Vue.advanced;

import Modele.Vendeur;
import Modele.Produit;
import Controller.ProduitController;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VendeurPanel extends JPanel {
    private final ProduitController produitController;
    private final JPanel produitDisplayPanel;
    private final JButton addProduitBtn;
    private final JButton refreshBtn;
    private final JButton deconnexionBtn;
    private final MainFrame mainFrame;
    private float opacity = 0.0f;

    public VendeurPanel(Vendeur vendeur, MainFrame mainFrame) {
        this.produitController = new ProduitController();
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setOpaque(false);

        // ─── HEADER : filler à gauche, titre centré, bouton Déconnexion à droite ───
        deconnexionBtn = createStyledButton("🚪 Déconnexion");
        deconnexionBtn.setPreferredSize(new Dimension(140, 35));
        deconnexionBtn.addActionListener(e -> mainFrame.showPanel("accueil"));
        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutWrapper.setOpaque(false);
        logoutWrapper.add(deconnexionBtn);

        Dimension eastSize = logoutWrapper.getPreferredSize();
        JPanel leftFiller = new JPanel(); leftFiller.setOpaque(false);
        leftFiller.setPreferredSize(eastSize);

        JLabel title = new JLabel("Tableau de bord vendeur");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(92, 92, 92));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        header.add(leftFiller,    BorderLayout.WEST);
        header.add(title,         BorderLayout.CENTER);
        header.add(logoutWrapper, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ─── FADE‑IN ────────────────────────────────────────────────────────
        new Timer(15, e -> {
            if (opacity < 1.0f) {
                opacity += 0.02f;
                repaint();
            } else {
                ((Timer)e.getSource()).stop();
            }
        }).start();

        // ─── ZONE PRODUITS DÉFILANTE ───────────────────────────────────────
        produitDisplayPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        produitDisplayPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        produitDisplayPanel.setBackground(new Color(245, 245, 245));
        JScrollPane scroll = new JScrollPane(produitDisplayPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ─── PIED : Ajouter / Rafraîchir ───────────────────────────────────
        addProduitBtn = createStyledButton("➕ Ajouter un produit");
        refreshBtn    = createStyledButton("🔄 Rafraîchir");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        bottom.add(addProduitBtn);
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);

        // ─── ACTION : ajouter un produit (avec description) ────────────────
        addProduitBtn.addActionListener(ev -> {
            String nom     = JOptionPane.showInputDialog(this, "Nom du produit :");
            String marque  = JOptionPane.showInputDialog(this, "Marque :");
            double prix    = Double.parseDouble(
                    JOptionPane.showInputDialog(this, "Prix (€) :")
            );
            int qte        = Integer.parseInt(
                    JOptionPane.showInputDialog(this, "Quantité :")
            );
            String desc    = JOptionPane.showInputDialog(this, "Description :");

            // Choix de l'image
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Choisir une image pour le produit");
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(
                    new FileNameExtensionFilter("Images", "jpg","png","jpeg")
            );
            String imgPath = null;
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                imgPath = fc.getSelectedFile().getAbsolutePath();
            }

            // Ajout en base (en supposant addProduit accepte maintenant description)
            produitController.addProduit(vendeur, nom, prix, qte, imgPath, marque, desc);
            updateProduitList(vendeur);
        });

        // ─── ACTION : rafraîchir la liste ──────────────────────────────────
        refreshBtn.addActionListener(e -> updateProduitList(vendeur));

        // ─── Chargement initial des produits ───────────────────────────────
        updateProduitList(vendeur);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g.create();
        Color c1 = new Color(253, 243, 247);
        Color c2 = new Color(252, 228, 236);
        g2d.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    /**
     * Met à jour l’affichage des produits avec image, infos, marque, prix, stock et description.
     */
    public void updateProduitList(Vendeur vendeur) {
        produitDisplayPanel.removeAll();
        vendeur.setProduitList(
                produitController.getProduitsParVendeur(vendeur.getId())
        );

        for (Produit p : vendeur.getProduitList()) {
            JPanel carte = new JPanel(new BorderLayout());
            carte.setPreferredSize(new Dimension(250, 400));
            carte.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            carte.setBackground(Color.WHITE);

            // --- Image ---
            if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
                JLabel imgLbl = new JLabel(
                        new ImageIcon(redimensionnerImage(p.getImagePath(), 150, 150))
                );
                imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
                imgLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
                carte.add(imgLbl, BorderLayout.NORTH);
            }

            // --- Infos produit + description ---
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            infoPanel.setBackground(Color.WHITE);

            JLabel lblNom   = new JLabel(p.getNom());
            lblNom.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblNom.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(lblNom);

            infoPanel.add(Box.createVerticalStrut(5));
            JLabel lblId    = new JLabel("ID : " + p.getId());
            lblId.setForeground(new Color(100, 100, 100));
            lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(lblId);

            infoPanel.add(Box.createVerticalStrut(5));
            JLabel lblPrix  = new JLabel(String.format("Prix : %.2f €", p.getPrix()));
            lblPrix.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(lblPrix);

            infoPanel.add(Box.createVerticalStrut(5));
            JLabel lblStock = new JLabel("Stock : " + p.getQuantite());
            lblStock.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(lblStock);

            infoPanel.add(Box.createVerticalStrut(5));
            JLabel lblMarque= new JLabel("Marque : " + p.getMarque());
            lblMarque.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(lblMarque);

            infoPanel.add(Box.createVerticalStrut(10));
            // limite à 300 caractères et protège contre null
            String fullDesc = p.getDescription() != null ? p.getDescription() : "";
            int maxChars = 100;
            String shortDesc = fullDesc.length() > maxChars
                    ? fullDesc.substring(0, maxChars) + "…"
                    : fullDesc;
            JTextArea txtDesc = new JTextArea(shortDesc);

            txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
            txtDesc.setLineWrap(true);
            txtDesc.setWrapStyleWord(true);
            txtDesc.setEditable(false);
            txtDesc.setBackground(new Color(245, 245, 245));
            txtDesc.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            txtDesc.setMaximumSize(new Dimension(230, 80));
            infoPanel.add(txtDesc);

            // --- Boutons Modifier / Supprimer ---
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            btnPanel.setOpaque(false);

            JButton modBtn = createStyledButton("Modifier");
            modBtn.setPreferredSize(new Dimension(100, 30));
            modBtn.addActionListener(e -> {
                // On réutilise la boîte de dialogue
                ModifierProduitDialog dlg = new ModifierProduitDialog(mainFrame, p);
                dlg.setVisible(true);
                if (!dlg.isConfirme()) return;

                // Récupération des modifications
                p.setNom(dlg.getNomModifie());
                p.setPrix(dlg.getPrixModifie());
                p.setQuantite(dlg.getStockModifie());
                p.setMarque(dlg.getMarqueModifiee());
                if (dlg.getCheminImageModifie() != null)
                    p.setImagePath(dlg.getCheminImageModifie());
                if (dlg.getDescriptionModifie() != null)
                    p.setDescription(dlg.getDescriptionModifie());

                produitController.updateProduit(p);
                updateProduitList(vendeur);
            });

            JButton supBtn = createStyledButton("Supprimer");
            supBtn.setPreferredSize(new Dimension(100, 30));
            supBtn.addActionListener(e -> {
                produitController.removeProduit(vendeur, p);
                updateProduitList(vendeur);
            });

            btnPanel.add(modBtn);
            btnPanel.add(supBtn);
            infoPanel.add(Box.createVerticalStrut(10));
            infoPanel.add(btnPanel);

            carte.add(infoPanel, BorderLayout.CENTER);
            produitDisplayPanel.add(carte);
        }

        produitDisplayPanel.revalidate();
        produitDisplayPanel.repaint();
    }

    /** Crée un bouton stylisé pastel */
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(new Color(248, 187, 208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
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

    /**
     * Redimensionne une image depuis un chemin.
     */
    public static Image redimensionnerImage(String chemin, int w, int h) {
        try {
            BufferedImage orig = ImageIO.read(new File(chemin));
            BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
            g2d.drawImage(orig, 0, 0, w, h, null);
            g2d.dispose();
            return resized;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Getters pour tests ou utilisation extérieure
    public JButton getAddProduitButton()  { return addProduitBtn; }
    public JButton getRefreshButton()     { return refreshBtn; }
    public JButton getDeconnexionButton() { return deconnexionBtn; }
}
