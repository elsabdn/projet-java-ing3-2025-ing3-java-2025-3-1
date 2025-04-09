package Controller;

import Modele.Buyer;
import Modele.Seller;
import Modele.User;
import Database.DatabaseManager;

public class AuthController {
    private DatabaseManager db;

    public AuthController(DatabaseManager db) {
        this.db = db;
    }

    public User login(String email, String password) {
        return db.getUsers().stream()
                .filter(user -> user.getEmail().equals(email) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public Buyer registerBuyer(String email, String password) {
        int id = db.generateUserId();
        Buyer buyer = new Buyer(id, email, password);
        db.addUser(buyer);
        return buyer;
    }

    public Seller registerSeller(String email, String password) {
        int id = db.generateUserId();
        Seller seller = new Seller(id, email, password);
        db.addUser(seller);
        return seller;
    }
}
