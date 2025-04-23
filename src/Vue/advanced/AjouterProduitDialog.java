package Vue.advanced;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Boîte de dialogue unique pour saisir toutes les infos d’un nouveau produit,
 * avec des boutons stylés en rose.
 */
public class AjouterProduitDialog extends JDialog {
    private boolean confirme = false;

    private JTextField  nomField, marqueField, prixField, qteField;
    private JTextArea   descArea;
    private JLabel      imgLabel;
    private String      cheminImage;

    public AjouterProduitDialog(JFrame parent) {
        super(parent, "Ajouter un produit", true);
        setLayout(new BorderLayout(10,10));
        setSize(400, 500);
        setLocationRelativeTo(parent);

        // ===== Formulaire =====
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0; gbc.gridy = 0;

        // Nom
        form.add(new JLabel("Nom :"), gbc);
        nomField = new JTextField();
        gbc.gridx = 1; form.add(nomField, gbc);

        // Marque
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Marque :"), gbc);
        marqueField = new JTextField();
        gbc.gridx = 1; form.add(marqueField, gbc);

        // Prix
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Prix (€) :"), gbc);
        prixField = new JTextField();
        gbc.gridx = 1; form.add(prixField, gbc);

        // Quantité
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Quantité :"), gbc);
        qteField = new JTextField();
        gbc.gridx = 1; form.add(qteField, gbc);

        // Description
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Description :"), gbc);
        descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(descArea);
        gbc.gridx = 1; form.add(sp, gbc);

        // Sélecteur d’image
        gbc.gridy++; gbc.gridx = 0;
        form.add(new JLabel("Image :"), gbc);
        JPanel imgPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        imgLabel = new JLabel("Aucune sélection");
        JButton choose = createStyledButton("Parcourir…");
        choose.setFont(choose.getFont().deriveFont(Font.PLAIN, 12f));
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

        add(form, BorderLayout.CENTER);

        // ===== Boutons Valider / Annuler =====
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton ok     = createStyledButton("Ajouter");
        JButton cancel = createStyledButton("Annuler");

        ok.addActionListener(e -> {
            // On peut ajouter ici une validation plus poussée si besoin
            try {
                Double.parseDouble(prixField.getText().trim());
                Integer.parseInt(qteField.getText().trim());
                confirme = true;
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Prix ou quantité invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(e -> setVisible(false));

        actions.add(ok);
        actions.add(cancel);
        add(actions, BorderLayout.SOUTH);
    }

    /** Style pastel rose pour les boutons */
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

    // ===== Getters =====
    public boolean isConfirme()      { return confirme; }
    public String  getNom()          { return nomField.getText().trim(); }
    public String  getMarque()       { return marqueField.getText().trim(); }
    public double  getPrix()         { return Double.parseDouble(prixField.getText().trim()); }
    public int     getQuantite()     { return Integer.parseInt(qteField.getText().trim()); }
    public String  getDescription()  { return descArea.getText().trim(); }
    public String  getCheminImage()  { return cheminImage; }
}
