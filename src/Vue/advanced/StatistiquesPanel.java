package Vue.advanced;

import DAO.StatistiquesDAO;
import Modele.Vendeur;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;

//import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import DAO.DatabaseManager;

public class StatistiquesPanel extends JPanel {

    private final Vendeur vendeur;
    private final StatistiquesDAO statistiquesDAO;
    private JComboBox<String> produitComboBox;
    private JTextField clientIdTextField; // Champ de texte pour l'ID du client
    private JPanel detailsPanel; // Panel pour afficher les informations détaillées

    public StatistiquesPanel(Vendeur vendeur) throws SQLException {
        this.vendeur = vendeur;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240)); // Couleur de fond douce

        // Connexion à la base de données
        DatabaseManager dbManager = DatabaseManager.getInstance();
        Connection connection = dbManager.getConnection();
        this.statistiquesDAO = new StatistiquesDAO(connection);

        // Panneau contenant tous les graphiques et informations
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Espacement

        // Graphique ventes par mois
        Map<String, Integer> ventesParMois = statistiquesDAO.getVentesParMois();
        if (!ventesParMois.isEmpty()) {
            JFreeChart pieChart = createVentesParMoisChart(ventesParMois);
            ChartPanel chartPanel = new ChartPanel(pieChart);
            chartPanel.setBackground(Color.WHITE);
            chartPanel.setPreferredSize(new Dimension(800, 600));
            contentPanel.add(chartPanel);
        } else {
            JLabel noDataLabel = new JLabel("Aucune donnée disponible pour les ventes par mois.", SwingConstants.CENTER);
            contentPanel.add(noDataLabel);
        }

        // Graphique top produits vendus
        Map<String, Integer> topProduits = statistiquesDAO.getTopProduits();
        if (!topProduits.isEmpty()) {
            JFreeChart barChart = createTopProduitsChart(topProduits);
            ChartPanel barChartPanel = new ChartPanel(barChart);
            barChartPanel.setPreferredSize(new Dimension(800, 600));
            contentPanel.add(barChartPanel);
        } else {
            JLabel noDataLabel = new JLabel("Aucun produit trouvé dans le top produits.", SwingConstants.CENTER);
            contentPanel.add(noDataLabel);
        }

        Map<String,Integer> offres = statistiquesDAO.getOffresBienAccueillies();
        if (!offres.isEmpty()) {
            JFreeChart offresChart = createOffresBienAccueilliesChart(offres);
            ChartPanel cp = new ChartPanel(offresChart);
            cp.setPreferredSize(new Dimension(800, 400));
            cp.setBackground(Color.WHITE);
            contentPanel.add(cp);
        }else {
            // Quand il n’y a aucune offre bien accueillie à afficher
            JLabel noOffresLabel = new JLabel("Aucune offre de gros bien accueillie.", SwingConstants.CENTER);
            noOffresLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noOffresLabel.setForeground(new Color(120, 120, 120));
            noOffresLabel.setPreferredSize(new Dimension(800, 400));
            contentPanel.add(noOffresLabel);
        }

        // Panneau avec le JComboBox pour sélectionner le produit et le JTextField pour saisir l'ID du client
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(Color.white);

        // JComboBox pour sélectionner le produit
        produitComboBox = new JComboBox<>(statistiquesDAO.getListeProduits().values().toArray(new String[0]));
        produitComboBox.setPreferredSize(new Dimension(200, 30));
        produitComboBox.setFont(new Font("Arial", Font.PLAIN, 16));

        // JTextField pour saisir l'ID du client
        clientIdTextField = new JTextField(10);  // Champ de texte pour saisir l'ID
        clientIdTextField.setPreferredSize(new Dimension(200, 30));
        clientIdTextField.setFont(new Font("Arial", Font.PLAIN, 16));

        selectionPanel.add(new JLabel("Sélectionnez un produit:"));
        selectionPanel.add(produitComboBox);
        selectionPanel.add(Box.createHorizontalStrut(20)); // Espace entre les composants
        selectionPanel.add(new JLabel("Entrez l'ID du client:"));
        selectionPanel.add(clientIdTextField);

        contentPanel.add(selectionPanel);

        // Bouton pour afficher plus d'infos
        JButton detailsButton = createStyledButton("Afficher plus d'infos");
        detailsButton.setPreferredSize(new Dimension(200, 40));
        detailsButton.addActionListener(e -> showMoreInfo());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(detailsButton);
        contentPanel.add(buttonPanel);

        // Panneau de détails caché initialement
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.white);
        detailsPanel.setVisible(false);  // Caché au départ

        contentPanel.add(detailsPanel); // Ajouter le panneau des détails au panneau principal

        // Panneau déroulant
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JFreeChart createVentesParMoisChart(Map<String, Integer> ventesParMois) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Integer> entry : ventesParMois.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "Ventes par Mois",
                dataset,
                true,
                true,
                false
        );

        // Personnalisation du graphique en camembert
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(245,245,245));
        plot.setSectionPaint(0, new Color(248, 188, 208)); // Couleur rose pour la première section
        plot.setSectionPaint(1, new Color(252, 242, 246, 255)); // Couleur plus claire pour la seconde section

        return chart;
    }

    private JFreeChart createTopProduitsChart(Map<String, Integer> topProduits) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : topProduits.entrySet()) {
            dataset.addValue(entry.getValue(), "Produits", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Top 3 Produits Vendus",
                "Produit",
                "Ventes",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Personnalisation du graphique en barres
        barChart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = (CategoryPlot) barChart.getPlot();
        plot.setBackgroundPaint(new Color(245,245,245));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(248, 188, 208)); // Couleur rose pour les barres

        return barChart;
    }

    /**
     * Crée un bar chart pour les offres en gros les plus bien accueillies.
     */
    private JFreeChart createOffresBienAccueilliesChart(Map<String, Integer> offres) {
        // On construit le dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String,Integer> e : offres.entrySet()) {
            dataset.addValue(e.getValue(), "Lots vendus", e.getKey());
        }

        // On crée le bar chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Offres en Gros les Plus Accueillies",  // titre
                "Produit",                              // axe X
                "Lots vendus",                          // axe Y
                dataset,
                PlotOrientation.VERTICAL,
                false,  // pas de légende
                true,
                false
        );

        // Fond blanc autour du graphique
        chart.setBackgroundPaint(Color.WHITE);

        // Fond gris clair à l'intérieur de la zone du plot
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));

        // Barres en rose pastel
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(248, 187, 208));

        return chart;
    }



    private void showMoreInfo() {
        // Récupérer les sélections faites dans les composants
        String selectedProduit = (String) produitComboBox.getSelectedItem();
        String clientIdText = clientIdTextField.getText().trim(); // Récupère l'ID client saisi

        if (clientIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un ID client.");
            return;
        }

        try {
            int clientId = Integer.parseInt(clientIdText);

            // Révéler le panneau de détails
            detailsPanel.setVisible(true);

            // Réinitialiser le contenu des détails
            detailsPanel.removeAll();

            // Afficher les informations du produit sélectionné
            int produitId = statistiquesDAO.getProduitIdByName(selectedProduit);
            int montantVentesProduit = statistiquesDAO.getMontantVenteProduit(produitId);
            JLabel ventesProduitLabel = new JLabel("Montant total des ventes pour le produit \"" + selectedProduit + "\": " + montantVentesProduit + " €");
            ventesProduitLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            ventesProduitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            detailsPanel.add(ventesProduitLabel);
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

// Afficher les informations du client sélectionné
            int nombreCommandesClient = statistiquesDAO.getNombreCommandesClient3Mois(clientId);
            JLabel commandesClientLabel = new JLabel("Nombre de commandes du client sur les 3 derniers mois: " + nombreCommandesClient);
            commandesClientLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            commandesClientLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            detailsPanel.add(commandesClientLabel);

            // Repackager le panneau pour réafficher le contenu
            detailsPanel.revalidate();
            detailsPanel.repaint();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "L'ID client doit être un nombre entier.");
        }
    }

    /**
     * Crée un JButton stylé rose pastel avec effet hover.
     */
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(new Color(248, 187, 208)); // rose pastel
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(244, 143, 177));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(248, 187, 208));
            }
        });
        return btn;
    }

}