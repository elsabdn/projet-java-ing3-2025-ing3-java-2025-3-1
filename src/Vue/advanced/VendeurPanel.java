package Vue.advanced;

import Modele.Vendeur;
import Modele.Produit;
import Controller.ProduitController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

/**
 * Panneau principal pour les vendeurs, affichant leurs produits
 * et permettant d’en ajouter un via une unique fenêtre de dialogue.
 */
public class VendeurPanel extends JPanel {
    private final ProduitController produitController;
    private final JPanel         produitDisplayPanel;
    private final JButton        addProduitBtn;
    private final JButton        refreshBtn;
    private final JButton        deconnexionBtn;
    private final MainFrame      mainFrame;
    private float                opacity = 0.0f;

    private final JButton consulterRapportBtn; // Nouveau bouton

    public VendeurPanel(Vendeur vendeur, MainFrame mainFrame) {
        this.produitController = new ProduitController();
        this.mainFrame         = mainFrame;

        setLayout(new BorderLayout());
        setOpaque(false);

        // ─── HEADER ───────────────────────────────────────────────
        deconnexionBtn = createStyledButton("🚪 Déconnexion");
        deconnexionBtn.setPreferredSize(new Dimension(140,35));
        deconnexionBtn.addActionListener(e -> mainFrame.afficherPanel("accueil"));

        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        logoutWrapper.setOpaque(false);
        logoutWrapper.add(deconnexionBtn);

        Dimension eastSize = logoutWrapper.getPreferredSize();
        JPanel leftFiller = new JPanel();
        leftFiller.setOpaque(false);
        leftFiller.setPreferredSize(eastSize);

        JLabel title = new JLabel("Tableau de bord vendeur", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD,20));
        title.setForeground(new Color(92, 92, 92));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        header.add(leftFiller,    BorderLayout.WEST);
        header.add(title,         BorderLayout.CENTER);
        header.add(logoutWrapper, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ─── FADE‑IN ───────────────────────────────────────────────
        new Timer(15, e -> {
            if (opacity < 1.0f) {
                opacity += 0.02f;
                repaint();
            } else {
                ((Timer)e.getSource()).stop();
            }
        }).start();

        // ─── ZONE PRODUITS ─────────────────────────────────────────
        produitDisplayPanel = new JPanel(new GridLayout(0,3,20,20));
        produitDisplayPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        produitDisplayPanel.setBackground(new Color(245,245,245));
        JScrollPane scroll = new JScrollPane(produitDisplayPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ─── FOOTER : boutons ─────────────────────────────────────
        addProduitBtn = createStyledButton("➕ Ajouter un produit");
        refreshBtn    = createStyledButton("🔄 Rafraîchir");
        consulterRapportBtn = createStyledButton("📊 Consulter Rapport"); // Nouveau bouton
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        bottom.add(addProduitBtn);
        bottom.add(refreshBtn);
        bottom.add(consulterRapportBtn); // Ajout du bouton
        add(bottom, BorderLayout.SOUTH);

        // ─── ACTION AJOUT ─────────────────────────────────────────
        addProduitBtn.addActionListener(ev -> {
            AjouterProduitDialog dlg = new AjouterProduitDialog(mainFrame);
            dlg.setVisible(true);
            if (!dlg.estConfirme()) return;

            String nom     = dlg.getNom();
            String marque  = dlg.getMarque();
            double prix    = dlg.getPrix();
            int qte        = dlg.getQuantite();
            String desc    = dlg.getDescription(); // description libre
            String imgPath = dlg.getCheminImage();

            produitController.ajouterProduit(vendeur, nom, prix, qte, imgPath, marque, desc, dlg.estPromoEnGros(), dlg.getSeuilGros(), dlg.getPrixGros());
            mettreAJourProduitList(vendeur);
        });

        // Action du bouton "Consulter Rapport"
        consulterRapportBtn.addActionListener(e -> {
            try {
                // Passe MainFrame en paramètre pour la navigation
                StatistiquesPanel statsPanel = new StatistiquesPanel(vendeur, mainFrame);
                mainFrame.ajouterPanel(statsPanel, "statistiques");
                mainFrame.afficherPanel("statistiques");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });


        // ─── ACTION RAFRAÎCHIR ────────────────────────────────────
        refreshBtn.addActionListener(e -> mettreAJourProduitList(vendeur));

        // Chargement initial
        mettreAJourProduitList(vendeur);
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g.create();
        Color c1 = new Color(253,243,247), c2 = new Color(252,228,236);
        g2.setPaint(new GradientPaint(0,0,c1,0,getHeight(),c2));
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.dispose();
    }

    /**
     * Met à jour la grille de produits du vendeur.
     * Description tronquée à 100 caractères max.
     */
    public void mettreAJourProduitList(Vendeur vendeur) {
        produitDisplayPanel.removeAll();
        vendeur.setProduitListe(
                produitController.recupererProduitsParVendeur(vendeur.getId())
        );

        for (Produit p : vendeur.getProduitListe()) {
            JPanel carte = new JPanel(new BorderLayout());
            carte.setBackground(Color.WHITE);
            carte.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10,10,10,10),
                    BorderFactory.createLineBorder(new Color(245, 245, 245))
            ));
            carte.setPreferredSize(new Dimension(250, 400));

            // Image
            if (p.getImageChemin() != null && !p.getImageChemin().isEmpty()) {
                ImageIcon ico = new ImageIcon(redimensionnerImage(p.getImageChemin(), 150,150));
                JLabel imgLbl = new JLabel(ico, SwingConstants.CENTER);
                imgLbl.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
                carte.add(imgLbl, BorderLayout.NORTH);
            }

            // Infos
            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            JLabel lblNom = new JLabel(p.getNom(), SwingConstants.CENTER);
            lblNom.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblNom.setAlignmentX(Component.CENTER_ALIGNMENT);
            info.add(lblNom);

            info.add(Box.createVerticalStrut(5));
            JLabel lblPrix = new JLabel(String.format("Prix : %.2f €", p.getPrix()));
            lblPrix.setAlignmentX(Component.CENTER_ALIGNMENT);
            info.add(lblPrix);

            info.add(Box.createVerticalStrut(5));
            JLabel lblStock = new JLabel("Stock : " + p.getQuantite());
            lblStock.setAlignmentX(Component.CENTER_ALIGNMENT);
            info.add(lblStock);

            info.add(Box.createVerticalStrut(5));
            JLabel lblMarque = new JLabel("Marque : " + p.getMarque());
            lblMarque.setAlignmentX(Component.CENTER_ALIGNMENT);
            info.add(lblMarque);

            // Affiche le prix de gros si activé
            if (p.estPromoEnGros()) {
                JLabel lblPromo = new JLabel(
                        String.format("Prix de gros : %.2f € pour %d unités",
                                p.getPrixGros(),
                                p.getSeuilGros()
                        )
                );
                lblPromo.setFont(new Font("SansSerif", Font.ITALIC, 13));
                lblPromo.setForeground(new Color(255, 34, 34)); // couleur orange
                lblPromo.setAlignmentX(Component.CENTER_ALIGNMENT);
                info.add(Box.createVerticalStrut(5));
                info.add(lblPromo);
            }


            // ── Description tronquée à 100 caractères ──
            info.add(Box.createVerticalStrut(10));
            String full = p.getDescription() != null ? p.getDescription() : "";
            String shortDesc = full.length() > 100
                    ? full.substring(0, 100) + "…"
                    : full;
            JTextArea txtDesc = new JTextArea(shortDesc);
            txtDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
            txtDesc.setLineWrap(true);
            txtDesc.setWrapStyleWord(true);
            txtDesc.setEditable(false);
            txtDesc.setBackground(new Color(245,245,245));
            txtDesc.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            txtDesc.setMaximumSize(new Dimension(350,80));
            info.add(txtDesc);

            // Boutons Modifier/Supprimer
            info.add(Box.createVerticalStrut(10));
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
            btnPanel.setOpaque(false);

            JButton modBtn = createStyledButton("Modifier");
            modBtn.setPreferredSize(new Dimension(100,30));
            modBtn.addActionListener(e -> {
                ModifierProduitDialog dlg = new ModifierProduitDialog(mainFrame, p);
                dlg.setVisible(true);
                if (!dlg.isConfirme()) return;

                p.setNom(dlg.getNomModifie());
                p.setPrix(dlg.getPrixModifie());
                p.setQuantite(dlg.getStockModifie());
                p.setMarque(dlg.getMarqueModifiee());
                if (dlg.getCheminImageModifie() != null) p.setImageChemin(dlg.getCheminImageModifie());
                if (dlg.getDescriptionModifie() != null) p.setDescription(dlg.getDescriptionModifie());

                produitController.mettreAJourProduit(p);
                mettreAJourProduitList(vendeur);
            });

            JButton supBtn = createStyledButton("Supprimer");
            supBtn.setPreferredSize(new Dimension(100,30));
            supBtn.addActionListener(e -> {
                produitController.supprimerProduit(vendeur, p);
                mettreAJourProduitList(vendeur);
            });

            btnPanel.add(modBtn);
            btnPanel.add(supBtn);
            info.add(btnPanel);

            carte.add(info, BorderLayout.CENTER);
            produitDisplayPanel.add(carte);
        }

        produitDisplayPanel.revalidate();
        produitDisplayPanel.repaint();
    }

    /** Redimensionne une image depuis un chemin ou URL */
    private static Image redimensionnerImage(String chemin, int w, int h) {
        try {
            BufferedImage orig;
            if (chemin.startsWith("http")) {
                orig = ImageIO.read(new URL(chemin));
            } else {
                orig = ImageIO.read(new File(chemin));
            }
            BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = dst.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(orig, 0, 0, w, h, null);
            g2.dispose();
            return dst;
        } catch (IOException ex) {
            ex.printStackTrace();
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }
    }

    /** Crée un bouton stylisé pastel */
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD,14));
        btn.setBackground(new Color(248,187,208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5,15,5,15));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(244,143,177));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(248,187,208));
            }
        });
        return btn;
    }

    // Getters pour tests ou utilisation extérieure
    public JButton getAjoutProduitButton()  { return addProduitBtn; }
    public JButton getRefreshButton()     { return refreshBtn; }
    public JButton getDeconnexionButton() { return deconnexionBtn; }
}