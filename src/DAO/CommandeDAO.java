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

/**
 * DAO pour gérer les commandes et leurs items.
 */
public class CommandeDAO {

    /**
     * Crée une nouvelle commande (sans note) et renvoie son ID généré.
     */
    public int creerCommande(int utilisateurId, double total) {
        String sql = "INSERT INTO commande (utilisateur_id, montant_total) VALUES (?, ?)";
        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, utilisateurId);
            stmt.setDouble(2, total);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Insère les items d'une commande dans la table commande_item.
     */
    public void ajouterArticlesCommande(int commandeId, List<Panier.Articles> items) {
        String sql = "INSERT INTO commande_item (commande_id, produit_id, quantite, prix) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Panier.Articles item : items) {
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

    /**
     * Récupère la liste des commandes d'un utilisateur, avec note, date et détails des items.
     */
    public List<Commande> recupererCommandesParUtilisateurId(int utilisateurId) {
        List<Commande> commandes = new ArrayList<>();

        // on sélectionne note et date_commande
        String sqlCommandes = ""
                + "SELECT id, montant_total, note, date_commande "
                + "FROM commande WHERE utilisateur_id = ?";

        String sqlItems = ""
                + "SELECT ci.produit_id, ci.quantite, ci.prix, "
                + "       p.nom, p.vendeur_id, p.image_path, p.marque "
                + "  FROM commande_item ci "
                + "  JOIN produit p ON ci.produit_id = p.id "
                + " WHERE ci.commande_id = ?";

        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement psCmd = conn.prepareStatement(sqlCommandes)) {

            psCmd.setInt(1, utilisateurId);
            try (ResultSet rsCmd = psCmd.executeQuery()) {
                while (rsCmd.next()) {
                    int commandeId = rsCmd.getInt("id");
                    double montant = rsCmd.getDouble("montant_total");
                    int note       = rsCmd.getInt("note");
                    Timestamp ts    = rsCmd.getTimestamp("date_commande");
                    java.util.Date date = ts != null ? new java.util.Date(ts.getTime()) : null;

                    // Chargement des items
                    List<Panier.Articles> items = new ArrayList<>();
                    try (PreparedStatement psItems = conn.prepareStatement(sqlItems)) {
                        psItems.setInt(1, commandeId);
                        try (ResultSet rsIt = psItems.executeQuery()) {
                            while (rsIt.next()) {
                                Produit p = new Produit(
                                        rsIt.getInt("produit_id"),
                                        rsIt.getString("nom"),
                                        rsIt.getDouble("prix"),
                                        0,
                                        new Vendeur(rsIt.getInt("vendeur_id"), "", ""),
                                        rsIt.getString("image_path"),
                                        rsIt.getString("marque")
                                );
                                int quantite = rsIt.getInt("quantite");
                                items.add(new Panier.Articles(p, quantite));
                            }
                        }
                    }

                    // On utilise désormais le constructeur (id, montant, note, date, items)
                    commandes.add(new Commande(commandeId, montant, note, date, items));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commandes;
    }

    /**
     * Enregistre une commande terminée avec note et insère ses items.
     */
    public void enregistrerCommande(List<Produit> produits, int note, Acheteur acheteur) {
        String sqlInsertCommande =
                "INSERT INTO commande (utilisateur_id, montant_total, statut, note) "
                        + "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement psCmd = conn.prepareStatement(sqlInsertCommande, Statement.RETURN_GENERATED_KEYS)) {

            psCmd.setInt(1, acheteur.getId());
            double total = produits.stream().mapToDouble(Produit::getPrix).sum();
            psCmd.setDouble(2, total);
            psCmd.setString(3, "terminee");
            psCmd.setInt(4, note);
            psCmd.executeUpdate();

            int commandeId = -1;
            try (ResultSet rs = psCmd.getGeneratedKeys()) {
                if (rs.next()) {
                    commandeId = rs.getInt(1);
                }
            }

            // On regroupe par quantité
            Map<Integer, Integer> quantites = new HashMap<>();
            for (Produit p : produits) {
                quantites.merge(p.getId(), 1, Integer::sum);
            }

            // Insertion des items
            String sqlInsertItems =
                    "INSERT INTO commande_item (commande_id, produit_id, quantite, prix) "
                            + "VALUES (?, ?, ?, ?)";
            try (PreparedStatement psIt = conn.prepareStatement(sqlInsertItems)) {
                for (Map.Entry<Integer,Integer> e : quantites.entrySet()) {
                    psIt.setInt(1, commandeId);
                    psIt.setInt(2, e.getKey());
                    psIt.setInt(3, e.getValue());
                    // on réutilise le prix de l'un des produits (à adapter si besoin)
                    double prixUnitaire = produits.stream()
                            .filter(p -> p.getId() == e.getKey())
                            .findFirst()
                            //.map(Produit::getPrix)

                            //// Modifications pour le prix de gros ////
                            .map(p -> {
                                int qte = quantites.get(p.getId());
                                if (p.isPromoEnGros() && qte >= p.getSeuilGros()) {
                                    return p.getPrixGros();
                                } else {
                                    return p.getPrix();
                                }
                            })
                            ////

                            .orElse(0.0);
                    psIt.setDouble(4, prixUnitaire);
                    psIt.addBatch();
                }
                psIt.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}