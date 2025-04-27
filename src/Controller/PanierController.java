package Controller;

import Modele.Acheteur;
import Modele.Panier;
import Modele.Produit;
import DAO.CommandeDAO;
import DAO.PanierDAO;
import DAO.ProduitDAO;

import javax.swing.*;
import java.awt.*;

public class PanierController {
    private final Acheteur acheteur;
    private final PanierDAO panierDAO = new PanierDAO();
    private final CommandeDAO commandeDAO = new CommandeDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();

    public PanierController(Acheteur acheteur) {
        this.acheteur = acheteur;
        panierDAO.chargerPanier(acheteur);
    }

    public boolean ajouterAuPanier(Produit produit, int quantite) {
        if (produit.getQuantite() >= quantite) {
            acheteur.getPanier().ajouterArticle(produit, quantite);
            produit.setQuantite(produit.getQuantite() - quantite);
            produitDAO.mettreAJourProduit(produit);
            panierDAO.ajouterArticle(acheteur.getId(), produit, quantite);
            return true;
        }
        return false;
    }

    // Méthode pour afficher le contenu du panier
    public void afficherPanier(Acheteur acheteur) {
        // Charge les produits dans le panier de l'acheteur
        panierDAO.chargerPanier(acheteur);

        // Récupère le panier de l'acheteur
        Panier panier = acheteur.getPanier();

        // Vérifie si le panier est vide ou non
        if (panier.getItems().isEmpty()) {
            System.out.println("Le panier est vide.");
        } else {
            // Si le panier n'est pas vide, affiche les produits
            System.out.println("Contenu du panier :");
            for (Panier.Articles item : panier.getItems()) {
                System.out.println("Produit: " + item.getProduit().getNom() + ", Quantité: " + item.getQuantite());
            }
        }
    }

    public void validerPanier() {
        Panier panier = acheteur.getPanier();
        if (panier.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Votre panier est vide !");
            return;
        }

        // ✅ Nouveau calcul du total avec gestion du prix en gros
        double total = 0.0;
        for (Panier.Articles item : panier.getItems()) {
            Produit p = item.getProduit();
            int qte = item.getQuantite();

            if (p.estPromoEnGros() && qte >= p.getSeuilGros()) {
                total += p.getPrixGros() * qte;
            } else {
                total += p.getPrix() * qte;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Total : " + String.format("%.2f", total) + " €\nConfirmer l'achat ?", "Paiement",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int commandeId = commandeDAO.creerCommande(acheteur.getId(), total);
            commandeDAO.ajouterArticlesCommande(commandeId, panier.getItems());
            panierDAO.viderPanier(acheteur.getId());
            panier.clear();

            JTextArea textArea = new JTextArea(genererTicket());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "Ticket de Commande", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String genererTicket() {
        StringBuilder sb = new StringBuilder();
        sb.append("🧾 Ticket de Commande\n");
        sb.append("Acheteur : ").append(acheteur.getEmail()).append("\n\n");

        double total = 0.0;
        for (Panier.Articles item : acheteur.getPanier().getItems()) {
            Produit produit = item.getProduit();
            int qte = item.getQuantite();

            double prixUnitaire;
            if (produit.estPromoEnGros() && qte >= produit.getSeuilGros()) {
                prixUnitaire = produit.getPrixGros();
            } else {
                prixUnitaire = produit.getPrix();
            }

            double sousTotal = prixUnitaire * qte;
            total += sousTotal;

            sb.append("- ").append(produit.getNom())
                    .append(" x").append(qte)
                    .append(" @ ").append(String.format("%.2f", prixUnitaire)).append(" €")
                    .append(" = ").append(String.format("%.2f", sousTotal)).append(" €\n");
        }

        sb.append("\nTotal : ").append(String.format("%.2f", total)).append(" €");
        return sb.toString();
    }
}