package Vue.advanced;

import Modele.Acheteur;
import Modele.Produit;
import Modele.Commande;
import DAO.CommandeDAO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel d'acheteur avec recherche filtrée et accès aux fonctionnalités
 * de panier et historique.
 */
public class AcheteurPanel extends JPanel {
    private final MainFrame mainFrame;
    private final List<Produit> allProduits;
    private final List<Produit> panier;

    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JPanel produitPanel;
    private JButton refreshBtn;
    private JButton viewPanierBtn;
    private JButton deconnexionBtn;

    public AcheteurPanel(MainFrame mainFrame, List<Produit> produits) {
        this.mainFrame   = mainFrame;
        this.allProduits = new ArrayList<>(produits);
        this.panier      = new ArrayList<>();

        setLayout(new BorderLayout());
        setOpaque(false);

        // ─── Header + Search ─────────────────────────────────────
        JPanel header      = construireEnTete();
        JPanel searchPanel = construirePanneauRecherche();

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.add(header);
        topContainer.add(searchPanel);

        add(topContainer, BorderLayout.NORTH);

        // ─── Grille produits ─────────────────────────────────────
        produitPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        produitPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        produitPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scroll = new JScrollPane(produitPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ─── Bas de page : boutons ────────────────────────────────
        refreshBtn    = createStyledButton("🔄 Rafraîchir");
        viewPanierBtn = createStyledButton("🧺 Voir le panier");
        JButton histoBtn = createStyledButton("📜 Historique");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        bottom.add(refreshBtn);
        bottom.add(viewPanierBtn);
        bottom.add(histoBtn);
        add(bottom, BorderLayout.SOUTH);

        // ─── Actions des boutons ──────────────────────────────────
        refreshBtn.addActionListener(e -> mettreAJourListeProduits(allProduits));
        viewPanierBtn.addActionListener(e -> {
            PanierPanel panelPanier = new PanierPanel(mainFrame, panier);
            mainFrame.ajouterPanel(panelPanier, "panier");
            mainFrame.afficherPanel("panier");
        });
        histoBtn.addActionListener(e -> {
            Acheteur acheteur = mainFrame.obtenirAcheteurConnecte();
            if (acheteur == null) {
                JOptionPane.showMessageDialog(mainFrame,
                        "❌ Erreur : aucun utilisateur connecté.");
                return;
            }
            CommandeDAO dao = new CommandeDAO();
            List<Commande> commandes = dao.recupererCommandesParUtilisateurId(acheteur.getId());
            if (commandes.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,
                        "😅 Vous n'avez encore rien commandé !");
            } else {
                HistoriquePanel historiquePanel = new HistoriquePanel(mainFrame, acheteur);
                mainFrame.ajouterPanel(historiquePanel, "historique");
                mainFrame.afficherPanel("historique");
            }
        });

        // ─── Chargement initial des produits ──────────────────────
        mettreAJourListeProduits(allProduits);
    }

