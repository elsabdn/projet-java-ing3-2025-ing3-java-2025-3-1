package Vue.advanced;

import Controller.AuthController;
import Modele.Acheteur;
import Modele.Vendeur;

import javax.swing.*;
import java.awt.*;

/**
 * Panel unique pour saisir email + mot de passe
 * et créer un compte Acheteur ou Vendeur.
 */
public class InscriptionPanel extends JPanel {
    // Enumération pour spécifier le type de compte à créer
    public enum TypeUtilisateur { ACHETEUR, VENDEUR }

    // Références aux composants et contrôleurs
    private final MainFrame mainFrame;
    private final AuthController authController;
    private final TypeUtilisateur type;
    private final JTextField emailField;
    private final JPasswordField mdpField;
    private final JButton btnValider;
    private final JButton btnRetour;

    /**
     * Constructeur : gère l'affichage et le comportement de l'interface
     */
    public InscriptionPanel(MainFrame mainFrame,
                            AuthController authController,
                            TypeUtilisateur type) {
        this.mainFrame      = mainFrame;
        this.authController = authController;
        this.type           = type;

        setLayout(new GridBagLayout());
        setOpaque(false);

        // ─── Création d'une carte blanche avec arrondis ─────────────────────────────
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // fond blanc arrondi
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(0, 20));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 320));

        // ─── Titre selon le type de compte ────────────────────────────────────────────────
        String titreTxt = type == TypeUtilisateur.ACHETEUR
                ? "Créer un compte Acheteur"
                : "Créer un compte Vendeur";
        JLabel titre = new JLabel(titreTxt, SwingConstants.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 18));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        card.add(titre, BorderLayout.NORTH);

        // ─── Formulaire email + mot de passe ───────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0; gbc.gridy = 0;

        // email
        form.add(new JLabel("Email :"), gbc);
        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(250, 30));
        emailField.setBackground(Color.WHITE);
        gbc.gridx = 1;
        form.add(emailField, gbc);

        // mot de passe
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Mot de passe :"), gbc);
        mdpField = new JPasswordField();
        mdpField.setPreferredSize(new Dimension(250, 30));
        mdpField.setBackground(Color.WHITE);
        gbc.gridx = 1;
        form.add(mdpField, gbc);

        card.add(form, BorderLayout.CENTER);

        // ─── Boutons valider et retour ─────────────────────────────────────────────
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actions.setOpaque(false);

        btnValider = createStyledButton("Valider");
        btnRetour  = createStyledButton("⬅ Retour");

        actions.add(btnValider);
        actions.add(btnRetour);
        card.add(actions, BorderLayout.SOUTH);

        // on ajoute la carte au centre
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 0;
        add(card, c2);

        // ─── Listeners ───────────────────────────────────────────
        btnValider.addActionListener(e -> onValider());
        btnRetour .addActionListener(e -> mainFrame.showPanel("accueil"));
    }

    /**
     * Action à exécuter lors du clic sur "Valider" :
     * enregistre un nouvel utilisateur en fonction du type
     */
    private void onValider() {
        String email = emailField.getText().trim();
        String mdp   = new String(mdpField.getPassword()).trim();
        if (email.isEmpty() || mdp.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez remplir tous les champs.",
                    "Champs manquants",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (type == TypeUtilisateur.ACHETEUR) {
            Acheteur a = authController.registerAcheteur(email, mdp);
            mainFrame.setAcheteurConnecte(a);
            mainFrame.showAcheteurHome();
        } else {
            Vendeur v = authController.registerVendeur(email, mdp);
            mainFrame.showVendeurHome(v);
        }
    }

    /**
     * Crée un bouton stylé rose pastel avec effet hover.
     */
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(new Color(248,187,208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8,20,8,20));
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
