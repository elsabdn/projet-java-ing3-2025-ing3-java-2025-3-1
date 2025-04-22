package DAO;

import Modele.Acheteur;
import Modele.Utilisateur;
import Modele.Vendeur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {
    public void ajouter(Utilisateur u) {
        String sql = "INSERT INTO utilisateur (email, mot_de_passe, role) VALUES (?, ?, ?)";

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, u.getEmail());
            stmt.setString(2, u.getMdp());
            stmt.setString(3, u.getRole());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Utilisateur> getAll() {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";

        try (Connection conn = ConnexionBDD.getConnexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String mdp = rs.getString("mot_de_passe");
                String role = rs.getString("role");

                if (role.equals("acheteur"))
                    list.add(new Acheteur(id, email, mdp));
                else
                    list.add(new Vendeur(id, email, mdp));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Utilisateur getByEmailAndMdp(String email, String mdp) {
        String sql = "SELECT * FROM utilisateur WHERE email = ? AND mot_de_passe = ?";

        try (Connection conn = ConnexionBDD.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, mdp);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String role = rs.getString("role");

                    return role.equals("acheteur") ?
                            new Acheteur(id, email, mdp) :
                            new Vendeur(id, email, mdp);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}