package Vue.advanced;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AccueilPanel extends JPanel {
    private JButton loginBtn, acheteurBtn, vendeurBtn;
    private float opacity = 0.0f;

    public AccueilPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        Timer timer = new Timer(15, e -> {
            if (opacity < 1.0f) {
                opacity += 0.02f;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();

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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel logo = new JLabel("🛍️ ShoppingApp");
        logo.setFont(new Font("Serif", Font.BOLD, 32));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setForeground(new Color(92, 92, 92));
        card.add(logo, gbc);

        gbc.gridy++;
        JLabel subtitle = new JLabel("Votre boutique en ligne préférée !");
        subtitle.setFont(new Font("Serif", Font.PLAIN, 16));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setForeground(new Color(120, 120, 120));
        card.add(subtitle, gbc);

        gbc.gridy++;
        loginBtn = createStyledButton("🔐 Se connecter");
        card.add(loginBtn, gbc);

        gbc.gridy++;
        acheteurBtn = createStyledButton("🛍️ Créer un compte acheteur");
        card.add(acheteurBtn, gbc);

        gbc.gridy++;
        vendeurBtn = createStyledButton("🏪 Créer un compte vendeur");
        card.add(vendeurBtn, gbc);

        add(card);
    }

    // 🌸 Fond 100% rose pâle
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

    // 🌸 Boutons pastel doux
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

    // Actions
    public void setLoginAction(ActionListener l) {
        loginBtn.addActionListener(l);
    }

    public void setAcheteurAction(ActionListener l) {
        acheteurBtn.addActionListener(l);
    }

    public void setVendeurAction(ActionListener l) {
        vendeurBtn.addActionListener(l);
    }
}
