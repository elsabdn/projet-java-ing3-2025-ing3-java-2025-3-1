package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginView() {
        setTitle("Connexion");
        setSize(300, 150);
        setLocationRelativeTo(null);

        emailField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Se connecter");

        setLayout(new GridLayout(3, 2));
        add(new JLabel("Email :"));
        add(emailField);
        add(new JLabel("Mot de passe :"));
        add(passwordField);
        add(new JLabel(""));
        add(loginButton);
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void setLoginAction(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
}
