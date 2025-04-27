package DAO;

import Modele.*;
import Vue.advanced.MainFrame;
import Vue.advanced.VendeurPanel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL_BDD          = "jdbc:mysql://localhost:3306/ecommerce";
    private static final String UTILISATEUR_BDD  = "root";
    private static final String MOT_DE_PASSE_BDD = "";

    private Connection connexion; // <= On garde SEULEMENT celle-ci

    private static DatabaseManager instance;

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connexion = DriverManager.getConnection(URL_BDD, UTILISATEUR_BDD, MOT_DE_PASSE_BDD);
            System.out.println("Connexion à la base de données établie avec succès !");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL introuvable");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connexion; // <= ON RÉPOND AVEC connexion, pas "connection" !
    }

    public void fermerConnexion() {
        if (connexion != null) {
            try {
                connexion.close();
                System.out.println("Connexion à la base fermée");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion");
                e.printStackTrace();
            }
        }
    }

    // === UTILISATEURS ===

    public void addUtilisateur(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (email, mot_de_passe, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, utilisateur.getEmail());
            stmt.setString(2, utilisateur.getMdp());
            stmt.setString(3, utilisateur.getRole());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    utilisateur.setId(rs.getInt(1));
                }
            }
            if (utilisateur instanceof Acheteur) {
                creerPanierPourAcheteur((Acheteur) utilisateur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur");
            e.printStackTrace();
        }
    }

    private void creerPanierPourAcheteur(Acheteur acheteur) {
        String sql = "INSERT INTO panier (utilisateur_id) VALUES (?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, acheteur.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du panier");
            e.printStackTrace();
        }
    }

    public List<Utilisateur> getUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int    id    = rs.getInt("id");
                String email = rs.getString("email");
                String mdp   = rs.getString("mot_de_passe");
                String role  = rs.getString("role");
                if ("acheteur".equals(role)) {
                    Acheteur a = new Acheteur(id, email, mdp);
                    chargerPanierPourAcheteur(a);
                    utilisateurs.add(a);
                } else {
                    Vendeur v = new Vendeur(id, email, mdp);
                    chargerProduitsPourVendeur(v);
                    utilisateurs.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs");
            e.printStackTrace();
        }
        return utilisateurs;
    }

    private void chargerPanierPourAcheteur(Acheteur acheteur) {
        String sql =
                "SELECT pi.produit_id, pi.quantite, p.nom, p.prix, p.quantite AS stock, " +
                        "p.vendeur_id, p.image_path, p.marque, p.description " +
                        "FROM panier pa " +
                        "JOIN panier_item pi ON pa.id = pi.panier_id " +
                        "JOIN produit p ON pi.produit_id = p.id " +
                        "WHERE pa.utilisateur_id = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, acheteur.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int    produitId = rs.getInt("produit_id");
                    int    quantite  = rs.getInt("quantite");
                    String nom       = rs.getString("nom");
                    double prix      = rs.getDouble("prix");
                    int    stock     = rs.getInt("stock");
                    int    vid       = rs.getInt("vendeur_id");
                    String img       = rs.getString("image_path");
                    String marque    = rs.getString("marque");
                    String desc      = rs.getString("description");

                    Vendeur v = new Vendeur(vid, "", "");
                    Produit p = new Produit(produitId, nom, prix, stock, v, img, marque, desc);
                    acheteur.getPanier().ajouterArticle(p, quantite);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du panier");
            e.printStackTrace();
        }
    }

    public int genererIdUtilisateur() {
        int idMax = 0;
        String sql = "SELECT MAX(id) AS id_max FROM utilisateur";
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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

    public void ajouterProduit(Produit produit) {
        String sql =
                "INSERT INTO produit (nom, prix, quantite, vendeur_id, image_path, marque, description) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produit.getNom());
            stmt.setDouble(2, produit.getPrix());
            stmt.setInt(3, produit.getQuantite());
            stmt.setInt(4, produit.getVendeur().getId());
            stmt.setString(5, produit.getImageChemin());
            stmt.setString(6, produit.getMarque());
            stmt.setString(7, produit.getDescription());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) produit.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit");
            e.printStackTrace();
        }
    }

    public void supprimerProduit(Produit produit) {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, produit.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit");
            e.printStackTrace();
        }
    }

    public List<Produit> getProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql =
                "SELECT p.id, p.nom, p.prix, p.quantite, p.vendeur_id, " +
                        "       p.image_path, p.marque, p.description, " +
                        "       p.promoEnGros, p.seuilGros, p.prixGros, " +
                        "       u.email " +
                        "  FROM produit p " +
                        "  JOIN utilisateur u ON p.vendeur_id = u.id";
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produit p = new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix"),
                        rs.getInt("quantite"),
                        new Vendeur(rs.getInt("vendeur_id"), rs.getString("email"), ""),
                        rs.getString("image_path"),
                        rs.getString("marque"),
                        rs.getString("description")
                );
                p.setPromoEnGros( rs.getBoolean("promoEnGros") );
                p.setSeuilGros(    rs.getInt    ("seuilGros")   );
                p.setPrixGros(     rs.getDouble ("prixGros")    );
                produits.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }


    private void chargerProduitsPourVendeur(Vendeur vendeur) {
        String sql = "SELECT * FROM produit WHERE vendeur_id = ?";
        VendeurPanel vp = new VendeurPanel(vendeur, new MainFrame());
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, vendeur.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int    id       = rs.getInt("id");
                    String nom      = rs.getString("nom");
                    double prix     = rs.getDouble("prix");
                    int    quantite = rs.getInt("quantite");
                    String img      = rs.getString("image_path");
                    String marque   = rs.getString("marque");
                    String desc     = rs.getString("description");

                    Produit p = new Produit(id, nom, prix, quantite, vendeur, img, marque, desc);
                    vendeur.ajouterProduit(p);
                }
                vp.updateProduitList(vendeur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des produits du vendeur");
            e.printStackTrace();
        }
    }

    public int genererIdProduit() {
        int idMax = 0;
        String sql = "SELECT MAX(id) AS id_max FROM produit";
        try (Statement stmt = connexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
        String sql =
                "SELECT id, nom, prix, quantite, image_path, marque, description, promoEnGros, seuilGros, prixGros " +
                        "FROM produit WHERE vendeur_id = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, vendeurId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vendeur v = new Vendeur(vendeurId, "", "");
                    Produit p = new Produit(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getDouble("prix"),
                            rs.getInt("quantite"),
                            v,
                            rs.getString("image_path"),
                            rs.getString("marque"),
                            rs.getString("description")
                    );
                    p.setPromoEnGros(rs.getBoolean("promoEnGros"));
                    p.setSeuilGros  (rs.getInt("seuilGros"));
                    p.setPrixGros   (rs.getDouble("prixGros"));
                    produits.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits du vendeur");
            e.printStackTrace();
        }
        return produits;
    }

    public Produit obtenirProduitParId(int idRecherche) {
        String sql =
                "SELECT p.nom, p.prix, p.quantite, p.vendeur_id, p.image_path, " +
                        "p.marque, p.description, u.email " +
                        "FROM produit p JOIN utilisateur u ON p.vendeur_id = u.id " +
                        "WHERE p.id = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, idRecherche);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nom       = rs.getString("nom");
                    double prix      = rs.getDouble("prix");
                    int    quantite  = rs.getInt("quantite");
                    int    vid       = rs.getInt("vendeur_id");
                    String img       = rs.getString("image_path");
                    String marque    = rs.getString("marque");
                    String desc      = rs.getString("description");
                    String emailVend = rs.getString("email");

                    Vendeur v = new Vendeur(vid, emailVend, "");
                    return new Produit(idRecherche, nom, prix, quantite, v, img, marque, desc);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du produit par ID");
            e.printStackTrace();
        }
        return null;
    }

    public void mettreAJourQuantiteProduit(int produitId, int nouvelleQuantite) {
        String sql = "UPDATE produit SET quantite = ? WHERE id = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, nouvelleQuantite);
            stmt.setInt(2, produitId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la quantité du produit");
            e.printStackTrace();
        }
    }

    public void ajouterItemAuPanier(int utilisateurId, int produitId, int quantite) {
        int panierId = getIdPanier(utilisateurId);
        if (panierId == -1) return;

        String verif = "SELECT id, quantite FROM panier_item WHERE panier_id = ? AND produit_id = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(verif)) {
            stmt.setInt(1, panierId);
            stmt.setInt(2, produitId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int itemId = rs.getInt("id");
                    int newQte = rs.getInt("quantite") + quantite;
                    String maj = "UPDATE panier_item SET quantite = ? WHERE id = ?";
                    try (PreparedStatement u = connexion.prepareStatement(maj)) {
                        u.setInt(1, newQte);
                        u.setInt(2, itemId);
                        u.executeUpdate();
                    }
                } else {
                    String ins = "INSERT INTO panier_item (panier_id, produit_id, quantite) VALUES (?, ?, ?)";
                    try (PreparedStatement i = connexion.prepareStatement(ins)) {
                        i.setInt(1, panierId);
                        i.setInt(2, produitId);
                        i.setInt(3, quantite);
                        i.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'un item au panier");
            e.printStackTrace();
        }
    }

    private int getIdPanier(int utilisateurId) {
        String sql = "SELECT id FROM panier WHERE utilisateur_id = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
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

    public void viderPanier(int utilisateurId) {
        int panierId = getIdPanier(utilisateurId);
        if (panierId == -1) return;

        String sql = "DELETE FROM panier_item WHERE panier_id = ?";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, panierId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors du vidage du panier");
            e.printStackTrace();
        }
    }

    public int creerCommande(int utilisateurId, double montantTotal) {
        String sql = "INSERT INTO commande (utilisateur_id, montant_total) VALUES (?, ?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, utilisateurId);
            stmt.setDouble(2, montantTotal);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la commande");
            e.printStackTrace();
        }
        return -1;
    }

    public void ajouterItemsALaCommande(int commandeId, List<Panier.Articles> items) {
        String sql = "INSERT INTO commande_item (commande_id, produit_id, quantite, prix) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            for (Panier.Articles item : items) {
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