package Vue.advanced;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel container;

    public MainFrame() {
        setTitle("🛒 Shopping App");
        setSize(1000, 700); // ✅ Taille élargie
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();

        container = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                Color color1 = new Color(253, 243, 247); // #fdf3f7
                Color color2 = new Color(252, 228, 236); // #fce4ec
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        container.setOpaque(false);
        setContentPane(container);
    }

    public void addPanel(JPanel panel, String name) {
        container.add(panel, name);
    }

    public void showPanel(String name) {
        cardLayout.show(container, name);
    }
}
