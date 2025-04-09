package Vue.advanced;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton backBtn;

    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("🔐 Connexion");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel emailLabel = new JLabel("Email :");
        JLabel passwordLabel = new JLabel("Mot de passe :");

        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

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
        add(passwordLabel, gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(backBtn, gbc);
        gbc.gridx = 1;
        add(loginBtn, gbc);
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setLoginAction(ActionListener l) {
        loginBtn.addActionListener(l);
    }

    public void setBackAction(ActionListener l) {
        backBtn.addActionListener(l);
    }
}
