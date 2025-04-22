package DAO;

import Modele.Acheteur;
import Modele.Commande;
import Modele.Panier;
import Modele.Produit;
import Modele.Vendeur;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandeDAO {

    public int creerCommande(int utilisateurId, double total) {
        String sql = "INSERT INTO commande (utilisateur_id, montant_total) VALUES (?, ?)";

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, utilisateurId);
            stmt.setDouble(2, total);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void ajouterItemsCommande(int commandeId, List<Panier.Item> items) {
        String sql = "INSERT INTO commande_item (commande_id, produit_id, quantite, prix) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Panier.Item item : items) {
                stmt.setInt(1, commandeId);
                stmt.setInt(2, item.getProduit().getId());
                stmt.setInt(3, item.getQuantite());
                stmt.setDouble(4, item.getProduit().getPrix());
                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Commande> getCommandesByUtilisateurId(int utilisateurId) {
        List<Commande> commandes = new ArrayList<>();
        String sqlCommandes = "SELECT * FROM commande WHERE utilisateur_id = ?";
        String sqlItems = "SELECT ci.*, p.nom, p.prix, p.vendeur_id, p.image_path, p.marque FROM commande_item ci " +
                "JOIN produit p ON ci.produit_id = p.id WHERE ci.commande_id = ?";

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmtCmd = conn.prepareStatement(sqlCommandes)) {

            stmtCmd.setInt(1, utilisateurId);
            ResultSet rsCmd = stmtCmd.executeQuery();

            while (rsCmd.next()) {
                int commandeId = rsCmd.getInt("id");
                double montant = rsCmd.getDouble("montant_total");

                List<Panier.Item> items = new ArrayList<>();
                try (PreparedStatement stmtItems = conn.prepareStatement(sqlItems)) {
                    stmtItems.setInt(1, commandeId);
                    ResultSet rsItems = stmtItems.executeQuery();
                    while (rsItems.next()) {
                        String imagePath = rsItems.getString("image_path");
                        String marque = rsItems.getString("marque");
                        Produit p = new Produit(
                                rsItems.getInt("produit_id"),
                                rsItems.getString("nom"),
                                rsItems.getDouble("prix"),
                                0,
                                new Vendeur(rsItems.getInt("vendeur_id"), "", ""),
                                imagePath, marque);
                        int quantite = rsItems.getInt("quantite");
                        items.add(new Panier.Item(p, quantite));
                    }
                }

                commandes.add(new Commande(commandeId, montant, items));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commandes;
    }

    // ✅ Méthode corrigée avec le paramètre acheteur
    public void enregistrerCommande(List<Produit> produits, int note, Acheteur acheteur) {
        try (Connection conn = ConnexionBDD.getConnexion()) {
            String sql = "INSERT INTO commande (utilisateur_id, montant_total, statut, note) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, acheteur.getId()); // ✅ On utilise l'acheteur passé en paramètre
            double total = produits.stream().mapToDouble(Produit::getPrix).sum();
            ps.setDouble(2, total);
            ps.setString(3, "terminee");
            ps.setInt(4, note);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int commandeId = -1;
            if (rs.next()) {
                commandeId = rs.getInt(1);
            }

            // Insertion des produits dans commande_item
            String sql2 = "INSERT INTO commande_item (commande_id, produit_id, quantite) VALUES (?, ?, ?)";
            PreparedStatement ps2 = conn.prepareStatement(sql2);

            Map<Integer, Integer> quantites = new HashMap<>();
            for (Produit p : produits) {
                quantites.merge(p.getId(), 1, Integer::sum);
            }

            for (Map.Entry<Integer, Integer> entry : quantites.entrySet()) {
                ps2.setInt(1, commandeId);
                ps2.setInt(2, entry.getKey());
                ps2.setInt(3, entry.getValue());
                ps2.addBatch();
            }

            ps2.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
