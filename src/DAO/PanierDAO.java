package DAO;

import Modele.Acheteur;
import Modele.Produit;
import Modele.Vendeur;

import java.sql.*;

/**
 * PanierDAO gère les opérations d'accès à la base de données concernant les paniers :
 * chargement, création, ajout d'articles, suppression d'articles.
 */
public class PanierDAO {

    /**
     * Charge le contenu du panier d'un acheteur depuis la base de données
     * et le remplit dans l'objet Acheteur.
     */
    public void chargerPanier(Acheteur acheteur) {
        String sql = """
        SELECT pi.produit_id, pi.quantite, p.nom, p.prix, p.quantite AS stock, p.vendeur_id, p.image_path, p.marque
        FROM panier pa
        JOIN panier_item pi ON pa.id = pi.panier_id
        JOIN produit p ON pi.produit_id = p.id
        WHERE pa.utilisateur_id = ?
    """;

        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, acheteur.getId());
            ResultSet resultat = stmt.executeQuery();

            while (resultat.next()) {
                String CheminImage = resultat.getString("image_path");
                String marque = resultat.getString("marque");

                Produit produit = new Produit(
                        resultat.getInt("produit_id"),
                        resultat.getString("nom"),
                        resultat.getDouble("prix"),
                        resultat.getInt("stock"),
                        new Vendeur(resultat.getInt("vendeur_id"), "", ""),
                        CheminImage, marque);
                int quantite = resultat.getInt("quantite");
                acheteur.getPanier().ajouterArticle(produit, quantite);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Crée un nouveau panier pour un utilisateur donné en base de données.
     * @return L'identifiant du panier créé, ou -1 en cas d'erreur.
     */
    public int creerPanier(int utilisateurId) {
        String sql = "INSERT INTO panier (utilisateur_id) VALUES (?)";
        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement instruction = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            instruction.setInt(1, utilisateurId);
            instruction.executeUpdate();
            ResultSet rs = instruction.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * Ajoute un article au panier d'un utilisateur.
     * Si le produit est déjà présent, augmente la quantité existante.
     */
    public void ajouterArticle(int utilisateurId, Produit produit, int quantite) {
        int panierId = obtenirIdPanier(utilisateurId);

        if (panierId == -1) {
            panierId = creerPanier(utilisateurId);  // Crée un panier pour l'utilisateur si nécessaire
        }
        if (panierId == -1) return;  // Si la création du panier échoue, on arrête

        try (Connection connexion = ConnexionBDD.obtenirConnexion()) {
            // vérifier si produit déjà dans le panier
            String verification = "SELECT id, quantite FROM panier_item WHERE panier_id = ? AND produit_id = ?";
            PreparedStatement verif = connexion.prepareStatement(verification);
            verif.setInt(1, panierId);
            verif.setInt(2, produit.getId());
            ResultSet resultat = verif.executeQuery();

            if (resultat.next()) {
                int itemId = resultat.getInt("id");
                int newQuantite = resultat.getInt("quantite") + quantite;
                PreparedStatement miseAJour = connexion.prepareStatement("UPDATE panier_item SET quantite = ? WHERE id = ?");
                miseAJour.setInt(1, newQuantite);
                miseAJour.setInt(2, itemId);
                miseAJour.executeUpdate();
            } else {
                PreparedStatement insert = connexion.prepareStatement("INSERT INTO panier_item (panier_id, produit_id, quantite) VALUES (?, ?, ?)");
                insert.setInt(1, panierId);
                insert.setInt(2, produit.getId());
                insert.setInt(3, quantite);
                insert.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vide complètement le panier d'un utilisateur donné.
     */
    public void viderPanier(int utilisateurId) {
        int panierId = obtenirIdPanier(utilisateurId);

        if (panierId == -1) return;

        String sql = "DELETE FROM panier_item WHERE panier_id = ?";

        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement instruction = conn.prepareStatement(sql)) {

            instruction.setInt(1, panierId);
            instruction.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère l'identifiant du panier d'un utilisateur à partir de son ID utilisateur.
     * @return L'identifiant du panier, ou -1 s'il n'existe pas.
     */
    private int obtenirIdPanier(int utilisateurId) {
        String sql = "SELECT id FROM panier WHERE utilisateur_id = ?";

        try (Connection conn = ConnexionBDD.obtenirConnexion();
             PreparedStatement instruction = conn.prepareStatement(sql)) {

            instruction.setInt(1, utilisateurId);
            ResultSet resultat = instruction.executeQuery();

            if (resultat.next()) return resultat.getInt("id");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}