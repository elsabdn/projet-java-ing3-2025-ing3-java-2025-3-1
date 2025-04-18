package DAO;

import Modele.Commande;
import Modele.Panier;
import Modele.Produit;
import Modele.Vendeur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


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
        String sqlItems = "SELECT ci.*, p.nom, p.prix, p.vendeur_id FROM commande_item ci " +
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
                        Produit p = new Produit(
                                rsItems.getInt("produit_id"),
                                rsItems.getString("nom"),
                                rsItems.getDouble("prix"),
                                0,
                                new Vendeur(rsItems.getInt("vendeur_id"), "", "")
                        );
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

}
