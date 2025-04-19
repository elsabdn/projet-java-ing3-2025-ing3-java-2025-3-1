package DAO;

import Modele.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Vue.advanced.VendeurPanel;

public class DatabaseManager {
    private static final String URL_BDD = "jdbc:mysql://localhost:3306/ecommerce";
    private static final String UTILISATEUR_BDD = "root";
    private static final String MOT_DE_PASSE_BDD = "";

    private Connection connexion;

    public DatabaseManager() {
        try {
            // Chargement du driver JDBC pour MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Établissement de la connexion
            connexion = DriverManager.getConnection(URL_BDD, UTILISATEUR_BDD, MOT_DE_PASSE_BDD);
            System.out.println("Connexion à la base de données établie avec succès !");
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Driver MySQL introuvable");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }

    // Méthode pour fermer la connexion
    public void fermerConnexion() {
        if (connexion != null) {
            try {
                connexion.close();
                System.out.println("Connexion à la base de données fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion");
                e.printStackTrace();
            }
        }
    }

    // === UTILISATEURS ===
    public void addUtilisateur(Utilisateur utilisateur) {
        String requete = "INSERT INTO utilisateur (email, mot_de_passe, role) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connexion.prepareStatement(requete, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, utilisateur.getEmail());
            stmt.setString(2, utilisateur.getMdp());
            stmt.setString(3, utilisateur.getRole());

            int lignesAffectees = stmt.executeUpdate();

            if (lignesAffectees == 0) {
                throw new SQLException("La création de l'utilisateur a échoué");
            }

            try (ResultSet cleGeneree = stmt.getGeneratedKeys()) {
                if (cleGeneree.next()) {
                    utilisateur.setId(cleGeneree.getInt(1));
                } else {
                    throw new SQLException("La création de l'utilisateur a échoué, aucun ID obtenu");
                }
            }

            // Si c'est un acheteur, créer un panier automatiquement
            if (utilisateur instanceof Acheteur) {
                creerPanierPourAcheteur((Acheteur) utilisateur);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur");
            e.printStackTrace();
        }
    }

    private void creerPanierPourAcheteur(Acheteur acheteur) {
        String requete = "INSERT INTO panier (utilisateur_id) VALUES (?)";

        try (PreparedStatement stmt = connexion.prepareStatement(requete)) {
            stmt.setInt(1, acheteur.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du panier");
            e.printStackTrace();
        }
    }

    public List<Utilisateur> getUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String requete = "SELECT * FROM utilisateur";

        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String mdp = rs.getString("mot_de_passe");
                String role = rs.getString("role");

                Utilisateur utilisateur;
                if ("acheteur".equals(role)) {
                    Acheteur acheteur = new Acheteur(id, email, mdp);
                    // Récupérer le panier de l'acheteur
                    chargerPanierPourAcheteur(acheteur);
                    utilisateur = acheteur;
                } else {
                    Vendeur vendeur = new Vendeur(id, email, mdp);
                    // Charger les produits du vendeur
                    chargerProduitsPourVendeur(vendeur);
                    utilisateur = vendeur;
                }

                utilisateurs.add(utilisateur);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs");
            e.printStackTrace();
        }

        return utilisateurs;
    }

    private void chargerPanierPourAcheteur(Acheteur acheteur) {
        // Récupérer le panier de l'acheteur et les produits qu'il contient
        String requete = "SELECT pi.produit_id, pi.quantite, p.nom, p.prix, p.quantite AS stock, p.vendeur_id, p.image_path, p.marque " +
                "FROM panier pa " +
                "JOIN panier_item pi ON pa.id = pi.panier_id " +
                "JOIN produit p ON pi.produit_id = p.id " +
                "WHERE pa.utilisateur_id = ?";

        try (PreparedStatement stmt = connexion.prepareStatement(requete)) {
            stmt.setInt(1, acheteur.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int produitId = rs.getInt("produit_id");
                    int quantite = rs.getInt("quantite");
                    String nom = rs.getString("nom");
                    double prix = rs.getDouble("prix");
                    int stock = rs.getInt("stock");
                    int vendeurId = rs.getInt("vendeur_id");
                    String imagePath = rs.getString("image_path");
                    String marque = rs.getString("marque");


                    // Créer un vendeur temporaire (on n'a pas besoin de tous ses détails ici)
                    Vendeur vendeur = new Vendeur(vendeurId, "", "");

                    // Créer le produit
                    Produit produit = new Produit(produitId, nom, prix, stock, vendeur, imagePath, marque);

                    // Ajouter au panier
                    acheteur.getPanier().addItem(produit, quantite);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du panier");
            e.printStackTrace();
        }
    }

    public int genererIdUtilisateur() {
        int idMax = 0;
        String requete = "SELECT MAX(id) as id_max FROM utilisateur";

        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {

            if (rs.next()) {
                idMax = rs.getInt("id_max");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la génération de l'ID utilisateur");
            e.printStackTrace();
        }

        return idMax + 1;
    }

    // === PRODUITS ===
    public void addProduit(Produit produit) {
        String requete = "INSERT INTO produit (nom, prix, quantite, vendeur_id, image_path, marque) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connexion.prepareStatement(requete, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produit.getNom());
            stmt.setDouble(2, produit.getPrix());
            stmt.setInt(3, produit.getQuantite());
            stmt.setInt(4, produit.getVendeur().getId());
            stmt.setString(5, produit.getImagePath());
            stmt.setString(6, produit.getMarque());

            int lignesAffectees = stmt.executeUpdate();

            if (lignesAffectees == 0) {
                throw new SQLException("La création du produit a échoué");
            }

            try (ResultSet cleGeneree = stmt.getGeneratedKeys()) {
                if (cleGeneree.next()) {
                    // On met à jour l'id du produit avec celui généré par la base
                    produit.setId(cleGeneree.getInt(1));
                } else {
                    throw new SQLException("La création du produit a échoué, aucun ID obtenu");
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit");
            e.printStackTrace();
        }
    }

    public void removeProduit(Produit produit) {
        String requete = "DELETE FROM produit WHERE id = ?";

        try (PreparedStatement stmt = connexion.prepareStatement(requete)) {
            stmt.setInt(1, produit.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit");
            e.printStackTrace();
        }
    }

    public List<Produit> getProduits() {
        List<Produit> produits = new ArrayList<>();
        String requete = "SELECT p.*, u.email FROM produit p JOIN utilisateur u ON p.vendeur_id = u.id";

        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                int quantite = rs.getInt("quantite");
                int vendeurId = rs.getInt("vendeur_id");
                String imagePath = rs.getString("image_path");
                String marque = rs.getString("marque");
                String vendeurEmail = rs.getString("email");

                // Créer un vendeur avec les infos disponibles
                Vendeur vendeur = new Vendeur(vendeurId, vendeurEmail, "");

                // Créer et ajouter le produit
                Produit produit = new Produit(id, nom, prix, quantite, vendeur, imagePath, marque);
                produits.add(produit);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits");
            e.printStackTrace();
        }

        return produits;
    }

    private void chargerProduitsPourVendeur(Vendeur vendeur) {
        String requete = "SELECT * FROM produit WHERE vendeur_id = ?";
        VendeurPanel vendeurPanel = new VendeurPanel(vendeur);
        try (PreparedStatement stmt = connexion.prepareStatement(requete)) {
            stmt.setInt(1, vendeur.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nom = rs.getString("nom");
                    double prix = rs.getDouble("prix");
                    int quantite = rs.getInt("quantite");
                    String imagePath = rs.getString("image_path");
                    String marque = rs.getString("marque");

                    // Créer et ajouter le produit à la liste du vendeur
                    Produit produit = new Produit(id, nom, prix, quantite, vendeur, imagePath, marque);
                    vendeur.addProduit(produit);

                }
                vendeurPanel.updateProduitList(vendeur);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des produits du vendeur");
            e.printStackTrace();
        }
    }

    public int genererIdProduit() {
        int idMax = 0;
        String requete = "SELECT MAX(id) as id_max FROM produit";

        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(requete)) {

            if (rs.next()) {
                idMax = rs.getInt("id_max");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la génération de l'ID produit");
            e.printStackTrace();
        }

        return idMax + 1;
    }

    public List<Produit> getProduitsParVendeur(int vendeurId) {
        List<Produit> produits = new ArrayList<>();
        String requete = "SELECT id, nom, prix, quantite, vendeur_id, image_path, marque FROM produit WHERE vendeur_id = ?";

        try (PreparedStatement stmt = connexion.prepareStatement(requete)) {
            stmt.setInt(1, vendeurId); // On passe l'ID du vendeur à la requête

            try (ResultSet rs = stmt.executeQuery()) {
                // Parcours des résultats de la requête et création des objets Produit
                while (rs.next()) {
                    int produitId = rs.getInt("id");
                    String nom = rs.getString("nom");
                    double prix = rs.getDouble("prix");
                    int quantite = rs.getInt("quantite");
                    String imagePath = rs.getString("image_path");
                    int vendeurIdDb = rs.getInt("vendeur_id");
                    String marque = rs.getString("marque");

                    // Crée un objet Vendeur
                    Vendeur vendeur = new Vendeur(vendeurIdDb, "", ""); // Remplacer "" par les informations appropriées pour le vendeur

                    // Création d'un objet Produit à partir des données récupérées
                    Produit produit = new Produit(produitId, nom, prix, quantite, vendeur, imagePath, marque);

                    // Ajout du produit à la liste des produits
                    produits.add(produit);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits du vendeur");
            e.printStackTrace();
        }

        return produits; // Retourne la liste des produits récupérés
    }


    public Produit getProduitById(int id) {
        String requete = "SELECT p.*, u.email FROM produit p JOIN utilisateur u ON p.vendeur_id = u.id WHERE p.id = ?";

        try (PreparedStatement stmt = connexion.prepareStatement(requete)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nom = rs.getString("nom");
                    double prix = rs.getDouble("prix");
                    int quantite = rs.getInt("quantite");
                    int vendeurId = rs.getInt("vendeur_id");
                    String imagePath = rs.getString("image_path");
                    String marque = rs.getString("marque");
                    String vendeurEmail = rs.getString("email");

                    // Créer un vendeur avec les infos disponibles
                    Vendeur vendeur = new Vendeur(vendeurId, vendeurEmail, "");

                    // Retourner le produit
                    return new Produit(id, nom, prix, quantite, vendeur, imagePath, marque);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du produit par ID");
            e.printStackTrace();
        }

        return null;
    }

    // Méthode pour mettre à jour la quantité d'un produit
    public void mettreAJourQuantiteProduit(int produitId, int nouvelleQuantite) {
        String requete = "UPDATE produit SET quantite = ? WHERE id = ?";

        try (PreparedStatement stmt = connexion.prepareStatement(requete)) {
            stmt.setInt(1, nouvelleQuantite);
            stmt.setInt(2, produitId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la quantité du produit");
            e.printStackTrace();
        }
    }

    // Méthode pour ajouter un item au panier
    public void ajouterItemAuPanier(int utilisateurId, int produitId, int quantite) {
        // D'abord, obtenir l'ID du panier pour cet utilisateur
        int panierId = getIdPanier(utilisateurId);

        if (panierId != -1) {
            // Vérifier si le produit est déjà dans le panier
            String requeteVerif = "SELECT id, quantite FROM panier_item WHERE panier_id = ? AND produit_id = ?";

            try (PreparedStatement stmtVerif = connexion.prepareStatement(requeteVerif)) {
                stmtVerif.setInt(1, panierId);
                stmtVerif.setInt(2, produitId);

                try (ResultSet rs = stmtVerif.executeQuery()) {
                    if (rs.next()) {
                        // Le produit est déjà dans le panier, mettre à jour la quantité
                        int itemId = rs.getInt("id");
                        int nouvelleQuantite = rs.getInt("quantite") + quantite;

                        String requeteMaj = "UPDATE panier_item SET quantite = ? WHERE id = ?";
                        try (PreparedStatement stmtMaj = connexion.prepareStatement(requeteMaj)) {
                            stmtMaj.setInt(1, nouvelleQuantite);
                            stmtMaj.setInt(2, itemId);
                            stmtMaj.executeUpdate();
                        }
                    } else {
                        // Le produit n'est pas dans le panier, l'ajouter
                        String requeteInsert = "INSERT INTO panier_item (panier_id, produit_id, quantite) VALUES (?, ?, ?)";
                        try (PreparedStatement stmtInsert = connexion.prepareStatement(requeteInsert)) {
                            stmtInsert.setInt(1, panierId);
                            stmtInsert.setInt(2, produitId);
                            stmtInsert.setInt(3, quantite);
                            stmtInsert.executeUpdate();
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'ajout d'un item au panier");
                e.printStackTrace();
            }
        }
    }

    private int getIdPanier(int utilisateurId) {
        String requete = "SELECT id FROM panier WHERE utilisateur_id = ?";

        try (PreparedStatement stmt = connexion.prepareStatement(requete)) {
            stmt.setInt(1, utilisateurId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'ID du panier");
            e.printStackTrace();
        }

        return -1;
    }

    // Méthode pour vider le panier après achat
    public void viderPanier(int utilisateurId) {
        int panierId = getIdPanier(utilisateurId);

        if (panierId != -1) {
            String requete = "DELETE FROM panier_item WHERE panier_id = ?";

            try (PreparedStatement stmt = connexion.prepareStatement(requete)) {
                stmt.setInt(1, panierId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Erreur lors du vidage du panier");
                e.printStackTrace();
            }
        }
    }

    // Méthode pour enregistrer une commande
    public int creerCommande(int utilisateurId, double montantTotal) {
        String requete = "INSERT INTO commande (utilisateur_id, montant_total) VALUES (?, ?)";

        try (PreparedStatement stmt = connexion.prepareStatement(requete, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, utilisateurId);
            stmt.setDouble(2, montantTotal);

            int lignesAffectees = stmt.executeUpdate();

            if (lignesAffectees == 0) {
                throw new SQLException("La création de la commande a échoué");
            }

            try (ResultSet cleGeneree = stmt.getGeneratedKeys()) {
                if (cleGeneree.next()) {
                    return cleGeneree.getInt(1);
                } else {
                    throw new SQLException("La création de la commande a échoué, aucun ID obtenu");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la commande");
            e.printStackTrace();
        }

        return -1;
    }

    // Méthode pour ajouter des items à une commande
    public void ajouterItemsALaCommande(int commandeId, List<Panier.Item> items) {
        String requete = "INSERT INTO commande_item (commande_id, produit_id, quantite, prix) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connexion.prepareStatement(requete)) {
            for (Panier.Item item : items) {
                stmt.setInt(1, commandeId);
                stmt.setInt(2, item.getProduit().getId());
                stmt.setInt(3, item.getQuantite());
                stmt.setDouble(4, item.getProduit().getPrix());
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout des items à la commande");
            e.printStackTrace();
        }
    }
}