package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Accueil extends JFrame {
    private JButton loginButton;
    private JButton registerAcheteurButton;
    private JButton registerVendeurButton;

    public Accueil() {
        setTitle("Accueil");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loginButton = new JButton("Se connecter");
        registerAcheteurButton = new JButton("Créer compte acheteur");
        registerVendeurButton = new JButton("Créer compte vendeur");

        setLayout(new GridLayout(3, 1));
        add(loginButton);
        add(registerAcheteurButton);
        add(registerVendeurButton);
    }

    public void setLoginAction(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void setRegisterAcheteurAction(ActionListener listener) {
        registerAcheteurButton.addActionListener(listener);
    }

    public void setRegisterVendeurAction(ActionListener listener) {
        registerVendeurButton.addActionListener(listener);
    }
}
