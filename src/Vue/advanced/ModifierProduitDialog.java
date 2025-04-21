package Vue.advanced;

import Modele.Produit;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Boîte de dialogue pour modifier un produit, incluant sa description.
 */
public class ModifierProduitDialog extends JDialog {
    private boolean confirme = false;

    private JTextField champNom;
    private JTextField champPrix;
    private JTextField champStock;
    private JTextField champMarque;
    private JLabel     etiquetteImage;
    private JTextArea  champDescription;
    private String     cheminImage;

    public ModifierProduitDialog(JFrame proprietaire, Produit p) {
        super(proprietaire, "Modifier un produit", true);
        setLayout(new BorderLayout(10,10));
        setSize(450, 480);
        setLocationRelativeTo(proprietaire);

        // ─── Corps du formulaire ──────────────────────────────────────
        JPanel panneauForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // Nom
        panneauForm.add(new JLabel("Nom :"), gbc);
        champNom = new JTextField(p.getNom());
        gbc.gridx = 1; panneauForm.add(champNom, gbc);

        // Prix
        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Prix (€) :"), gbc);
        champPrix = new JTextField(String.valueOf(p.getPrix()));
        gbc.gridx = 1; panneauForm.add(champPrix, gbc);

        // Stock
        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Stock :"), gbc);
        champStock = new JTextField(String.valueOf(p.getQuantite()));
        gbc.gridx = 1; panneauForm.add(champStock, gbc);

        // Marque
        gbc.gridx = 0; gbc.gridy++;
        panneauForm.add(new JLabel("Marque :"), gbc);
        champMarque = new JTextField(p.getMarque());
        gbc.gridx = 1; panneauForm.add(champMarque, gbc);

        // Image
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

        add(panneauForm, BorderLayout.CENTER);

        // ─── Boutons Valider / Annuler ────────────────────────────────
        JPanel panneauActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnValider = new JButton("Valider");
        JButton btnAnnuler = new JButton("Annuler");

        btnValider.addActionListener(e -> {
            confirme = true;
            setVisible(false);
        });
        btnAnnuler.addActionListener(e -> setVisible(false));

        panneauActions.add(btnValider);
        panneauActions.add(btnAnnuler);
        add(panneauActions, BorderLayout.SOUTH);
    }

    /** Retourne vrai si l’utilisateur a validé le formulaire */
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

    /** Chemin de la nouvelle image, ou null si non modifié */
    public String getCheminImageModifie() {
        return cheminImage;
    }

    public String getDescriptionModifie() {
        return champDescription.getText().trim();
    }
}
