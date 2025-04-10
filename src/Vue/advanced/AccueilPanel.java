package Vue.advanced;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AccueilPanel extends JPanel {
    private JButton loginBtn, acheteurBtn, vendeurBtn;

    public AccueilPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Bienvenue dans l'application Shopping");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        loginBtn = new JButton("🔐 Se connecter");
        acheteurBtn = new JButton("🛍️ Créer un compte acheteur");
        vendeurBtn = new JButton("🏪 Créer un compte vendeur");

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy++;
        add(loginBtn, gbc);
        gbc.gridy++;
        add(acheteurBtn, gbc);
        gbc.gridy++;
        add(vendeurBtn, gbc);
    }

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