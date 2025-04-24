package Vue.advanced;

import Modele.Produit;
import Controller.ProduitController;
import Vue.advanced.PaiementPanel;
import DAO.CommandeDAO;
import Modele.Acheteur;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel d'affichage et gestion du panier d'achat d'un utilisateur.
 * Regroupe les produits ajoutés, permet leur suppression, et valide la commande.
 */
public class PanierPanel extends JPanel {
    private final MainFrame mainFrame; // Référence à la frame principale
    private final List<Produit> panier; // Liste des produits dans le panier

    public PanierPanel(MainFrame mainFrame, List<Produit> panier) {
        this.mainFrame = mainFrame;
        this.panier    = panier;

        setLayout(new BorderLayout());
        setOpaque(false);

        // ─── Bandeau haut: titre + bouton déconnexion ────────────────────────────────────────────────
        JButton btnDeconnexion = createStyledButton("🚪 Déconnexion");
        btnDeconnexion.setPreferredSize(new Dimension(140, 35));
        btnDeconnexion.addActionListener(e -> mainFrame.showPanel("accueil"));

        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        logoutWrapper.setOpaque(false);
        logoutWrapper.add(btnDeconnexion);

        Dimension eastSize = logoutWrapper.getPreferredSize();
        JPanel leftFiller = new JPanel();
        leftFiller.setOpaque(false);
        leftFiller.setPreferredSize(eastSize);

        JLabel lblTitre = new JLabel("Mon panier");
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitre.setForeground(new Color(92, 92, 92));
        lblTitre.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        header.add(leftFiller,    BorderLayout.WEST);
        header.add(lblTitre,      BorderLayout.CENTER);
        header.add(logoutWrapper, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ─── Zone centrale paddée: liste des produits ou message si vide ────────────────────────────────────────
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (panier.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            JLabel msg = new JLabel("🛒 Votre panier est vide !");
            msg.setFont(new Font("SansSerif", Font.BOLD, 28));
            msg.setForeground(new Color(120, 120, 120));
            emptyPanel.add(msg, new GridBagConstraints());
            centerWrapper.add(emptyPanel, BorderLayout.CENTER);

        } else {
            //Regrouper les produits par ID pour comptabiliser les quantités
            Map<Integer, Integer> quantites = new LinkedHashMap<>();
            Map<Integer, Produit> produitsUniques = new LinkedHashMap<>();
            for (Produit p : panier) {
                int id = p.getId();
                quantites.merge(id, 1, Integer::sum);
                produitsUniques.putIfAbsent(id, p);
            }

            JPanel listPanel = new JPanel();
            listPanel.setBackground(Color.WHITE);
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            for (Map.Entry<Integer, Produit> e : produitsUniques.entrySet()) {
                Produit p = e.getValue();
                int qty   = quantites.get(p.getId());
                listPanel.add(creerCarteProduit(p, qty));
                listPanel.add(Box.createVerticalStrut(10));
            }

            JScrollPane scroll = new JScrollPane(listPanel);
            scroll.setBorder(null);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            // hauteur fixe pour 3 lignes : 3×150 + 2×10 = 470
            scroll.setPreferredSize(new Dimension(0, 470));

            centerWrapper.add(scroll, BorderLayout.CENTER);
        }

        add(centerWrapper, BorderLayout.CENTER);

        // ─── Résumé & actions à droite ────────────────────────────────────
        JPanel resume = new JPanel();
        resume.setLayout(new BoxLayout(resume, BoxLayout.Y_AXIS));
        resume.setBackground(new Color(250, 250, 250));
        resume.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20, 20, 20, 20),
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
        ));
        Dimension tailleResume = new Dimension(320, 220);
        resume.setPreferredSize(tailleResume);
        resume.setMinimumSize(tailleResume);
        resume.setMaximumSize(tailleResume);

        int totalQty   = panier.size();
        double totalPx = panier.stream().mapToDouble(Produit::getPrix).sum();

        JLabel lblResume   = new JLabel("Résumé de la commande");
        JLabel lblArticles = new JLabel("Articles : " + totalQty);
        JLabel lblTotal    = new JLabel(String.format("Total : %.2f €", totalPx));
        for (JLabel l : new JLabel[]{lblResume, lblArticles, lblTotal}) {
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        lblResume.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotal.setBorder(new EmptyBorder(10, 0, 20, 0));

        JButton btnRetour  = createStyledButton("⬅ Retour aux produits");
        JButton btnValider = createStyledButton("Valider la commande");
        Dimension btnSize = new Dimension(300, 40);
        for (JButton b : new JButton[]{btnRetour, btnValider}) {
            b.setPreferredSize(btnSize);
            b.setMaximumSize(btnSize);
            b.setMinimumSize(btnSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        btnRetour.addActionListener(e -> mainFrame.showPanel("acheteur"));

        // Validation de commande
        btnValider.addActionListener(e -> {
            double totalPrix = panier.stream()
                    .mapToDouble(Produit::getPrix)
                    .sum();

            PaiementPanel paiement = new PaiementPanel(totalPrix);

            paiement.setCancelAction(evt -> mainFrame.showPanel("panier"));

            paiement.setConfirmPaymentAction(evt -> {
                int note = paiement.getNote();
                if (note < 1 || note > 10) {
                    JOptionPane.showMessageDialog(mainFrame, "Merci de saisir une note entre 1 et 10.");
                    return;
                }

                Acheteur acheteur = mainFrame.getAcheteurConnecte();
                if (acheteur == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Erreur : aucun utilisateur connecté.");
                    return;
                }

                CommandeDAO dao = new CommandeDAO();
                dao.enregistrerCommande(panier, note, acheteur);

                JOptionPane.showMessageDialog(mainFrame, "Commande enregistrée avec la note : " + note);
                mainFrame.showPanel("accueil");
            });

            mainFrame.addPanel(paiement, "paiement");
            mainFrame.showPanel("paiement");
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

    // Carte produit unique avec bouton Supprimer
    private JPanel creerCarteProduit(Produit produit, int quantite) {
        JPanel carte = new JPanel(new BorderLayout());
        carte.setOpaque(false);
        carte.setBackground(Color.WHITE);
        carte.setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(220,220,220)));
        carte.setPreferredSize(new Dimension(0,150));
        carte.setMaximumSize(new Dimension(Integer.MAX_VALUE,150));

        // Image produit
        if (produit.getImagePath() != null && !produit.getImagePath().isEmpty()) {
            Image img = redimensionnerImage(produit.getImagePath(), 100, 100);
            if (img != null) {
                JLabel imgLbl = new JLabel(new ImageIcon(img));
                imgLbl.setBorder(new EmptyBorder(10, 10, 0, 10));
                carte.add(imgLbl, BorderLayout.WEST);
            } else {
                JLabel imgLbl = new JLabel("Image indisponible");
                imgLbl.setPreferredSize(new Dimension(100, 100));
                imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
                imgLbl.setBorder(new EmptyBorder(10, 10, 0, 10));
                carte.add(imgLbl, BorderLayout.WEST);
            }
        }


        // Infos + quantité: nom, prix, stock, quantité
        JPanel infos = new JPanel();
        infos.setOpaque(false);
        infos.setLayout(new BoxLayout(infos, BoxLayout.Y_AXIS));
        infos.setBorder(new EmptyBorder(10,10,10,10));

        JLabel lblNom   = new JLabel(produit.getNom());
        JLabel lblPrix  = new JLabel(String.format("%.2f €", produit.getPrix()));
        JLabel lblStock = new JLabel("Stock : " + produit.getQuantite());
        JLabel lblQty   = new JLabel("Quantité : " + quantite);
        for (JLabel l : new JLabel[]{lblNom,lblPrix,lblStock,lblQty}) {
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        lblNom.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblPrix.setForeground(new Color(100,100,100));
        lblStock.setForeground(new Color(150,150,150));

        infos.add(lblNom);
        infos.add(Box.createVerticalStrut(5));
        infos.add(lblPrix);
        infos.add(Box.createVerticalStrut(5));
        infos.add(lblStock);
        infos.add(Box.createVerticalStrut(10));
        infos.add(lblQty);

        carte.add(infos, BorderLayout.CENTER);

        // Bouton Supprimer produit à droite
        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.setBackground(new Color(220, 50, 50));
        btnSupprimer.setFocusPainted(false);
        btnSupprimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSupprimer.setPreferredSize(new Dimension(150, 35));
        btnSupprimer.addActionListener(e -> {
            // supprime toutes les occurrences du produit
            panier.removeIf(p -> p.getId() == produit.getId());
            // rafraîchit le panneau
            mainFrame.addPanel(new PanierPanel(mainFrame, panier), "panier");
            mainFrame.showPanel("panier");
        });

        JPanel supprWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        supprWrapper.setOpaque(false);
        supprWrapper.add(btnSupprimer);
        carte.add(supprWrapper, BorderLayout.EAST);

        return carte;
    }

    // Style graphique des boutons
    private JButton createStyledButton(String texte) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(new Color(248,187,208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10,20,10,20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(244,143,177));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(248,187,208));
            }
        });
        return btn;
    }

    // Redimensionne une image à la taille souhaitée
    private static Image redimensionnerImage(String path, int w, int h) {
        try {
            File imageFile = new File(path);
            if (!imageFile.exists()) {
                System.err.println("Image non trouvée : " + path);
                return null;
            }

            BufferedImage orig = ImageIO.read(imageFile);
            BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(orig, 0, 0, w, h, null);
            g2.dispose();
            return resized;

        } catch (IOException ex) {
            System.err.println("Erreur chargement image : " + path);
            ex.printStackTrace();
            return null;
        }
    }

}