package Modele;

public class Buyer extends User {
    private Cart cart;

    public Buyer(int id, String email, String password) {
        super(id, email, password, "buyer");
        this.cart = new Cart(this);
    }

    public Cart getCart() { return cart; }
}
