package Vue.advanced;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * AjouterProduitDialog : boîte de dialogue permettant à un vendeur d'ajouter un nouveau produit.
 * Ce formulaire permet de saisir le nom, la marque, le prix, la quantité, la description et l’image.
 */
public class AjouterProduitDialog extends JDialog {
    private boolean confirme = false; // Vrai si l’utilisateur a cliqué sur "Ajouter"

    private JTextField  nomField, marqueField, prixField, qteField;
    private JTextArea   descArea;
    private JLabel      imgLabel;
    private String      cheminImage;// Chemin de l’image sélectionnée
    private JCheckBox promoEnGrosCheck;
    private JTextField seuilGrosField, prixGrosField;
    /**
     * Constructeur : initialise et affiche tous les composants graphiques du formulaire.
     */
    public AjouterProduitDialog(JFrame parent) {
        super(parent, "Ajouter un produit", true);
        setLayout(new BorderLayout(10,10));
        setSize(400, 500);
        setLocationRelativeTo(parent); // Centre la fenêtre

        // ===== Formulaire =====
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0; gbc.gridy = 0;

        // Nom du produit
        form.add(new JLabel("Nom :"), gbc);
        nomField = new JTextField();
        gbc.gridx = 1; form.add(nomField, gbc);

        // Marque du produit
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Marque :"), gbc);
        marqueField = new JTextField();
        gbc.gridx = 1; form.add(marqueField, gbc);

        // Prix du produit
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Prix (€) :"), gbc);
        prixField = new JTextField();
        gbc.gridx = 1; form.add(prixField, gbc);

        // Quantité en stock
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Quantité :"), gbc);
        qteField = new JTextField();
        gbc.gridx = 1; form.add(qteField, gbc);

        // Description du produit
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Description :"), gbc);
        descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(descArea);
        gbc.gridx = 1; form.add(sp, gbc);

        // Sélecteur d’image avec label
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Image :"), gbc);
        JPanel imgPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        imgLabel = new JLabel("Aucune sélection");
        JButton choose = createStyledButton("Parcourir…");
        choose.setFont(choose.getFont().deriveFont(Font.PLAIN, 12f));

        // Action pour ouvrir un sélecteur de fichier image
        choose.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg","png","jpeg"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                cheminImage = f.getAbsolutePath();
                imgLabel.setText(f.getName());
            }
        });
        imgPane.add(imgLabel);
        imgPane.add(Box.createHorizontalStrut(5));
        imgPane.add(choose);
        gbc.gridx = 1; form.add(imgPane, gbc);

        // Promo en gros
        gbc.gridy++; gbc.gridx=0; form.add(new JLabel("Promo en gros ?"),gbc);
        promoEnGrosCheck = new JCheckBox(); gbc.gridx=1; form.add(promoEnGrosCheck,gbc);
        // Seuil gros
        gbc.gridy++; gbc.gridx=0; form.add(new JLabel("Seuil (qté) :"),gbc);
        seuilGrosField = new JTextField(); gbc.gridx=1; form.add(seuilGrosField,gbc);
        // Prix gros
        gbc.gridy++; gbc.gridx=0; form.add(new JLabel("Prix de gros (€) :"),gbc);
        prixGrosField = new JTextField(); gbc.gridx=1; form.add(prixGrosField,gbc);


        add(form, BorderLayout.CENTER);

        // ===== Boutons Valider / Annuler =====
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton ok     = createStyledButton("Ajouter");
        JButton cancel = createStyledButton("Annuler");

        // Action : validation des champs prix et quantité
        ok.addActionListener(e -> {
            // On peut ajouter ici une validation plus poussée si besoin
            try {
                Double.parseDouble(prixField.getText().trim());
                Integer.parseInt(qteField.getText().trim());
                confirme = true;
                setVisible(false); // Ferme la boîte de dialogue
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Prix ou quantité invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Action : bouton Annuler = ferme simplement la boîte
        cancel.addActionListener(e -> setVisible(false));

        actions.add(ok);
        actions.add(cancel);
        add(actions, BorderLayout.SOUTH);
    }

    /**
     * Crée un bouton avec style rose pastel + effet hover
     */
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(new Color(248, 187, 208));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(244, 143, 177));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(248, 187, 208));
            }
        });
        return btn;
    }

    // ===== Getters publics pour récupérer les infos saisies ===
    public boolean estConfirme()      { return confirme; }
    public String getNom()          { return nomField.getText().trim(); }
    public String getMarque()       { return marqueField.getText().trim(); }
    public double getPrix()         { return Double.parseDouble(prixField.getText().trim()); }
    public int getQuantite()     { return Integer.parseInt(qteField.getText().trim()); }
    public String getDescription()  { return descArea.getText().trim(); }
    public String getCheminImage()  { return cheminImage; }
    public boolean estPromoEnGros() { return promoEnGrosCheck.isSelected(); }
    public int getSeuilGros() { return Integer.parseInt(seuilGrosField.getText().trim()); }
    public double getPrixGros() { return Double.parseDouble(prixGrosField.getText().trim()); }
}
