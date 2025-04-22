package Vue.advanced;

import Modele.Commande;
import Modele.Panier;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CommandeDetailPanel extends JPanel {
    private final MainFrame mainFrame;
    private final Commande commande;

    public CommandeDetailPanel(MainFrame mainFrame, Commande commande) {
        this.mainFrame = mainFrame;
        this.commande  = commande;

        setLayout(new BorderLayout());
        setOpaque(false);

        // ─── HEADER ───────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // filler gauche
        JPanel leftFiller = new JPanel();
        leftFiller.setOpaque(false);
        leftFiller.setPreferredSize(new Dimension(140, 35));
        header.add(leftFiller, BorderLayout.WEST);
        // titre centré
        JLabel title = new JLabel("🧾 Détail de la commande #" + commande.getId(), SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(92, 92, 92));
        header.add(title, BorderLayout.CENTER);
        // bouton déconnexion
        JButton btnLogout = createStyledButton("🚪 Déconnexion");
        btnLogout.setPreferredSize(new Dimension(140, 35));
        btnLogout.addActionListener(e -> mainFrame.showPanel("historique"));
        header.add(btnLogout, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ─── CENTRE : wrapper blanc ─────────────────────────────────
        JPanel centreWrapper = new JPanel(new BorderLayout());
        centreWrapper.setBackground(Color.WHITE);
        centreWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // résumé en haut du wrapper
        JPanel summary = new JPanel();
        summary.setOpaque(false);
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE);

        JLabel lblDate = new JLabel("Date : " + sdf.format(commande.getDate()));
        lblDate.setFont(new Font("SansSerif", Font.PLAIN, 14));
        summary.add(lblDate);

        summary.add(Box.createVerticalStrut(5));
        JLabel lblTotal = new JLabel(String.format("Montant total : %.2f €", commande.getMontant()));
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        summary.add(lblTotal);

        if (commande.getNote() > 0) {
            summary.add(Box.createVerticalStrut(5));
            JLabel lblNote = new JLabel("Note : " + commande.getNote() + " / 10");
            lblNote.setFont(new Font("SansSerif", Font.PLAIN, 14));
            summary.add(lblNote);
        }

        centreWrapper.add(summary, BorderLayout.NORTH);

        // liste des items en scroll
        JPanel itemsPanel = new JPanel();
        itemsPanel.setOpaque(false);
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        for (Panier.Item item : commande.getItems()) {
            itemsPanel.add(creerCarteItem(item));
            itemsPanel.add(Box.createVerticalStrut(10));
        }

        if (commande.getItems().isEmpty()) {
            JLabel vide = new JLabel("🛒 Aucun article dans cette commande.", SwingConstants.CENTER);
            vide.setFont(new Font("SansSerif", Font.ITALIC, 14));
            vide.setForeground(new Color(120,120,120));
            vide.setBorder(new EmptyBorder(50,0,50,0));
            itemsPanel.add(vide);
        }

        JScrollPane scrollItems = new JScrollPane(
                itemsPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollItems.setBorder(null);
        // on force le viewport blanc pour ne plus voir de gris
        scrollItems.getViewport().setBackground(Color.WHITE);
        scrollItems.setBackground(Color.WHITE);
        scrollItems.getVerticalScrollBar().setUnitIncrement(16);

        centreWrapper.add(scrollItems, BorderLayout.CENTER);

        add(centreWrapper, BorderLayout.CENTER);

        // ─── FOOTER ────────────────────────────────────────────────
        JButton back = createStyledButton("⬅ Retour");
        back.addActionListener(e -> mainFrame.showPanel("historique"));
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10,0,20,0));
        footer.add(back);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel creerCarteItem(Panier.Item item) {
        JPanel card = new JPanel(new BorderLayout(10,10));
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10,10,10,10),
                BorderFactory.createLineBorder(new Color(220,220,220))
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // image
        Image img = redimensionnerImage(item.getProduit().getImagePath(), 80, 80);
        if (img != null) {
            JLabel pic = new JLabel(new ImageIcon(img));
            pic.setBorder(new EmptyBorder(0,0,0,10));
            card.add(pic, BorderLayout.WEST);
        }

        // infos texte
        JPanel infos = new JPanel();
        infos.setOpaque(false);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));

        JLabel lblNom = new JLabel(item.getProduit().getNom());
        lblNom.setFont(new Font("SansSerif", Font.BOLD, 14));
        infos.add(lblNom);

        infos.add(Box.createVerticalStrut(5));
        JLabel lblPrix = new JLabel(
                String.format("Prix unitaire : %.2f €", item.getProduit().getPrix())
        );
        lblPrix.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infos.add(lblPrix);

        infos.add(Box.createVerticalStrut(5));
        JLabel lblQte = new JLabel("Quantité : " + item.getQuantite());
        lblQte.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infos.add(lblQte);

        card.add(infos, BorderLayout.CENTER);
        return card;
    }

    private Image redimensionnerImage(String chemin, int w, int h) {
        if (chemin == null || chemin.isBlank()) return null;
        try {
            BufferedImage orig = ImageIO.read(new File(chemin));
            BufferedImage dst  = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = dst.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(orig, 0, 0, w, h, null);
            g2.dispose();
            return dst;
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
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(244,143,177)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(248,187,208)); }
        });
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // gradient rose pâle
        Graphics2D g2 = (Graphics2D) g.create();
        Color c1 = new Color(253,243,247), c2 = new Color(252,228,236);
        g2.setPaint(new GradientPaint(0,0,c1,0,getHeight(),c2));
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.dispose();
    }
}
