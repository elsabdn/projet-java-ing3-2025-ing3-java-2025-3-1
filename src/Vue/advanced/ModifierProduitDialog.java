package Vue.advanced;

import Modele.Produit;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Boîte de dialogue pour modifier un produit, incluant sa description et les promotions en gros.
 */
public class ModifierProduitDialog extends JDialog {
    private boolean confirme = false;

    private JTextField champNom;
    private JTextField champPrix;
    private JTextField champStock;
    private JTextField champMarque;
    private JLabel etiquetteImage;
    private JTextArea champDescription;
    private String cheminImage;

    // Champs ajoutés
    private JCheckBox promoEnGrosCheckBox;
    private JTextField seuilGrosField;
    private JTextField prixGrosField;

    public ModifierProduitDialog(JFrame proprietaire, Produit p) {
        super(proprietaire, "Modifier un produit", true);
        setLayout(new BorderLayout(10,10));
        setSize(450, 620);
        setLocationRelativeTo(proprietaire);

        JPanel panneauForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        panneauForm.add(new JLabel("Nom :"), gbc);
        champNom = new JTextField(p.getNom());
        gbc.gridx = 1; panneauForm.add(champNom, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Prix (€) :"), gbc);
        champPrix = new JTextField(String.valueOf(p.getPrix()));
        gbc.gridx = 1; panneauForm.add(champPrix, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Stock :"), gbc);
        champStock = new JTextField(String.valueOf(p.getQuantite()));
        gbc.gridx = 1; panneauForm.add(champStock, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Marque :"), gbc);
        champMarque = new JTextField(p.getMarque());
        gbc.gridx = 1; panneauForm.add(champMarque, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Image :"), gbc);
        JPanel panneauImage = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        etiquetteImage = new JLabel(
                p.getImagePath() != null ? new File(p.getImagePath()).getName() : "Aucune"
        );
        JButton choisirImage = new JButton("Choisir...");
        choisirImage.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Sélectionner une image");
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(
                    new FileNameExtensionFilter("Images (jpg, png, jpeg)", "jpg","png","jpeg")
            );
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                cheminImage = fc.getSelectedFile().getAbsolutePath();
                etiquetteImage.setText(new File(cheminImage).getName());
            }
        });
        panneauImage.add(etiquetteImage);
        panneauImage.add(Box.createHorizontalStrut(5));
        panneauImage.add(choisirImage);
        gbc.gridx = 1; panneauForm.add(panneauImage, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Description :"), gbc);
        champDescription = new JTextArea(p.getDescription() != null ? p.getDescription() : "");
        champDescription.setLineWrap(true);
        champDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(champDescription);
        scrollDesc.setPreferredSize(new Dimension(250, 100));
        gbc.gridx = 1; panneauForm.add(scrollDesc, gbc);

        // Promo en gros
        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Promo en gros ?"), gbc);
        gbc.gridx = 1;
        promoEnGrosCheckBox = new JCheckBox();
        promoEnGrosCheckBox.setSelected(p.isPromoEnGros());
        panneauForm.add(promoEnGrosCheckBox, gbc);

        // Seuil
        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Seuil (quantité) :"), gbc);
        gbc.gridx = 1;
        seuilGrosField = new JTextField(String.valueOf(p.getSeuilGros()));
        panneauForm.add(seuilGrosField, gbc);

        // Prix de gros
        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Prix de gros (€) :"), gbc);
        gbc.gridx = 1;
        prixGrosField = new JTextField(String.valueOf(p.getPrixGros()));
        panneauForm.add(prixGrosField, gbc);

        add(panneauForm, BorderLayout.CENTER);

        // Boutons
        JPanel panneauActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnValider = new JButton("Valider");
        JButton btnAnnuler = new JButton("Annuler");

        btnValider.addActionListener(e -> {
            try {
                // Validation champs promo
                p.setPromoEnGros(promoEnGrosCheckBox.isSelected());
                p.setSeuilGros(Integer.parseInt(seuilGrosField.getText().trim()));
                p.setPrixGros(Double.parseDouble(prixGrosField.getText().trim()));

                confirme = true;
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Seuil ou prix de gros invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAnnuler.addActionListener(e -> setVisible(false));

        panneauActions.add(btnValider);
        panneauActions.add(btnAnnuler);
        add(panneauActions, BorderLayout.SOUTH);
    }

    public boolean isConfirme() {
        return confirme;
    }

    public String getNomModifie() {
        return champNom.getText().trim();
    }

    public double getPrixModifie() {
        return Double.parseDouble(champPrix.getText().trim());
    }

    public int getStockModifie() {
        return Integer.parseInt(champStock.getText().trim());
    }

    public String getMarqueModifiee() {
        return champMarque.getText().trim();
    }

    public String getCheminImageModifie() {
        return cheminImage;
    }

    public String getDescriptionModifie() {
        return champDescription.getText().trim();
    }
}
