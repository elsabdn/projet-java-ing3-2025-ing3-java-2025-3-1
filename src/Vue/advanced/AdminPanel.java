package Vue.advanced;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {

    private JTabbedPane tabbedPane;

    public AdminPanel() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // Onglet tableau de bord
        tabbedPane.addTab("Tableau de bord", createDashboardPanel());

        // Onglet gestion des utilisateurs
        tabbedPane.addTab("Utilisateurs", createUserManagementPanel());

        // Onglet gestion des promotions
        tabbedPane.addTab("Promotions", createPromotionsPanel());

        // Onglet historique clients
        tabbedPane.addTab("Historique clients", createHistoryPanel());

        // Onglet modération
        tabbedPane.addTab("Modération", createModerationPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("[Graphiques de ventes et CA ici avec JFreeChart]"));
        return panel;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("[CRUD utilisateurs avec JTable et formulaire]"));
        return panel;
    }

    private JPanel createPromotionsPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("[Configuration des offres et remises]"));
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("[Historique global des clients]"));
        return panel;
    }

    private JPanel createModerationPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("[Modération des commentaires/notes]"));
        return panel;
    }
}

