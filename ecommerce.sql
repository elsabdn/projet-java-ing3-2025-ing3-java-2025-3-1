-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : mer. 23 avr. 2025 à 11:42
-- Version du serveur : 9.1.0
-- Version de PHP : 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `ecommerce`
--

-- --------------------------------------------------------

--
-- Structure de la table `commande`
--

DROP TABLE IF EXISTS `commande`;
CREATE TABLE IF NOT EXISTS `commande` (
  `id` int NOT NULL AUTO_INCREMENT,
  `utilisateur_id` int DEFAULT NULL,
  `date_commande` datetime DEFAULT CURRENT_TIMESTAMP,
  `montant_total` decimal(10,2) NOT NULL,
  `note` text,
  `statut` enum('en_cours','terminee','annulee') DEFAULT 'en_cours',
  PRIMARY KEY (`id`),
  KEY `utilisateur_id` (`utilisateur_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `commande`
--

INSERT INTO `commande` (`id`, `utilisateur_id`, `date_commande`, `montant_total`, `note`, `statut`) VALUES
(1, 1, '2025-04-17 11:43:20', 46.00, NULL, 'en_cours'),
(2, 4, '2025-04-22 11:27:28', 750.00, '9', 'terminee'),
(3, 4, '2025-04-22 12:12:48', 959.00, '10', 'terminee');

-- --------------------------------------------------------

--
-- Structure de la table `commande_item`
--

DROP TABLE IF EXISTS `commande_item`;
CREATE TABLE IF NOT EXISTS `commande_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `commande_id` int DEFAULT NULL,
  `produit_id` int DEFAULT NULL,
  `quantite` int NOT NULL,
  `prix` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `commande_id` (`commande_id`),
  KEY `produit_id` (`produit_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `commande_item`
--

INSERT INTO `commande_item` (`id`, `commande_id`, `produit_id`, `quantite`, `prix`) VALUES
(1, 1, 1, 2, 10.50),
(2, 1, 2, 1, 25.00),
(3, 3, 5, 2, 100.00),
(4, 3, 7, 1, 9.00),
(5, 3, 9, 1, 750.00);

-- --------------------------------------------------------

--
-- Structure de la table `panier`
--

DROP TABLE IF EXISTS `panier`;
CREATE TABLE IF NOT EXISTS `panier` (
  `id` int NOT NULL AUTO_INCREMENT,
  `utilisateur_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `utilisateur_id` (`utilisateur_id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `panier`
--

INSERT INTO `panier` (`id`, `utilisateur_id`) VALUES
(1, 1),
(2, 3);

-- --------------------------------------------------------

--
-- Structure de la table `panier_item`
--

DROP TABLE IF EXISTS `panier_item`;
CREATE TABLE IF NOT EXISTS `panier_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `panier_id` int DEFAULT NULL,
  `produit_id` int DEFAULT NULL,
  `quantite` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `panier_id` (`panier_id`),
  KEY `produit_id` (`produit_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `panier_item`
--

INSERT INTO `panier_item` (`id`, `panier_id`, `produit_id`, `quantite`) VALUES
(1, 1, 1, 7),
(2, 1, 2, 1),
(3, 1, 4, 1);

-- --------------------------------------------------------

--
-- Structure de la table `produit`
--

DROP TABLE IF EXISTS `produit`;
CREATE TABLE IF NOT EXISTS `produit` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) NOT NULL,
  `prix` decimal(10,2) NOT NULL,
  `quantite` int NOT NULL,
  `vendeur_id` int DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `marque` varchar(100) DEFAULT NULL,
  `description` text,
  `promoEnGros` tinyint(1) DEFAULT '0',
  `seuilGros` int DEFAULT '0',
  `prixGros` double DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `vendeur_id` (`vendeur_id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `produit`
--

INSERT INTO `produit` (`id`, `nom`, `prix`, `quantite`, `vendeur_id`, `image_path`, `marque`, `description`, `promoEnGros`, `seuilGros`, `prixGros`) VALUES
(9, 'Talons', 750.00, 30, 5, 'C:\\Users\\elsab\\OneDrive\\Bureau\\ING3\\POO Java\\photos_produitsShop\\louboutin.jpg', 'Louboutin', 'Entièrement confectionné en cuir de veau verni noir, il affiche une tige sculptée qui étire ses lignes au décolleté audacieux emblématique. Se hissant avec aisance sur un fin talon de 120 mm, il accompagne chaque pas, soulignant le savoir-faire de la Maison Christian Louboutin.', 0, 0, 0),
(5, 'Samba', 100.00, 200, 5, 'C:\\Users\\elsab\\OneDrive\\Bureau\\ING3\\POO Java\\photos_produitsShop\\Capture d\'écran 2025-04-20 111050.png', 'Adidas', 'Quand les plus grands titres sont en jeu, on peut compter sur Lionel Messi pour être performant. Représente la passion de l\'Argentine sur le terrain et en dehors avec cette chaussure de football adidas Samba junior confortable. Ornée de détails du joueur, cette chaussure classique à 3 bandes présente une tige en cuir signature et une semelle extérieure en caoutchouc adhérent.', 0, 0, 0),
(7, 'Café', 9.00, 300, 5, 'C:\\Users\\elsab\\Downloads\\café.png', 'Carte noir', 'Prêt à faire voyager vos papilles ? Éthiopie, Honduras, Colombie... On a exploré le monde et on en a rapporté les meilleurs grains. Chacun a dû faire ses preuves pour entrer dans la composition de nos cafés : dimensions, origine, profil aromatique… Chez nous, on ne plaisante pas avec le goût !', 0, 0, 0),
(8, 'Rouge à lèvres', 50.00, 540, 5, 'C:\\Users\\elsab\\OneDrive\\Bureau\\ING3\\POO Java\\photos_produitsShop\\ral_dior.jpg', 'Dior', 'Un rouge à lèvres toujours plus couture qui procure longue tenue et 24 h* de confort, le tout dans une formule clean** et infusée de soin floral hydratant.', 0, 0, 0),
(10, 'mascara', 30.00, 220, 5, 'C:\\Users\\elsab\\OneDrive\\Bureau\\ING3\\POO Java\\photos_produitsShop\\mascara.jpg', 'Dior', 'Ce mascara habille le regard d\'un volume cil à cil parfaitement défini. Infusée en extrait de fleur de bleuet, sa formule est facile à travailler et ne transige pas sur la performance.', 0, 0, 0),
(11, 'Parfum', 150.00, 50, 5, 'C:\\Users\\elsab\\OneDrive\\Bureau\\ING3\\POO Java\\photos_produitsShop\\Valentino.jpg', 'Valentino', 'Inspirés par la ville de Rome où le passé et le présent cohabitent harmonieusement, les parfums BORN IN ROMA parlent de l\'expression de soi : une célébration des personnes vivant leur vie librement et revendiquant pleinement leur héritage.', 0, 0, 0),
(12, 'CACA', 50.00, 500, 5, 'C:\\Users\\elsab\\OneDrive\\Documents\\IMG_20200626_0001.jpg', 'CACALAND', 'un bon gros caca', 1, 10, 4);

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
CREATE TABLE IF NOT EXISTS `utilisateur` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(191) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `role` enum('acheteur','vendeur') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id`, `email`, `mot_de_passe`, `role`) VALUES
(1, 'acheteur@example.com', 'password123', 'acheteur'),
(2, 'vendeur@example.com', 'password456', 'vendeur'),
(3, 'elsa.bodenan@edu.ece.fr', 'elsa', 'acheteur'),
(4, 'a@com', 'a', 'acheteur'),
(5, 'b@com', 'b', 'vendeur'),
(6, 'test', 't', 'acheteur'),
(7, 't2', 't', 'vendeur');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
