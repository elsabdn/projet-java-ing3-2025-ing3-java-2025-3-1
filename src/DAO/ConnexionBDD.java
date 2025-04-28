package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConnexionBDD est une classe utilitaire pour établir une connexion à la base de données.
 * Elle fournit une méthode statique pour obtenir une connexion MySQL.
 */
public class ConnexionBDD {
    private static final String URL = "jdbc:mysql://localhost:8889/ecommerce";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    /**
     * Méthode statique pour obtenir une connexion à la base de données.
     *
     * @return Une instance Connection connectée à la base
     * @throws SQLException En cas de problème de connexion
     */
    public static Connection obtenirConnexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
