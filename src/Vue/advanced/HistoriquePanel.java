package Vue.advanced;

import Modele.Acheteur;
import Modele.Commande;
import Modele.Panier;
import DAO.CommandeDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HistoriquePanel extends JPanel {
    private final Acheteur acheteur;
    private final CommandeDAO commandeDAO;

    public HistoriquePanel(Acheteur acheteur) {
        this.acheteur = acheteur;
        this.commandeDAO = new CommandeDAO();

        setLayout(new BorderLayout());

        JLabel titre = new JLabel("📜 Historique de vos commandes", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 18));
        add(titre, BorderLayout.NORTH);

        List<Commande> commandes = commandeDAO.getCommandesByUtilisateurId(acheteur.getId());
        DefaultListModel<String> model = new DefaultListModel<>();

        if (commandes.isEmpty()) {
            model.addElement("🛑 Vous n’avez rien commandé encore… Qu’attendez-vous ?");
        } else {
            for (Commande c : commandes) {
                StringBuilder sb = new StringBuilder();
                sb.append("🧾 Commande #").append(c.getId())
                        .append(" - Total : ").append(String.format("%.2f", c.getMontant())).append("€");

                // Affichage de la note si elle existe
                if (c.getNote() > 0) {
                    sb.append(" - Note : ").append(c.getNote()).append("/10");
                }

                sb.append("\n");

                for (Panier.Item item : c.getItems()) {
                    sb.append("   • ").append(item.getProduit().getNom())
                            .append(" x").append(item.getQuantite())
                            .append(" @ ").append(String.format("%.2f", item.getProduit().getPrix())).append("€\n");
                }

                sb.append("\n");
                model.addElement(sb.toString());
            }
        }

        JList<String> listeCommandes = new JList<>(model);
        listeCommandes.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(listeCommandes);
        add(scrollPane, BorderLayout.CENTER);

        JButton retour = new JButton("⬅ Retour");
        retour.setFont(new Font("SansSerif", Font.PLAIN, 14));
        retour.addActionListener(e -> {
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            mainFrame.showPanel("acheteur");
        });
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(255, 255, 255, 0));
        btnPanel.add(retour);
        add(btnPanel, BorderLayout.SOUTH);
    }
}