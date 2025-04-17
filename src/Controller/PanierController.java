package Controller;

import Modele.Acheteur;
import Modele.Panier;
import Modele.Produit;
import DAO.CommandeDAO;
import DAO.PanierDAO;

import javax.swing.*;
import java.awt.*;

public class PanierController {
    private final Acheteur acheteur;
    private final PanierDAO panierDAO = new PanierDAO();
    private final CommandeDAO commandeDAO = new CommandeDAO();

    public PanierController(Acheteur acheteur) {
        this.acheteur = acheteur;
        panierDAO.chargerPanier(acheteur);
    }

    public boolean addToPanier(Produit produit, int quantite) {
        if (produit.getQuantite() >= quantite) {
            acheteur.getPanier().addItem(produit, quantite);
            produit.setQuantite(produit.getQuantite() - quantite);
            panierDAO.ajouterItem(acheteur.getId(), produit, quantite);
            return true;
        }
        return false;
    }

    public void checkout() {
        Panier panier = acheteur.getPanier();
        if (panier.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Votre panier est vide !");
            return;
        }

        double total = panier.getPrixTot();
        int confirm = JOptionPane.showConfirmDialog(null,
                "Total : " + total + " €\nConfirmer l'achat ?", "Paiement",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int commandeId = commandeDAO.creerCommande(acheteur.getId(), total);
            commandeDAO.ajouterItemsCommande(commandeId, panier.getItems());
            panierDAO.viderPanier(acheteur.getId());
            panier.clear();

            JTextArea textArea = new JTextArea(generateReceipt());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "Ticket de Commande", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("🧾 Ticket de Commande\n");
        sb.append("Acheteur : ").append(acheteur.getEmail()).append("\n\n");

        double total = 0.0;
        for (Panier.Item item : acheteur.getPanier().getItems()) {
            double sub = item.getProduit().getPrix() * item.getQuantite();
            total += sub;
            sb.append("- ").append(item.getProduit().getNom())
                    .append(" x").append(item.getQuantite())
                    .append(" @ ").append(item.getProduit().getPrix()).append(" €")
                    .append(" = ").append(String.format("%.2f", sub)).append(" €\n");
        }

        sb.append("\nTotal : ").append(String.format("%.2f", total)).append(" €");
        return sb.toString();
    }
}
