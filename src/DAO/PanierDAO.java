package DAO;

import Modele.Acheteur;
import Modele.Produit;
import Modele.Vendeur;

import java.sql.*;

public class PanierDAO {

    public void chargerPanier(Acheteur acheteur) {
        String sql = """
        SELECT pi.produit_id, pi.quantite, p.nom, p.prix, p.quantite AS stock, p.vendeur_id, p.image_path, p.marque
        FROM panier pa
        JOIN panier_item pi ON pa.id = pi.panier_id
        JOIN produit p ON pi.produit_id = p.id
        WHERE pa.utilisateur_id = ?
    """;

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, acheteur.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String imagePath = rs.getString("image_path");
                String marque = rs.getString("marque");

                Produit produit = new Produit(
                        rs.getInt("produit_id"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        new Vendeur(rs.getInt("vendeur_id"), "", ""),
                        imagePath, marque);
                int quantite = rs.getInt("quantite");
                acheteur.getPanier().addItem(produit, quantite);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int creerPanier(int utilisateurId) {
        String sql = "INSERT INTO panier (utilisateur_id) VALUES (?)";
        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, utilisateurId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public void ajouterItem(int utilisateurId, Produit produit, int quantite) {
        int panierId = getIdPanier(utilisateurId);

        if (panierId == -1) {
            panierId = creerPanier(utilisateurId);  // Crée un panier pour l'utilisateur si nécessaire
        }
        if (panierId == -1) return;  // Si la création du panier échoue, on arrête

        try (Connection conn = ConnexionBDD.getConnexion()) {
            // vérifier si produit déjà dans le panier
            String verif = "SELECT id, quantite FROM panier_item WHERE panier_id = ? AND produit_id = ?";
            PreparedStatement check = conn.prepareStatement(verif);
            check.setInt(1, panierId);
            check.setInt(2, produit.getId());
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                int itemId = rs.getInt("id");
                int newQuantite = rs.getInt("quantite") + quantite;
                PreparedStatement update = conn.prepareStatement("UPDATE panier_item SET quantite = ? WHERE id = ?");
                update.setInt(1, newQuantite);
                update.setInt(2, itemId);
                update.executeUpdate();
            } else {
                PreparedStatement insert = conn.prepareStatement("INSERT INTO panier_item (panier_id, produit_id, quantite) VALUES (?, ?, ?)");
                insert.setInt(1, panierId);
                insert.setInt(2, produit.getId());
                insert.setInt(3, quantite);
                insert.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viderPanier(int utilisateurId) {
        int panierId = getIdPanier(utilisateurId);

        if (panierId == -1) return;

        String sql = "DELETE FROM panier_item WHERE panier_id = ?";

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, panierId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getIdPanier(int utilisateurId) {
        String sql = "SELECT id FROM panier WHERE utilisateur_id = ?";

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt("id");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
