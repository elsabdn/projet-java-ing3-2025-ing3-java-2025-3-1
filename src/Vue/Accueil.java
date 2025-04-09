package Vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Accueil extends JFrame {
    private JButton loginButton;
    private JButton registerBuyerButton;
    private JButton registerSellerButton;

    public Accueil() {
        setTitle("Accueil");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loginButton = new JButton("Se connecter");
        registerBuyerButton = new JButton("Créer compte acheteur");
        registerSellerButton = new JButton("Créer compte vendeur");

        setLayout(new GridLayout(3, 1));
        add(loginButton);
        add(registerBuyerButton);
        add(registerSellerButton);
    }

    public void setLoginAction(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void setRegisterBuyerAction(ActionListener listener) {
        registerBuyerButton.addActionListener(listener);
    }

    public void setRegisterSellerAction(ActionListener listener) {
        registerSellerButton.addActionListener(listener);
    }
}
