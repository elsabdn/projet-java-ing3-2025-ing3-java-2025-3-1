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

        JLabel titre = new JLabel("Historique de vos commandes", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 18));
        add(titre, BorderLayout.NORTH);

        List<Commande> commandes = commandeDAO.getCommandesByUtilisateurId(acheteur.getId());
        DefaultListModel<String> model = new DefaultListModel<>();

        for (Commande c : commandes) {
            StringBuilder sb = new StringBuilder();
            sb.append("Commande #").append(c.getId()).append(" - Total : ").append(c.getMontant()).append("€\n");
            for (Panier.Item item : c.getItems()) {
                sb.append("• ").append(item.getProduit().getNom())
                        .append(" x").append(item.getQuantite())
                        .append(" @ ").append(item.getProduit().getPrix()).append("€\n");
            }
            sb.append("\n");
            model.addElement(sb.toString());
        }

        JList<String> listeCommandes = new JList<>(model);
        listeCommandes.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(listeCommandes);
        add(scrollPane, BorderLayout.CENTER);

        JButton retour = new JButton("Retour");
        retour.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.setContentPane(new AccueilPanel(acheteur));
            topFrame.revalidate();
        });
        add(retour, BorderLayout.SOUTH);
    }
}
