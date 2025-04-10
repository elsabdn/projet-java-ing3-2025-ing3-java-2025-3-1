package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ConnexionVue extends JFrame {
    private JTextField emailField;
    private JPasswordField mdpField;
    private JButton loginButton;

    public ConnexionVue() {
        setTitle("Connexion");
        setSize(300, 150);
        setLocationRelativeTo(null);

        emailField = new JTextField(15);
        mdpField = new JPasswordField(15);
        loginButton = new JButton("Se connecter");

        setLayout(new GridLayout(3, 2));
        add(new JLabel("Email :"));
        add(emailField);
        add(new JLabel("Mot de passe :"));
        add(mdpField);
        add(new JLabel(""));
        add(loginButton);
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getMdp() {
        return new String(mdpField.getPassword());  // Utilise getPassword() et convertis en String
    }


    public void setLoginAction(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
}
