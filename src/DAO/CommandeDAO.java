package DAO;

import Modele.Panier;

import java.sql.*;
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
}
