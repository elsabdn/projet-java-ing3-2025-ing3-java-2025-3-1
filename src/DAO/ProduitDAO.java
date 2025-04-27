package DAO;

import Modele.Produit;
import Modele.Vendeur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    public void ajouter(Produit p) {
        String sql = "INSERT INTO produit (nom, prix, quantite, vendeur_id, image_path, marque, description, promoEnGros, seuilGros, prixGros) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement instruction = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            instruction.setString(1, p.getNom());
            instruction.setDouble(2, p.getPrix());
            instruction.setInt(3, p.getQuantite());
            instruction.setInt(4, p.getVendeur().getId());
            instruction.setString(5, p.getImageChemin());
            instruction.setString(6, p.getMarque());
            instruction.setString(7, p.getDescription());
            instruction.setBoolean(8, p.estPromoEnGros());
            instruction.setInt(9, p.getSeuilGros());
            instruction.setDouble(10, p.getPrixGros());

            instruction.executeUpdate();

            try (ResultSet rs = instruction.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Produit> recupererTousLesProduits() {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT * FROM produit";
        try (Connection conn = ConnexionBDD.obtenirConnexion();
             Statement instruction = conn.createStatement();
             ResultSet rs = instruction.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                int quantite = rs.getInt("quantite");
                int vendeurId = rs.getInt("vendeur_id");
                String imagePath = rs.getString("image_path");
                String marque = rs.getString("marque");
                String description = rs.getString("description");

                boolean promoEnGros = rs.getBoolean("promoEnGros");
                int seuilGros = rs.getInt("seuilGros");
                double prixGros = rs.getDouble("prixGros");

                Vendeur vendeur = new Vendeur(vendeurId, "", "");
                Produit produit = new Produit(id, nom, prix, quantite, vendeur, imagePath, marque, description);
                produit.setPromoEnGros(promoEnGros);
                produit.setSeuilGros(seuilGros);
                produit.setPrixGros(prixGros);

                list.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void supprimer(Produit produit) {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement instruction = conn.prepareStatement(sql)) {

            instruction.setInt(1, produit.getId());
            instruction.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mettreAJourProduit(Produit produit) {
        String sql = "UPDATE produit SET nom = ?, prix = ?, quantite = ?, image_path = ?, marque = ?, description = ?, promoEnGros = ?, seuilGros = ?, prixGros = ? WHERE id = ?";
        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement instruction = conn.prepareStatement(sql)) {

            instruction.setString(1, produit.getNom());
            instruction.setDouble(2, produit.getPrix());
            instruction.setInt(3, produit.getQuantite());
            instruction.setString(4, produit.getImageChemin());
            instruction.setString(5, produit.getMarque());
            instruction.setString(6, produit.getDescription());
            instruction.setBoolean(7, produit.estPromoEnGros());
            instruction.setInt(8, produit.getSeuilGros());
            instruction.setDouble(9, produit.getPrixGros());
            instruction.setInt(10, produit.getId());

            int lignesModifiees = instruction.executeUpdate();
            if (lignesModifiees > 0) {
                System.out.println("✅ Produit mis à jour avec succès !");
            } else {
                System.out.println("❌ Aucun produit trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}