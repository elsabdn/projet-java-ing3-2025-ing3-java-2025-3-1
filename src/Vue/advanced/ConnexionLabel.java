package Vue.advanced;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ConnexionLabel extends JPanel {
    private JTextField emailField;
    private JPasswordField mdpField;
    private JButton loginBtn;
    private JButton backBtn;

    public ConnexionLabel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("🔐 Connexion");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel emailLabel = new JLabel("Email :");
        JLabel mdpLabel = new JLabel("Mot de passe :");

        emailField = new JTextField(20);
        mdpField = new JPasswordField(20);

        loginBtn = new JButton("Se connecter");
        backBtn = new JButton("← Retour");

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        add(emailLabel, gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(mdpLabel, gbc);
        gbc.gridx = 1;
        add(mdpField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(backBtn, gbc);
        gbc.gridx = 1;
        add(loginBtn, gbc);
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getMdp() {
        return new String(mdpField.getPassword());
    }

    public void setLoginAction(ActionListener l) {
        loginBtn.addActionListener(l);
    }

    public void setBackAction(ActionListener l) {
        backBtn.addActionListener(l);
    }
}
