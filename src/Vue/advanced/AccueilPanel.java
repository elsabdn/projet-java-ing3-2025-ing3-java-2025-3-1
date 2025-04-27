package Vue.advanced;

import Modele.Acheteur;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AccueilPanel extends JPanel {
    private final MainFrame mainFrame;
    private JButton loginBtn, acheteurBtn, vendeurBtn, historiqueBtn;
    private float opacity = 0.0f;

    /** Pour la page d’accueil non connecté */
    public AccueilPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initUI(null);
    }

    /** Pour la page d’accueil lorsqu’un acheteur est déjà connecté */
    public AccueilPanel(MainFrame mainFrame, Acheteur acheteurConnecte) {
        this.mainFrame = mainFrame;
        initUI(acheteurConnecte);
    }

    private void initUI(Acheteur acheteurConnecte) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        // fade‑in
        Timer timer = new Timer(15, e -> {
            if (opacity < 1.0f) {
                opacity += 0.02f;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();

        // carte blanche
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255, Math.min(255,(int)(opacity*255))));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);
            }
        };
        card.setPreferredSize(new Dimension(500, acheteurConnecte==null?480:540));
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15,0,15,0);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0;
        gbc.gridy  = 0;

        // logo
        JLabel logo = new JLabel("🛍 ShoppingApp");
        logo.setFont(new Font("Serif", Font.BOLD, 32));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setForeground(new Color(92,92,92));
        card.add(logo, gbc);

        // sous‑titre
        gbc.gridy++;
        JLabel subtitle = new JLabel("Votre boutique en ligne préférée !");
        subtitle.setFont(new Font("Serif", Font.PLAIN, 16));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setForeground(new Color(120,120,120));
        card.add(subtitle, gbc);

        // boutons selon statut
        gbc.gridy++;
        if (acheteurConnecte == null) {
            loginBtn = createStyledButton("🔐 Se connecter");
            card.add(loginBtn, gbc);

            gbc.gridy++;
            acheteurBtn = createStyledButton("🛍 Créer un compte acheteur");
            card.add(acheteurBtn, gbc);

            gbc.gridy++;
            vendeurBtn = createStyledButton("🏪 Créer un compte vendeur");
            card.add(vendeurBtn, gbc);

        } else {
            historiqueBtn = createStyledButton("🧾 Mes commandes");
            // ici on utilise mainFrame pour ouvrir HistoriquePanel
            historiqueBtn.addActionListener(e -> {
                HistoriquePanel hp = new HistoriquePanel(mainFrame, acheteurConnecte);
                mainFrame.ajouterPanel(hp, "historique");
                mainFrame.afficherPanel("historique");
            });
            card.add(historiqueBtn, gbc);
        }

        add(card);

        // Dans le constructeur ou initUI() d'AccueilPanel(MainFrame mainFrame, Acheteur?):
        acheteurBtn.addActionListener(e -> {
            InscriptionPanel p = new InscriptionPanel(
                    mainFrame,
                    mainFrame.obtenirAuthController(),
                    InscriptionPanel.TypeUtilisateur.ACHETEUR
            );
            mainFrame.ajouterPanel(p, "inscriptionAcheteur");
            mainFrame.afficherPanel("inscriptionAcheteur");
        });

        vendeurBtn.addActionListener(e -> {
            InscriptionPanel p = new InscriptionPanel(
                    mainFrame,
                    mainFrame.obtenirAuthController(),
                    InscriptionPanel.TypeUtilisateur.VENDEUR
            );
            mainFrame.ajouterPanel(p, "inscriptionVendeur");
            mainFrame.afficherPanel("inscriptionVendeur");
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        Color c1 = new Color(253,243,247), c2 = new Color(252,228,236);
        g2d.setPaint(new GradientPaint(0,0,c1,0,getHeight(),c2));
        g2d.fillRect(0,0,getWidth(),getHeight());
        g2d.dispose();
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(new Color(248,187,208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(300,45));
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

    // Seulment pour la page non‑connectée
    public void definirActionConnexio(ActionListener l)    { if (loginBtn    != null) loginBtn.addActionListener(l); }
    public void definirActionAcheteur(ActionListener l){ if (acheteurBtn != null) acheteurBtn.addActionListener(l); }
    public void definirActionVendeur(ActionListener l) { if (vendeurBtn  != null) vendeurBtn.addActionListener(l); }
}
