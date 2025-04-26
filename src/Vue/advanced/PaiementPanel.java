package Vue.advanced;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaiementPanel extends JPanel {
    // Champs de saisie pour les infos de carte et note
    private JTextField cardNumberField;
    private JTextField cardNameField;
    private JTextField expiryDateField;
    private JTextField cvvField;
    private JTextField noteField;

    // Boutons d'action
    private JButton confirmPaymentBtn;
    private JButton cancelBtn;

    // Étiquette pour afficher le montant total
    private JLabel totalAmountLabel;

    // Niveau d'opacité utilisé pour un effet de transition
    private float opacity = 0.0f;

    // Constructeur avec le montant total à payer en paramètre
    public PaiementPanel(double totalAmount) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        // Animation de fade-in
        Timer timer = new Timer(15, e -> {
            if (opacity < 1.0f) {
                opacity += 0.02f;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();

        // Carte blanche centrale avec coins arrondis
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, Math.min(255, (int)(opacity * 255))));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        card.setPreferredSize(new Dimension(500, 520));
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        // Titre principal
        JLabel title = new JLabel("💳 Paiement");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(new Color(92, 92, 92));
        card.add(title, gbc);

        // Affichage de la date du paiement
        gbc.gridy++;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        JLabel dateLabel = new JLabel("Date: " + sdf.format(new Date()));
        dateLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(dateLabel, gbc);

        // Montant total à payer
        gbc.gridy++;
        totalAmountLabel = new JLabel("Montant total: " + String.format("%.2f €", totalAmount));
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalAmountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalAmountLabel.setForeground(new Color(248, 188, 208));
        card.add(totalAmountLabel, gbc);

        // Formulaire de carte
        gbc.gridwidth = 1;

        gbc.gridy++;
        card.add(new JLabel("Numéro de carte:"), gbc);
        gbc.gridx = 1;
        cardNumberField = new JTextField(20);
        card.add(cardNumberField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        card.add(new JLabel("Nom sur la carte:"), gbc);
        gbc.gridx = 1;
        cardNameField = new JTextField(20);
        card.add(cardNameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        card.add(new JLabel("Date d'expiration (MM/YY):"), gbc);
        gbc.gridx = 1;
        expiryDateField = new JTextField(5);
        card.add(expiryDateField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        card.add(new JLabel("CVV:"), gbc);
        gbc.gridx = 1;
        cvvField = new JTextField(3);
        card.add(cvvField, gbc);

        // Champ note (pour évaluer la commande)
        gbc.gridx = 0; gbc.gridy++;
        card.add(new JLabel("Note sur 10 :"), gbc);
        gbc.gridx = 1;
        noteField = new JTextField(2);
        card.add(noteField, gbc);

        // Boutons d'action : annuler ou confirmer
        gbc.gridy++;
        gbc.gridx = 0;
        cancelBtn = createStyledButton("Annuler");
        cancelBtn.setBackground(new Color(239, 154, 154));
        card.add(cancelBtn, gbc);

        gbc.gridx = 1;
        confirmPaymentBtn = createStyledButton("Confirmer le paiement");
        confirmPaymentBtn.setBackground(new Color(129, 199, 132));
        card.add(confirmPaymentBtn, gbc);

        // On ajoute la carte au panel principal
        add(card);
    }

    // Fond gradient rose pâle
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        Color color1 = new Color(253, 243, 247);
        Color color2 = new Color(252, 228, 236);
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    // Création de boutons stylisés
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Animation au survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().brighter());
            }
        });

        return button;
    }

    // Définition de l'action du bouton de confirmation
    public void setConfirmPaymentAction(ActionListener l) {
        confirmPaymentBtn.addActionListener(l);
    }

    // Définition de l'action du bouton d'annulation
    public void setCancelAction(ActionListener l) {
        cancelBtn.addActionListener(l);
    }

    // Récupère la note entrée par l'utilisateur, ou -1 si invalide
    public int getNote() {
        try {
            return Integer.parseInt(noteField.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}