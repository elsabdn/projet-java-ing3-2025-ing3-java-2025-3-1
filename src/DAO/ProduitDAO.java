package DAO;

import Modele.Produit;
import Modele.Vendeur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    public void ajouter(Produit p) {
        String sql = "INSERT INTO produit (nom, prix, quantite, vendeur_id, image_path, marque) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getNom());
            stmt.setDouble(2, p.getPrix());
            stmt.setInt(3, p.getQuantite());
            stmt.setInt(4, p.getVendeur().getId());
            stmt.setString(5, p.getImagePath());
            stmt.setString(6, p.getMarque());


            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Produit> getAll() {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT * FROM produit";

        try (Connection conn = ConnexionBDD.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                int quantite = rs.getInt("quantite");
                int vendeurId = rs.getInt("vendeur_id");
                String imagePath = rs.getString("image_path");
                String marque = rs.getString("marque");


                Vendeur vendeur = new Vendeur(vendeurId, "", "");
                list.add(new Produit(id, nom, prix, quantite, vendeur, imagePath, marque));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public void supprimer(Produit produit) {
        String sql = "DELETE FROM produit WHERE id = ?";

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, produit.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mettreAJourProduit(Produit produit) {
        String sql = "UPDATE produit SET nom = ?, prix = ?, quantite = ?, image_path = ?, marque = ? WHERE id = ?";

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Vérification du chemin d'image
            System.out.println("Chemin de l'image : " + produit.getImagePath());  // Log pour vérifier le chemin
            // On met à jour tous les champs du produit
            stmt.setString(1, produit.getNom());
            stmt.setDouble(2, produit.getPrix());
            stmt.setInt(3, produit.getQuantite());
            stmt.setString(4, produit.getImagePath());
            stmt.setString(5, produit.getMarque());
            stmt.setInt(6, produit.getId());  // Id du produit à mettre à jour

            // Exécution de la mise à jour
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le produit a été mis à jour avec succès !");
            } else {
                System.out.println("Aucun produit trouvé avec cet ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
