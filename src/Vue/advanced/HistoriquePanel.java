package Vue.advanced;

import Modele.Acheteur;
import Modele.Commande;
import Modele.Panier;
import DAO.CommandeDAO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


/**
 * HistoriquePanel : Affiche la liste des commandes passées par l'utilisateur acheteur
 * avec détails (date, montant, statut, articles).
 */
public class HistoriquePanel extends JPanel {
    private final Acheteur acheteur;
    private final CommandeDAO commandeDAO;

    /**
     * Constructeur : construit dynamiquement l'interface avec les commandes de l'acheteur.
     */
    public HistoriquePanel(MainFrame mainFrame, Acheteur acheteur) {
        this.acheteur    = acheteur;
        this.commandeDAO = new CommandeDAO();

        setLayout(new BorderLayout());
        setOpaque(false);

        // ─── HEADER: titre et bouton de déconnexion ────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // filler gauche pour équilibrer
        JButton empty = createStyledButton("");
        empty.setVisible(false);
        empty.setPreferredSize(new Dimension(140,35));
        header.add(empty, BorderLayout.WEST);

        JLabel titre = new JLabel("📜 Historique de vos commandes", SwingConstants.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 20));
        titre.setForeground(new Color(92, 92, 92));
        header.add(titre, BorderLayout.CENTER);

        JButton deconnexion = createStyledButton("🚪 Déconnexion");
        deconnexion.setPreferredSize(new Dimension(140,35));
        deconnexion.addActionListener(e -> mainFrame.afficherPanel("acheteur"));
        header.add(deconnexion, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ─── CONTENU: récupération des commandes depuis la BDD ──────────────────────────────────
        List<Commande> commandes = commandeDAO.recupererCommandesParUtilisateurId(acheteur.getId());
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        // on ajoute 20px de marge en haut pour ne pas coller à l'entête
        listPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE);

        if (commandes.isEmpty()) {
            JLabel vide = new JLabel("🛑 Vous n’avez rien commandé encore…", SwingConstants.CENTER);
            vide.setFont(new Font("SansSerif", Font.ITALIC, 16));
            vide.setForeground(new Color(120,120,120));
            vide.setBorder(new EmptyBorder(50,0,50,0));
            listPanel.add(vide);

        } else {
            for (Commande c : commandes) {
                // ── Création de la "carte blanche" d’une commande ─────────────────────────────────────────
                JPanel carte = new JPanel(new BorderLayout(10,10));
                carte.setBackground(Color.WHITE);
                carte.setOpaque(true);
                carte.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(15,15,15,15),
                        BorderFactory.createLineBorder(new Color(200,200,200),1)
                ));
                carte.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
                carte.setPreferredSize(new Dimension(0, 140));

                // — Informations de la commande à gauche —
                JPanel info = new JPanel();
                info.setOpaque(false);
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

                JLabel lblStatut = new JLabel("LIVRÉ");
                lblStatut.setFont(new Font("SansSerif", Font.BOLD, 14));
                lblStatut.setForeground(new Color(34,139,34));
                info.add(lblStatut);

                info.add(Box.createVerticalStrut(4));
                JLabel lblDate = new JLabel(sdf.format(c.getDate()));
                lblDate.setFont(new Font("SansSerif", Font.PLAIN, 12));
                info.add(lblDate);

                info.add(Box.createVerticalStrut(8));
                JLabel lblTotal = new JLabel(String.format("Total : %.2f €", c.getMontant()));
                lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
                info.add(lblTotal);

                info.add(Box.createVerticalGlue());
                JLabel voir = new JLabel("<html><u>Voir la commande</u></html>");
                voir.setFont(new Font("SansSerif", Font.PLAIN, 12));
                voir.setForeground(new Color(33,33,255));
                voir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                voir.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        String key = "detail" + c.getId();
                        CommandeDetailPanel detail = new CommandeDetailPanel(mainFrame, c);
                        mainFrame.ajouterPanel(detail, key);
                        mainFrame.afficherPanel(key);

                    }
                });
                info.add(voir);

                carte.add(info, BorderLayout.WEST);

                // — Carousel d’images produits à droite —
                JPanel imgRow = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
                imgRow.setOpaque(false);
                for (Panier.Articles item : c.getItems()) {
                    String path = item.getProduit().getImageChemin();
                    Image img = redimensionnerImage(path, 80, 80);
                    if (img != null) {
                        JLabel pic = new JLabel(new ImageIcon(img));
                        pic.setToolTipText(item.getProduit().getNom()+" ×"+item.getQuantite());
                        imgRow.add(pic);
                    }
                }
                JScrollPane scImg = new JScrollPane(
                        imgRow,
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                );
                scImg.setBorder(null);
                // **forcer le fond blanc sous les images**
                scImg.getViewport().setBackground(Color.WHITE);
                scImg.setBackground(Color.WHITE);
                scImg.setPreferredSize(new Dimension(300,90));
                carte.add(scImg, BorderLayout.CENTER);

                listPanel.add(carte);
                listPanel.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane scroll = new JScrollPane(
                listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // ─── FOOTER: bouton retour ───────────────────────────────────────────────
        JButton retour = createStyledButton("⬅ Retour");
        retour.addActionListener(e -> mainFrame.afficherPanel("acheteur"));
        JPanel bas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bas.setOpaque(false);
        bas.setBorder(BorderFactory.createEmptyBorder(10,0,20,0));
        bas.add(retour);
        add(bas, BorderLayout.SOUTH);
    }

    /**
     * Fond d'écran en dégradé rose clair
     */
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
     * Redimensionne une image à la taille donnée (avec lissage)
     */
    private Image redimensionnerImage(String chemin, int w, int h) {
        if (chemin==null||chemin.isBlank()) return null;
        try {
            BufferedImage orig = ImageIO.read(new File(chemin));
            BufferedImage dst  = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = dst.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(orig,0,0,w,h,null);
            g2.dispose();
            return dst;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Boutons stylés en rose pastel avec effet hover
     */
    private JButton createStyledButton(String texte) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(new Color(248,187,208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(244,143,177));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(248,187,208));
            }
        });
        return btn;
    }
}