    private JPanel construireEnTete() {
        deconnexionBtn = createStyledButton("🚪 Déconnexion");
        deconnexionBtn.setPreferredSize(new Dimension(140, 35));
        deconnexionBtn.addActionListener(e -> mainFrame.afficherPanel("accueil"));

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
        header.setBackground(new Color(255, 228, 235));  // rose pastel
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));
        header.add(leftFiller, BorderLayout.WEST);
        header.add(titre,      BorderLayout.CENTER);
        header.add(logoutWrapper, BorderLayout.EAST);

        return header;
    }

    private JPanel construirePanneauRecherche() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(true);
        wrapper.setBackground(new Color(245, 245, 245));
        wrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Recherche :"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        filterCombo = new JComboBox<>(new String[]{"Nom", "Marque", "Prix ≤"});
        searchPanel.add(filterCombo);

        DocumentListener docL = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrer(); }
            public void removeUpdate(DocumentEvent e)  { filtrer(); }
            public void changedUpdate(DocumentEvent e) { filtrer(); }
        };
        searchField.getDocument().addDocumentListener(docL);
        filterCombo.addActionListener(e -> filtrer());

        wrapper.add(searchPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private void filtrer() {
        String text = searchField.getText().trim().toLowerCase();
        String crit = (String) filterCombo.getSelectedItem();
        List<Produit> filtered = allProduits.stream().filter(p -> {
            switch (crit) {
                case "Nom":
                    return p.getNom().toLowerCase().contains(text);
                case "Marque":
                    return p.getMarque().toLowerCase().contains(text);
                case "Prix ≤":
                    try {
                        return p.getPrix() <= Double.parseDouble(text);
                    } catch (NumberFormatException ex) {
                        return true;
                    }
                default:
                    return true;
            }
        }).collect(Collectors.toList());
        mettreAJourListeProduits(filtered);
    }

    public void mettreAJourListeProduits(List<Produit> produits) {
        produitPanel.removeAll();
        for (Produit p : produits) {
            JPanel carte = creerCarteProduit(p);
            carte.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            carte.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ProduitDetailPanel detail = new ProduitDetailPanel(mainFrame, p, panier);
                    String key = "detail" + p.getId();
                    mainFrame.ajouterPanel(detail, key);
                    mainFrame.afficherPanel(key);
                }
            });
            produitPanel.add(carte);
        }
        produitPanel.revalidate();
        produitPanel.repaint();
    }

    private JPanel creerCarteProduit(Produit p) {
        JPanel carte = new JPanel(new BorderLayout());
        carte.setPreferredSize(new Dimension(250, 330));
        carte.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        carte.setBackground(Color.WHITE);

        if (p.getImageChemin() != null && !p.getImageChemin().isEmpty()) {
            ImageIcon ico = new ImageIcon(redimensionnerImage(p.getImageChemin(), 150, 150));
            JLabel imgLbl = new JLabel(ico);
            imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
            imgLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
            carte.add(imgLbl, BorderLayout.NORTH);
        }

        JPanel infos = new JPanel();
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infos.setBackground(Color.WHITE);

        JLabel lblNom    = new JLabel(p.getNom());
        JLabel lblPrix   = new JLabel(String.format("%.2f €", p.getPrix()));
        JLabel lblMarque = new JLabel("Marque : " + p.getMarque());
        for (JLabel l : new JLabel[]{lblNom, lblPrix, lblMarque}) {
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        lblNom.setFont(new Font("SansSerif", Font.BOLD, 14));

        infos.add(lblNom);
        infos.add(Box.createVerticalStrut(5));
        infos.add(lblPrix);
        infos.add(Box.createVerticalStrut(5));
        infos.add(lblMarque);

        // Affichage du prix de gros si applicable
        if (p.estPromoEnGros()) {
            infos.add(Box.createVerticalStrut(5));
            JLabel lblPromo = new JLabel(
                    String.format("Promo gros : %.2f € pour %d achetés", p.getPrixGros(), p.getSeuilGros())
            );
            lblPromo.setFont(new Font("SansSerif", Font.ITALIC, 12));
            lblPromo.setForeground(new Color(255, 34, 34));
            lblPromo.setAlignmentX(Component.CENTER_ALIGNMENT);
            infos.add(lblPromo);
        }

        carte.add(infos, BorderLayout.CENTER);
        return carte;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(new Color(248, 187, 208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(244, 143, 177)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(248, 187, 208)); }
        });
        return btn;
    }

    public static Image redimensionnerImage(String chemin, int w, int h) {
        try {
            BufferedImage orig = chemin.startsWith("http")
                    ? ImageIO.read(new URL(chemin))
                    : ImageIO.read(new File(chemin));
            BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(orig, 0, 0, w, h, null);
            g2d.dispose();
            return resized;
        } catch (IOException ex) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }
    }

    // Getters utiles pour les tests ou l’intégration
    public JButton obtenirBoutonRafraichir()    { return refreshBtn; }
    public JButton obtenirBoutonVoirPanier() { return viewPanierBtn; }
}
