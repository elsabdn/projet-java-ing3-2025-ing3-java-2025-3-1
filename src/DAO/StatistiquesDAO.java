package DAO;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatistiquesDAO {

    private Connection conn;

    public StatistiquesDAO(Connection conn) {
        this.conn = conn;
    }

    // --- 1. Récupérer les ventes par mois ---
    public Map<String, Integer> getVentesParMois() {
        Map<String, Integer> ventesParMois = new HashMap<>();
        String query = "SELECT MONTH(date_commande) AS mois, SUM(montant_total) AS total_ventes " +
                "FROM commande GROUP BY mois ORDER BY mois";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int mois = rs.getInt("mois");
                int totalVentes = rs.getInt("total_ventes");
                String moisNom = getNomMois(mois);
                ventesParMois.put(moisNom, totalVentes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventesParMois;
    }

    // --- 2. Récupérer les produits les plus vendus ---
    public Map<String, Integer> getTopProduits() {
        Map<String, Integer> topProduits = new HashMap<>();
        String query = "SELECT p.nom, SUM(ci.quantite) AS total_vendus " +
                "FROM produit p " +
                "JOIN commande_item ci ON p.id = ci.produit_id " +
                "GROUP BY p.nom ORDER BY total_vendus DESC LIMIT 3";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String produitNom = rs.getString("nom");
                int totalVendus = rs.getInt("total_vendus");
                topProduits.put(produitNom, totalVendus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topProduits;
    }

    // --- 3. Montant total des ventes pour un produit donné ---
    public int getMontantVenteProduit(int produitId) {
        String query = "SELECT SUM(ci.quantite * p.prix) AS total_ventes " +
                "FROM commande_item ci " +
                "JOIN produit p ON ci.produit_id = p.id " +
                "WHERE ci.produit_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_ventes");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // --- 4. Nombre de commandes pour un client sur les 3 derniers mois ---
    public int getNombreCommandesClient3Mois(int clientId) {
        String query = "SELECT COUNT(*) AS nb_commandes FROM commande " +
                "WHERE utilisateur_id = ? AND date_commande >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("nb_commandes");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Renvoie pour chaque produit en promotion en gros
     * le nombre de fois où la quantité commandée
     * a atteint (ou dépassé) le seuil de gros.
     *
     * @return Map<nomProduit, nombreDeLotsVendues>
     * @throws SQLException
     */
    public Map<String, Integer> getOffresBienAccueillies() throws SQLException {
        String sql =
                "SELECT p.nom,\n" +
                        "       SUM(CASE WHEN ci.quantite >= p.seuilGros THEN 1 ELSE 0 END) AS nbLots\n" +
                        "FROM commande_item ci\n" +
                        "JOIN produit p ON ci.produit_id = p.id\n" +
                        "WHERE p.promoEnGros = 1\n" +
                        "GROUP BY p.nom\n" +
                        "HAVING SUM(CASE WHEN ci.quantite >= p.seuilGros THEN 1 ELSE 0 END) > 0\n" +
                        "ORDER BY nbLots DESC";

        Map<String,Integer> result = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(
                        rs.getString("nom"),
                        rs.getInt("nbLots")
                );
            }
        }
        return result;
    }

    // --- 5. Liste des produits ---
    public Map<Integer, String> getListeProduits() {
        Map<Integer, String> produits = new HashMap<>();
        String query = "SELECT id, nom FROM produit";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                produits.put(rs.getInt("id"), rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    // --- 6. Liste des clients ---
    public Map<Integer, String> getListeClients() {
        Map<Integer, String> clients = new HashMap<>();
        String query = "SELECT id, nom FROM utilisateur WHERE role = 'client'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                clients.put(rs.getInt("id"), rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    // --- 7. Obtenir l'ID d'un produit par son nom ---
    public int getProduitIdByName(String nomProduit) {
        String query = "SELECT id FROM produit WHERE nom = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nomProduit);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // --- 8. Obtenir l'ID d'un client par son nom ---
    public int getClientIdByName(String nomClient) {
        String query = "SELECT id FROM utilisateur WHERE nom = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nomClient);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String getNomMois(int mois) {
        String[] moisNom = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        return moisNom[mois - 1];
    }
}