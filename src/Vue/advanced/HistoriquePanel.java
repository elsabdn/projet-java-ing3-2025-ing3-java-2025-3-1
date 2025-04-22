package Vue.advanced;

import Modele.Acheteur;
import Modele.Commande;
import Modele.Panier;
import DAO.CommandeDAO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoriquePanel extends JPanel {
    private final Acheteur acheteur;
    private final CommandeDAO commandeDAO;

    public HistoriquePanel(Acheteur acheteur) {
        this.acheteur    = acheteur;
        this.commandeDAO = new CommandeDAO();

        setLayout(new BorderLayout());
        setOpaque(false);

        // ─── En‑tête ───────────────────────────────────────────────────────
        JLabel titre = new JLabel("📜 Historique de vos commandes", SwingConstants.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 20));
        titre.setForeground(new Color(92, 92, 92));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titre, BorderLayout.NORTH);

        // ─── Contenu : cartes de commande ────────────────────────────────
        List<Commande> commandes = commandeDAO.getCommandesByUtilisateurId(acheteur.getId());
        JPanel listeCartes = new JPanel();
        listeCartes.setOpaque(false);
        listeCartes.setLayout(new BoxLayout(listeCartes, BoxLayout.Y_AXIS));
        listeCartes.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        if (commandes.isEmpty()) {
            JLabel vide = new JLabel(
                    "🛑 Vous n’avez rien commandé encore… Qu’attendez-vous ?",
                    SwingConstants.CENTER
            );
            vide.setFont(new Font("SansSerif", Font.ITALIC, 16));
            vide.setForeground(new Color(120, 120, 120));
            vide.setBorder(BorderFactory.createEmptyBorder(50,0,50,0));
            listeCartes.add(vide);

        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE);

            for (Commande c : commandes) {
                // ── Carte blanche ─────────────────────────────────────────
                JPanel carte = new JPanel(new BorderLayout(10,10));
                carte.setBackground(Color.WHITE);
                carte.setOpaque(true);
                carte.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200,200,200)),
                        BorderFactory.createEmptyBorder(15,15,15,15)
                ));
                carte.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // hauteur fixe

                // — Infos à gauche —
                JPanel info = new JPanel();
                info.setOpaque(false);
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

                JLabel lblStatut = new JLabel("LIVRÉ");
                lblStatut.setFont(new Font("SansSerif", Font.BOLD, 14));
                lblStatut.setForeground(new Color(34,139,34));
                info.add(lblStatut);

                info.add(Box.createVerticalStrut(3));
                JLabel lblDate = new JLabel(sdf.format(c.getDate()));
                lblDate.setFont(new Font("SansSerif", Font.PLAIN, 12));
                info.add(lblDate);

                info.add(Box.createVerticalStrut(8));
                JLabel lblTotal = new JLabel(String.format("Total : %.2f €", c.getMontant()));
                lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
                info.add(lblTotal);

                info.add(Box.createVerticalGlue());
                JLabel voir = new JLabel("<html><u>Voir la commande</u></html>");
                voir.setFont(new Font("SansSerif", Font.PLAIN, 12));
                voir.setForeground(new Color(92,92,192));
                voir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                voir.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // TODO : ouvrir page détail de c
                        JOptionPane.showMessageDialog(
                                HistoriquePanel.this,
                                "Détails de la commande #" + c.getId(),
                                "Commande",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                });
                info.add(voir);

                carte.add(info, BorderLayout.WEST);

                // — Carrousel d’images à droite —
                JPanel carousel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                carousel.setOpaque(false);
                for (Panier.Item item : c.getItems()) {
                    Image img = redimensionnerImage(item.getProduit().getImagePath(), 80, 80);
                    if (img != null) {
                        JLabel pic = new JLabel(new ImageIcon(img));
                        pic.setToolTipText(item.getProduit().getNom() + " ×" + item.getQuantite());
                        carousel.add(pic);
                    }
                }
                JScrollPane scrollImg = new JScrollPane(
                        carousel,
                        JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                );
                scrollImg.setBorder(null);
                scrollImg.setPreferredSize(new Dimension(300, 90));
                carte.add(scrollImg, BorderLayout.CENTER);

                listeCartes.add(carte);
                listeCartes.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane scroll = new JScrollPane(listeCartes);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ─── Bouton Retour ────────────────────────────────────────────────
        JButton retour = createStyledButton("⬅ Retour");
        retour.addActionListener(e -> {
            MainFrame mf = (MainFrame)SwingUtilities.getWindowAncestor(this);
            mf.showPanel("acheteur");
        });
        JPanel bas = new JPanel();
        bas.setOpaque(false);
        bas.setBorder(BorderFactory.createEmptyBorder(10,0,20,0));
        bas.add(retour);
        add(bas, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // même gradient que les autres panels
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g.create();
        Color c1 = new Color(253,243,247);
        Color c2 = new Color(252,228,236);
        g2.setPaint(new GradientPaint(0,0,c1,0,getHeight(),c2));
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.dispose();
    }

    private Image redimensionnerImage(String chemin, int w, int h) {
        if (chemin == null || chemin.isBlank()) return null;
        try {
            BufferedImage orig = ImageIO.read(new File(chemin));
            BufferedImage resized = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(orig,0,0,w,h,null);
            g2.dispose();
            return resized;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private JButton createStyledButton(String texte) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(new Color(248,187,208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(244,143,177));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(248,187,208));
            }
        });
        return btn;
    }
}
