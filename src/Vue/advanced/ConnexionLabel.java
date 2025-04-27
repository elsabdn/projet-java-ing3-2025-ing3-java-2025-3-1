package Vue.advanced;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * ConnexionLabel : panneau contenant un formulaire de connexion avec un design pastel et responsive.
 */
public class ConnexionLabel extends JPanel {
    private JTextField emailField;
    private JPasswordField mdpField;
    private JButton loginBtn;
    private JButton backBtn;
    private float opacity = 0.0f; // Gère la transparence pour l'effet d'apparition

    public ConnexionLabel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        // Animation d'apparition douce (fade-in)
        Timer timer = new Timer(15, e -> {
            if (opacity < 1.0f) {
                opacity += 0.02f;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();

        // Carte centrale avec fond blanc semi-transparent
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, Math.min(255, (int)(opacity * 255))));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        card.setPreferredSize(new Dimension(500, 480));
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Grille de placement des composants
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        // Titre de la fenêtre
        JLabel title = new JLabel("Connexion");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(new Color(92, 92, 92));
        card.add(title, gbc);

        // Champs d'identifiants
        JLabel emailLabel = new JLabel("Email :");
        JLabel mdpLabel = new JLabel("Mot de passe :");

        emailField = new JTextField(20);
        mdpField = new JPasswordField(20);

        loginBtn = createStyledButton("Se connecter");
        backBtn = createStyledButton("← Retour");

        // Placement des composants dans la grille
        gbc.gridwidth = 1;
        gbc.gridy++;
        card.add(emailLabel, gbc);
        gbc.gridx = 1;
        card.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        card.add(mdpLabel, gbc);
        gbc.gridx = 1;
        card.add(mdpField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        card.add(backBtn, gbc);
        gbc.gridx = 1;
        card.add(loginBtn, gbc);

        add(card);
    }

    /**
     * Crée un bouton stylé en rose pastel avec effet hover.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(248, 187, 208)); // #f8bbd0
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 45));
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

    /**
     * Arrière-plan en dégradé rose doux.
     */
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

    // === Getters et setters ===
    public String obtenirEmail() {
        return emailField.getText();
    }

    public String obtenirMdp() {
        return new String(mdpField.getPassword());
    }

    public void definirActionConnexion(ActionListener l) {
        loginBtn.addActionListener(l);
    }

    public void definirActionRetour(ActionListener l) {
        backBtn.addActionListener(l);
    }
}
