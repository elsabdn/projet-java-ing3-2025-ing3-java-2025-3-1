package Controller;

import Modele.Acheteur;
import Modele.Panier;
import Modele.Produit;

import java.awt.Font;
import javax.swing.*;

public class PanierController {
    private Panier panier;

    public PanierController(Acheteur acheteur) {
        this.panier = acheteur.getPanier();
    }

    /*
    public addToPanier(Acheteur acheteur, Product product, int quantite) {
        if (product.getQuantite() < quantite) {
            return false; // pas assez de stock
        }

        Panier panier = buyer.getPanier();
        panier.addItem(product, quantite);
        product.setQuantite(product.getQuantite() - quantite); // mise à jour du stock

        return true;
    }
     */
    public boolean addToPanier(Produit produit, int quantite) {
        if (produit.getQuantite() >= quantite) {
            panier.addItem(produit, quantite);
            produit.setQuantite(produit.getQuantite() - quantite); // déduction stock
            return true;
        } else {
            System.out.println("Stock insuffisant pour " + produit.getName());
            return false;
        }
    }


    public void removeFromPanier(Acheteur acheteur, Produit produit) {
        Panier panier = acheteur.getPanier();
        panier.removeItem(produit);
    }

    public double calculateTotal(Acheteur acheteur) {

        return acheteur.getPanier().getTotalPrice();
    }

    public void checkout(Acheteur acheteur) {
        Panier panier = acheteur.getPanier();

        if (panier.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Votre panier est vide !");
            return;
        }

        double total = panier.getItems().stream()
                .mapToDouble(item -> item.getProduit().getPrice() * item.getQuantite())
                .sum();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Total : " + total + " €\nConfirmer l'achat ?", "Paiement",
                JOptionPane.YES_NO_OPTION);

        /*
        if (confirm == JOptionPane.YES_OPTION) {
            panier.clear();
            JOptionPane.showMessageDialog(null, "Paiement effectué avec succès ! ✨");
        }
         */

        if (confirm == JOptionPane.YES_OPTION) {
            String receipt = generateReceipt(acheteur);
            panier.clear();

            JTextArea textArea = new JTextArea(receipt);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "Ticket de Commande", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private String generateReceipt(Acheteur acheteur) {
        StringBuilder sb = new StringBuilder();
        sb.append("🧾 Ticket de Commande\n");
        sb.append("Acheteur : ").append(acheteur.getEmail()).append("\n\n");

        double total = 0.0;
        for (Panier.Item item : acheteur.getPanier().getItems()) {
            double sub = item.getProduit().getPrice() * item.getQuantite();
            total += sub;
            sb.append("- ")
                    .append(item.getProduit().getName())
                    .append(" x").append(item.getQuantite())
                    .append(" @ ").append(item.getProduit().getPrice()).append(" €")
                    .append(" = ").append(String.format("%.2f", sub)).append(" €\n");
        }

        sb.append("\nTotal : ").append(String.format("%.2f", total)).append(" €");

        return sb.toString();
    }




}
