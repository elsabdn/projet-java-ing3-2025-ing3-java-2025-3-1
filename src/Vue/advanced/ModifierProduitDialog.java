package Vue.advanced;

import Modele.Produit;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Boîte de dialogue pour modifier un produit existant,
 * incluant la gestion de la promotion en gros,
 * avec des boutons stylés pastel.
 */
public class ModifierProduitDialog extends JDialog {
    private boolean confirme = false;

    // Champs du formulaire
    private JTextField champNom;
    private JTextField champPrix;
    private JTextField champStock;
    private JTextField champMarque;
    private JLabel etiquetteImage;
    private JTextArea champDescription;
    private String cheminImage;

    // Champs pour la promotion en gros
    private JCheckBox promoEnGrosCheck;
    private JTextField seuilGrosField;
    private JTextField prixGrosField;

    public ModifierProduitDialog(JFrame proprietaire, Produit p) {
        super(proprietaire, "Modifier un produit", true);
        setLayout(new BorderLayout(10, 10));
        setSize(450, 650);
        setLocationRelativeTo(proprietaire);

        // === Formulaire principal ===
        JPanel formulaire = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Nom
        formulaire.add(new JLabel("Nom :"), gbc);
        champNom = new JTextField(p.getNom());
        gbc.gridx = 1;
        formulaire.add(champNom, gbc);

        // Prix unitaire
        gbc.gridy++;
        gbc.gridx = 0;
        formulaire.add(new JLabel("Prix (€) :"), gbc);
        champPrix = new JTextField(String.valueOf(p.getPrix()));
        gbc.gridx = 1;
        formulaire.add(champPrix, gbc);

        // Stock
        gbc.gridy++;
        gbc.gridx = 0;
        formulaire.add(new JLabel("Quantité en stock :"), gbc);
        champStock = new JTextField(String.valueOf(p.getQuantite()));
        gbc.gridx = 1;
        formulaire.add(champStock, gbc);

        // Marque
        gbc.gridy++;
        gbc.gridx = 0;
        formulaire.add(new JLabel("Marque :"), gbc);
        champMarque = new JTextField(p.getMarque());
        gbc.gridx = 1;
        formulaire.add(champMarque, gbc);

        // Image
        gbc.gridy++;
        gbc.gridx = 0;
        formulaire.add(new JLabel("Image :"), gbc);
        JPanel panneauImage = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        etiquetteImage = new JLabel(
                p.getImageChemin() != null ? new File(p.getImageChemin()).getName() : "Aucune"
        );
        JButton btnImage = createStyledButton("Choisir…");
        btnImage.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(
                    new FileNameExtensionFilter("Images (jpg, png, jpeg)", "jpg", "png", "jpeg")
            );
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                cheminImage = f.getAbsolutePath();
                etiquetteImage.setText(f.getName());
            }
        });
        panneauImage.add(etiquetteImage);
        panneauImage.add(Box.createHorizontalStrut(5));
        panneauImage.add(btnImage);
        gbc.gridx = 1;
        formulaire.add(panneauImage, gbc);

        // Description
        gbc.gridy++;
        gbc.gridx = 0;
        formulaire.add(new JLabel("Description :"), gbc);
        champDescription = new JTextArea(p.getDescription() != null ? p.getDescription() : "", 4, 20);
        champDescription.setLineWrap(true);
        champDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(champDescription);
        gbc.gridx = 1;
        formulaire.add(scrollDesc, gbc);

        // Promotion en gros
        gbc.gridy++;
        gbc.gridx = 0;
        formulaire.add(new JLabel("Promo en gros ?"), gbc);
        promoEnGrosCheck = new JCheckBox();
        promoEnGrosCheck.setSelected(p.estPromoEnGros());
        gbc.gridx = 1;
        formulaire.add(promoEnGrosCheck, gbc);

        // Seuil de quantité
        gbc.gridy++;
        gbc.gridx = 0;
        formulaire.add(new JLabel("Seuil (quantité) :"), gbc);
        seuilGrosField = new JTextField(String.valueOf(p.getSeuilGros()));
        gbc.gridx = 1;
        formulaire.add(seuilGrosField, gbc);

        // Prix de gros
        gbc.gridy++;
        gbc.gridx = 0;
        formulaire.add(new JLabel("Prix de gros (€) :"), gbc);
        prixGrosField = new JTextField(String.valueOf(p.getPrixGros()));
        gbc.gridx = 1;
        formulaire.add(prixGrosField, gbc);

        add(formulaire, BorderLayout.CENTER);

        // === Boutons Valider / Annuler ===
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnValider = createStyledButton("Valider");
        JButton btnAnnuler = createStyledButton("Annuler");

        btnValider.addActionListener(e -> {
            try {
                // Validation des champs numériques
                Double.parseDouble(champPrix.getText().trim());
                Integer.parseInt(champStock.getText().trim());
                if (promoEnGrosCheck.isSelected()) {
                    Integer.parseInt(seuilGrosField.getText().trim());
                    Double.parseDouble(prixGrosField.getText().trim());
                }

                // Application des modifications
                p.setNom(champNom.getText().trim());
                p.setPrix(Double.parseDouble(champPrix.getText().trim()));
                p.setQuantite(Integer.parseInt(champStock.getText().trim()));
                p.setMarque(champMarque.getText().trim());
                if (cheminImage != null) p.setImageChemin(cheminImage);
                p.setDescription(champDescription.getText().trim());
                p.setPromoEnGros(promoEnGrosCheck.isSelected());
                p.setSeuilGros(Integer.parseInt(seuilGrosField.getText().trim()));
                p.setPrixGros(Double.parseDouble(prixGrosField.getText().trim()));

                confirme = true;
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Veuillez saisir des valeurs valides pour les champs numériques.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnAnnuler.addActionListener(e -> setVisible(false));
        actions.add(btnValider);
        actions.add(btnAnnuler);
        add(actions, BorderLayout.SOUTH);
    }

    // Méthode utilitaire pour créer des boutons stylés pastel
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(new Color(248, 187, 208));  // rose pastel
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(244, 143, 177));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(248, 187, 208));
            }
        });
        return btn;
    }

    // ===== Getters =====
    public boolean isConfirme()            { return confirme; }
    public String obtenirNomModifie()         { return champNom.getText().trim(); }
    public double obtenirPrixModifie()        { return Double.parseDouble(champPrix.getText().trim()); }
    public int obtenirStockModifie()       { return Integer.parseInt(champStock.getText().trim()); }
    public String obtenirMarqueModifiee()     { return champMarque.getText().trim(); }
    public String obtenirCheminImageModifie() { return cheminImage; }
    public String obtenirDescriptionModifie() { return champDescription.getText().trim(); }
    public boolean isPromoEnGrosModifie()  { return promoEnGrosCheck.isSelected(); }
    public int     getSeuilGrosModifie()   { return Integer.parseInt(seuilGrosField.getText().trim()); }
    public double  getPrixGrosModifie()    { return Double.parseDouble(prixGrosField.getText().trim()); }
}
