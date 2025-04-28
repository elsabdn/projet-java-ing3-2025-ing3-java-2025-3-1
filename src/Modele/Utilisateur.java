package Modele;


/**
 * Classe abstraite Utilisateur représentant un utilisateur général de l'application.
 * Cette classe est destinée à être héritée par des classes spécifiques comme Acheteur ou Vendeur.
 */
public abstract class Utilisateur {
    protected int id;
    protected String email;
    protected String mdp;
    protected String role;


    /**
     * Constructeur de la classe Utilisateur.
     * Initialise les champs communs à tout utilisateur.
     *
     * @param id Identifiant unique
     * @param email Adresse email
     * @param mdp Mot de passe
     * @param role Rôle de l'utilisateur
     */
    public Utilisateur(int id, String email, String mdp, String role) {
        this.id = id;
        this.email = email;
        this.mdp = mdp;
        this.role = role;
    }

    // ─── Getters ─────────────────────────────────────
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getMdp() { return mdp; }
    public String getRole() { return role; }

    // ─── Setters ─────────────────────────────────────
    public void setId(int id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setMdp(String mdp) { this.mdp = mdp; }
    public void setRole(String role) { this.role = role; }
}