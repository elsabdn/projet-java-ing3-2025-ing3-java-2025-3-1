package Vue.advanced;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AccueilPanel extends JPanel {
    private JButton loginBtn, buyerBtn, sellerBtn;

    public AccueilPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Bienvenue dans l'application Shopping");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        loginBtn = new JButton("🔐 Se connecter");
        buyerBtn = new JButton("🛍️ Créer un compte acheteur");
        sellerBtn = new JButton("🏪 Créer un compte vendeur");

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy++;
        add(loginBtn, gbc);
        gbc.gridy++;
        add(buyerBtn, gbc);
        gbc.gridy++;
        add(sellerBtn, gbc);
    }

    public void setLoginAction(ActionListener l) {
        loginBtn.addActionListener(l);
    }

    public void setBuyerAction(ActionListener l) {
        buyerBtn.addActionListener(l);
    }

    public void setSellerAction(ActionListener l) {
        sellerBtn.addActionListener(l);
    }
}