package Vue.advanced;

import Modele.Acheteur;
import Modele.Produit;
import Modele.Commande;
import DAO.CommandeDAO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AcheteurPanel extends JPanel {
    private final MainFrame mainFrame;
    private final List<Produit> panier;
    private final JPanel produitPanel;
    private final JButton refreshBtn;
    private final JButton viewPanierBtn;
    private final JButton deconnexionBtn;

    public AcheteurPanel(MainFrame mainFrame, List<Produit> produits) {
        this.mainFrame = mainFrame;
        this.panier = new ArrayList<>();

        setLayout(new BorderLayout());
        setOpaque(false);

        // ─── En-tête ──────────────────────────────────────────────
        deconnexionBtn = createStyledButton("🚪 Déconnexion");
        deconnexionBtn.setPreferredSize(new Dimension(140, 35));
        deconnexionBtn.addActionListener(e -> mainFrame.showPanel("accueil"));

        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutWrapper.setOpaque(false);
        logoutWrapper.add(deconnexionBtn);

        Dimension eastSize = logoutWrapper.getPreferredSize();
        JPanel leftFiller = new JPanel();
        leftFiller.setOpaque(false);
        leftFiller.setPreferredSize(eastSize);

        JLabel titre = new JLabel("Bienvenue !");
        titre.setFont(new Font("SansSerif", Font.BOLD, 20));
        titre.setForeground(new Color(92, 92, 92));
        titre.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        header.add(leftFiller, BorderLayout.WEST);
        header.add(titre, BorderLayout.CENTER);
        header.add(logoutWrapper, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ─── Grille produits ───────────────────────────────────────
        produitPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        produitPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        produitPanel.setBackground(new Color(245, 245, 245));
        JScrollPane scroll = new JScrollPane(produitPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ─── Bas de page : boutons ────────────────────────────────
        refreshBtn = createStyledButton("🔄 Rafraîchir");
        viewPanierBtn = createStyledButton("🧺 Voir le panier");
        JButton historiqueBtn = createStyledButton("📜 Historique");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        bottom.add(refreshBtn);
        bottom.add(viewPanierBtn);
        bottom.add(historiqueBtn);
        add(bottom, BorderLayout.SOUTH);

        // ─── Actions des boutons ──────────────────────────────────
        refreshBtn.addActionListener(e -> updateProduitList(produits));

        viewPanierBtn.addActionListener(e -> {
            PanierPanel panelPanier = new PanierPanel(mainFrame, panier);
            mainFrame.addPanel(panelPanier, "panier");
            mainFrame.showPanel("panier");
        });

        historiqueBtn.addActionListener(e -> {
            Acheteur acheteur = mainFrame.getAcheteurConnecte();

            if (acheteur == null) {
                JOptionPane.showMessageDialog(mainFrame,
                        "❌ Erreur : aucun utilisateur connecté.");
                return;
            }

            CommandeDAO dao = new CommandeDAO();
            List<Commande> commandes = dao.getCommandesByUtilisateurId(acheteur.getId());

            if (commandes.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,
                        "😅 Vous n'avez encore rien commandé ! C’est le moment de vous faire plaisir.");
            } else {
                HistoriquePanel historiquePanel = new HistoriquePanel(acheteur);
                mainFrame.addPanel(historiquePanel, "historique");
                mainFrame.showPanel("historique");
            }
        });

        // ─── Chargement initial des produits ──────────────────────
        updateProduitList(produits);
    }

    public void updateProduitList(List<Produit> produits) {
        produitPanel.removeAll();
        for (Produit p : produits) {
            JPanel carte = creerCarteProduit(p);
            carte.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            carte.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ProduitDetailPanel detail = new ProduitDetailPanel(mainFrame, p, panier);
                    String key = "detail" + p.getId();
                    mainFrame.addPanel(detail, key);
                    mainFrame.showPanel(key);
                }
            });
            produitPanel.add(carte);
        }
        produitPanel.revalidate();
        produitPanel.repaint();
    }

    private JPanel creerCarteProduit(Produit p) {
        JPanel carte = new JPanel(new BorderLayout());
        carte.setPreferredSize(new Dimension(250, 300));
        carte.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        carte.setBackground(Color.WHITE);

        if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
            ImageIcon ico = new ImageIcon(redimensionnerImage(p.getImagePath(), 150, 150));
            JLabel imgLbl = new JLabel(ico);
            imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
            imgLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
            carte.add(imgLbl, BorderLayout.NORTH);
        }

        JPanel infos = new JPanel();
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infos.setBackground(Color.WHITE);

        JLabel lblNom = new JLabel(p.getNom());
        lblNom.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNom.setAlignmentX(Component.CENTER_ALIGNMENT);
        infos.add(lblNom);

        infos.add(Box.createVerticalStrut(5));
        JLabel lblPrix = new JLabel(String.format("%.2f €", p.getPrix()));
        lblPrix.setAlignmentX(Component.CENTER_ALIGNMENT);
        infos.add(lblPrix);

        infos.add(Box.createVerticalStrut(5));
        JLabel lblMarque = new JLabel("Marque : " + p.getMarque());
        lblMarque.setAlignmentX(Component.CENTER_ALIGNMENT);
        infos.add(lblMarque);

        carte.add(infos, BorderLayout.CENTER);
        return carte;
    }

    public static Image redimensionnerImage(String chemin, int w, int h) {
        try {
            if (chemin == null || chemin.isEmpty()) {
                throw new IOException("Chemin vide");
            }

            BufferedImage orig;
            if (chemin.startsWith("http")) {
                orig = ImageIO.read(new URL(chemin));
            } else {
                orig = ImageIO.read(new File(chemin));
            }

            if (orig == null) {
                throw new IOException("Image introuvable ou invalide");
            }

            BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(orig, 0, 0, w, h, null);
            g2d.dispose();
            return resized;

        } catch (IOException ex) {
            System.out.println("Erreur chargement image : " + chemin);
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
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

    public JButton getRefreshButton() { return refreshBtn; }
    public JButton getViewPanierButton() { return viewPanierBtn; }
}