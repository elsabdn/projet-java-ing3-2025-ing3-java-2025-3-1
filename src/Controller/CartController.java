package Controller;

import Modele.Buyer;
import Modele.Cart;
import Modele.Product;

import java.awt.Font;
import javax.swing.*;

public class CartController {
    private Cart cart;

    public CartController(Buyer buyer) {
        this.cart = buyer.getCart();
    }

    /*
    public addToCart(Buyer buyer, Product product, int quantity) {
        if (product.getQuantity() < quantity) {
            return false; // pas assez de stock
        }

        Cart cart = buyer.getCart();
        cart.addItem(product, quantity);
        product.setQuantity(product.getQuantity() - quantity); // mise à jour du stock

        return true;
    }
     */
    public boolean addToCart(Product product, int quantity) {
        if (product.getQuantity() >= quantity) {
            cart.addItem(product, quantity);
            product.setQuantity(product.getQuantity() - quantity); // déduction stock
            return true;
        } else {
            System.out.println("Stock insuffisant pour " + product.getName());
            return false;
        }
    }


    public void removeFromCart(Buyer buyer, Product product) {
        Cart cart = buyer.getCart();
        cart.removeProduct(product);
    }

    public double calculateTotal(Buyer buyer) {

        return buyer.getCart().getTotalPrice();
    }

    public void checkout(Buyer buyer) {
        Cart cart = buyer.getCart();

        if (cart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Votre panier est vide !");
            return;
        }

        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Total : " + total + " €\nConfirmer l'achat ?", "Paiement",
                JOptionPane.YES_NO_OPTION);

        /*
        if (confirm == JOptionPane.YES_OPTION) {
            cart.clear();
            JOptionPane.showMessageDialog(null, "Paiement effectué avec succès ! ✨");
        }
         */

        if (confirm == JOptionPane.YES_OPTION) {
            String receipt = generateReceipt(buyer);
            cart.clear();

            JTextArea textArea = new JTextArea(receipt);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "Ticket de Commande", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private String generateReceipt(Buyer buyer) {
        StringBuilder sb = new StringBuilder();
        sb.append("🧾 Ticket de Commande\n");
        sb.append("Acheteur : ").append(buyer.getEmail()).append("\n\n");

        double total = 0.0;
        for (Cart.Item item : buyer.getCart().getItems()) {
            double sub = item.getProduct().getPrice() * item.getQuantity();
            total += sub;
            sb.append("- ")
                    .append(item.getProduct().getName())
                    .append(" x").append(item.getQuantity())
                    .append(" @ ").append(item.getProduct().getPrice()).append(" €")
                    .append(" = ").append(String.format("%.2f", sub)).append(" €\n");
        }

        sb.append("\nTotal : ").append(String.format("%.2f", total)).append(" €");

        return sb.toString();
    }




}
