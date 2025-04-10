package Modele;

public abstract class Utilisateur {
    protected int id;
    protected String email;
    protected String mdp;
    protected String role;

    public Utilisateur(int id, String email, String mdp, String role) {
        this.id = id;
        this.email = email;
        this.mdp = mdp;
        this.role = role;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getMdp() { return mdp; }
    public String getRole() { return role; }

    public void setId(int id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setMdp(String mdp) { this.mdp = mdp; }
    public void setRole(String role) { this.role = role; }
}