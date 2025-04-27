package Vue.advanced;

import Modele.Produit;
import Controller.ProduitController;
import DAO.CommandeDAO;
import Modele.Acheteur;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel d'affichage et gestion du panier d'achat d'un utilisateur.
 * Regroupe les produits ajoutés, permet leur suppression, et valide la commande.
 */
public class PanierPanel extends JPanel {
    private final MainFrame mainFrame;            // Référence à la frame principale
    private final List<Produit> panier;           // Liste des produits dans le panier
    private final ProduitController produitController = new ProduitController();

    public PanierPanel(MainFrame mainFrame, List<Produit> panier) {
        this.mainFrame = mainFrame;
        this.panier    = panier;

        setLayout(new BorderLayout());
        setOpaque(false);

        //----------------------------- HEADER ---------------------------------
        JButton btnDeconnexion = createStyledButton("🚪 Déconnexion");
        btnDeconnexion.setPreferredSize(new Dimension(140, 35));
        btnDeconnexion.addActionListener(e -> mainFrame.afficherPanel("accueil"));

        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutWrapper.setOpaque(false);
        logoutWrapper.add(btnDeconnexion);

        Dimension eastSize = logoutWrapper.getPreferredSize();
        JPanel leftFiller = new JPanel();
        leftFiller.setOpaque(false);
        leftFiller.setPreferredSize(eastSize);

        JLabel lblTitre = new JLabel("Mon panier", SwingConstants.CENTER);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitre.setForeground(new Color(92, 92, 92));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        header.add(leftFiller,    BorderLayout.WEST);
        header.add(lblTitre,      BorderLayout.CENTER);
        header.add(logoutWrapper, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        //----------------------------- CENTRE ---------------------------------
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        if (panier.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            JLabel msg = new JLabel("🛒 Votre panier est vide !");
            msg.setFont(new Font("SansSerif", Font.BOLD, 28));
            msg.setForeground(new Color(120,120,120));
            emptyPanel.add(msg, new GridBagConstraints());
            centerWrapper.add(emptyPanel, BorderLayout.CENTER);
        } else {
            // regroupement des produits identiques
            Map<Integer,Integer> quantites       = new LinkedHashMap<>();
            Map<Integer,Produit> produitsUniques = new LinkedHashMap<>();
            for (Produit p : panier) {
                quantites.merge(p.getId(), 1, Integer::sum);
                produitsUniques.putIfAbsent(p.getId(), p);
            }

            JPanel listPanel = new JPanel();
            listPanel.setBackground(Color.WHITE);
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            for (Map.Entry<Integer,Produit> e : produitsUniques.entrySet()) {
                Produit p = e.getValue();
                int qty   = quantites.get(p.getId());
                listPanel.add(creerCarteProduit(p, qty));
                listPanel.add(Box.createVerticalStrut(10));
            }

            JScrollPane scroll = new JScrollPane(listPanel);
            scroll.setBorder(null);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.setPreferredSize(new Dimension(0, 470));

            centerWrapper.add(scroll, BorderLayout.CENTER);
        }

        add(centerWrapper, BorderLayout.CENTER);

        //---------------------------- RESUME & ACTIONS ------------------------
        JPanel resume = new JPanel();
        resume.setLayout(new BoxLayout(resume, BoxLayout.Y_AXIS));
        resume.setBackground(new Color(250,250,250));
        resume.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20,20,20,20),
                BorderFactory.createLineBorder(new Color(200,200,200),1)
        ));
        Dimension tailleResume = new Dimension(320,220);
        resume.setPreferredSize(tailleResume);

        // calcul des totaux
        final Map<Integer,Integer> qtes = new HashMap<>();
        for (Produit p : panier) qtes.merge(p.getId(), 1, Integer::sum);
        int totalQty = panier.size();

        final double totalPx;
        {
            double sum = 0;
            for (Map.Entry<Integer,Integer> e : qtes.entrySet()) {
                Produit p = panier.stream()
                        .filter(prod -> prod.getId()==e.getKey())
                        .findFirst().get();
                int qte = e.getValue();
                if (p.estPromoEnGros() && qte >= p.getSeuilGros()) {
                    int lots  = qte / p.getSeuilGros();
                    int reste = qte % p.getSeuilGros();
                    sum += lots * p.getPrixGros() + reste * p.getPrix();
                } else sum += qte * p.getPrix();
            }
            totalPx = sum; // première et unique affectation => effectively final
        }

        JLabel lblResume   = new JLabel("Résumé de la commande");
        JLabel lblArticles = new JLabel("Articles : " + totalQty);
        JLabel lblTotal    = new JLabel(String.format("Total : %.2f €", totalPx));
        for (JLabel l : new JLabel[]{lblResume,lblArticles,lblTotal}) l.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblResume.setFont(new Font("SansSerif", Font.BOLD,18));
        lblTotal.setBorder(new EmptyBorder(10,0,20,0));

        JButton btnRetour  = createStyledButton("⬅ Retour aux produits");
        JButton btnValider = createStyledButton("Valider la commande");
        Dimension btnSize = new Dimension(300,40);
        for (JButton b : new JButton[]{btnRetour,btnValider}) {
            b.setPreferredSize(btnSize);
            b.setMaximumSize(btnSize);
            b.setMinimumSize(btnSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        btnRetour.addActionListener(e -> mainFrame.afficherPanel("acheteur"));

        //----------------------- Action validation ---------------------------
        btnValider.addActionListener(e -> {
            final PaiementPanel paiement = new PaiementPanel(totalPx);
            paiement.definirCancelAction(evt -> mainFrame.afficherPanel("panier"));
            paiement.definirConfirmPaymentAction(evt -> {
                int note = paiement.obtenirNote();
                if (note < 1 || note > 10) {
                    JOptionPane.showMessageDialog(mainFrame, "Merci de saisir une note entre 1 et 10.");
                    return;
                }
                Acheteur acheteur = mainFrame.obtenirAcheteurConnecte();
                if (acheteur == null) {
                    JOptionPane.showMessageDialog(mainFrame,"Erreur : aucun utilisateur connecté.");
                    return;
                }
                CommandeDAO dao = new CommandeDAO();
                dao.enregistrerCommande(panier, note, acheteur);

                // mise à jour des stocks
                for (Map.Entry<Integer,Integer> ent : qtes.entrySet()) {
                    Produit p = panier.stream()
                            .filter(prod -> prod.getId()==ent.getKey())
                            .findFirst().get();
                    p.setQuantite(p.getQuantite() - ent.getValue());
                    produitController.mettreAJourProduit(p);
                }

                panier.clear();

                JOptionPane.showMessageDialog(mainFrame,
                        "Merci pour votre commande !",
                        "Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);

                mainFrame.afficherPanel("acheteur");
            });
            mainFrame.ajouterPanel(paiement, "paiement");
            mainFrame.afficherPanel("paiement");
        });

        resume.add(lblResume);
        resume.add(Box.createVerticalStrut(10));
        resume.add(lblArticles);
        resume.add(lblTotal);
        resume.add(Box.createVerticalGlue());
        resume.add(btnRetour);
        resume.add(Box.createVerticalStrut(20));
        resume.add(btnValider);

        add(resume, BorderLayout.EAST);
    }

    // ───────────────────────────── création d'une carte produit ─────────────────────────────
    private JPanel creerCarteProduit(Produit produit, int quantite) {
        JPanel carte = new JPanel(new BorderLayout());
        carte.setOpaque(false);
        carte.setBackground(Color.WHITE);
        carte.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(220,220,220)));
        carte.setPreferredSize(new Dimension(0,150));
        carte.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        if (produit.getImageChemin()!=null && !produit.getImageChemin().isEmpty()) {
            Image img = redimensionnerImage(produit.getImageChemin(), 100,100);
            if (img != null) {
                JLabel imgLbl = new JLabel(new ImageIcon(img));
                imgLbl.setBorder(new EmptyBorder(10,10,0,10));
                carte.add(imgLbl, BorderLayout.WEST);
            }
        }

        JPanel infos = new JPanel();
        infos.setOpaque(false);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.setBorder(new EmptyBorder(10,10,10,10));

        JLabel lblNom = new JLabel(produit.getNom());
        lblNom.setFont(new Font("SansSerif", Font.BOLD,16));
        lblNom.setAlignmentX(Component.CENTER_ALIGNMENT);
        infos.add(lblNom);
        infos.add(Box.createVerticalStrut(5));

        JLabel lblStock = new JLabel("Stock : "+produit.getQuantite());
        lblStock.setForeground(new Color(150,150,150));
        lblStock.setAlignmentX(Component.CENTER_ALIGNMENT);
        infos.add(lblStock);
        infos.add(Box.createVerticalStrut(10));

        double prixUnitaire = produit.getPrix();
        double prixStandard = quantite * prixUnitaire;
        double prixPromo    = prixStandard;
        if (produit.estPromoEnGros() && quantite >= produit.getSeuilGros()) {
            int lots  = quantite / produit.getSeuilGros();
            int reste = quantite % produit.getSeuilGros();
            prixPromo = lots * produit.getPrixGros() + reste * produit.getPrix();
        }

        String standardTxt = String.format("%d × %.2f € = %.2f €", quantite, prixUnitaire, prixStandard);
        JLabel lblStandard = new JLabel(standardTxt);
        lblStandard.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStandard.setHorizontalAlignment(SwingConstants.CENTER);
        if (prixPromo < prixStandard) {
            lblStandard.setText("<html><strike>"+standardTxt+"</strike></html>");
            infos.add(lblStandard);
            infos.add(Box.createVerticalStrut(4));
            JLabel lblPromo = new JLabel(String.format("Total avec promo : %.2f €", prixPromo));
            lblPromo.setForeground(Color.RED);
            lblPromo.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblPromo.setHorizontalAlignment(SwingConstants.CENTER);
            infos.add(lblPromo);
        } else infos.add(lblStandard);

        carte.add(infos, BorderLayout.CENTER);

        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setFont(new Font("SansSerif", Font.BOLD,14));
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.setBackground(new Color(220,50,50));
        btnSupprimer.setFocusPainted(false);
        btnSupprimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSupprimer.setPreferredSize(new Dimension(150,35));
        btnSupprimer.addActionListener(e -> {
            panier.removeIf(p -> p.getId()==produit.getId());
            mainFrame.ajouterPanel(new PanierPanel(mainFrame, panier), "panier");
            mainFrame.afficherPanel("panier");
        });
        JPanel supprWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        supprWrapper.setOpaque(false);
        supprWrapper.add(btnSupprimer);
        carte.add(supprWrapper, BorderLayout.EAST);

        return carte;
    }

    // ───────────────────────────────────── utilitaires ─────────────────────────────────────
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD,16));
        btn.setBackground(new Color(248,187,208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(244,143,177)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(new Color(248,187,208)); }
        });
        return btn;
    }

    private static Image redimensionnerImage(String path, int w, int h) {
        try {
            File f = new File(path);
            if (!f.exists()) return null;
            BufferedImage orig = ImageIO.read(f);
            BufferedImage dst  = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = dst.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(orig,0,0,w,h,null);
            g2.dispose();
            return dst;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}