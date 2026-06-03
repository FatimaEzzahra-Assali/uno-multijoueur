# UNO Multijoueur

## Présentation

UNO Multijoueur est une application Java permettant de jouer au célèbre jeu de cartes UNO en réseau.

Le projet a été réalisé dans le cadre du Projet de Synthèse de Licence Informatique et combine plusieurs domaines du développement logiciel :

* Programmation Orientée Objet (POO)
* Développement client/serveur
* Programmation réseau avec sockets TCP
* Interface graphique JavaFX
* Persistance des données avec JDBC et MySQL
* Tests unitaires

L'application permet à plusieurs joueurs de se connecter à un serveur central, de participer à une partie de UNO en temps réel et d'enregistrer les résultats dans une base de données.

---

## Fonctionnalités

### Gestion du jeu

* Création et gestion d'une partie de UNO
* Gestion des joueurs connectés
* Distribution automatique des cartes
* Gestion des tours de jeu
* Vérification des coups autorisés
* Gestion des pénalités
* Gestion du "UNO !"
* Détermination du gagnant

### Cartes implémentées

* Cartes simples
* Cartes +2
* Cartes Passe Ton Tour

L'architecture a été conçue pour permettre l'ajout facile d'autres cartes spéciales.

---

## Fonctionnalités réseau

Architecture client/serveur basée sur les sockets TCP.

### Côté serveur

* Gestion des connexions multiples
* Réception et traitement des commandes des joueurs
* Synchronisation de l'état de la partie
* Gestion des messages échangés

### Côté client

* Connexion au serveur
* Réception des mises à jour en temps réel
* Envoi des actions de jeu
* Gestion des interactions utilisateur

---

## Interface graphique

L'application dispose d'une interface graphique développée avec JavaFX et FXML.

### Écrans disponibles

* Connexion
* Accueil
* Partie
* Messagerie
* Fin de partie

### Fonctionnalités graphiques

* Affichage dynamique des cartes
* Sélection visuelle des cartes jouables
* Animation de distribution
* Animation de pioche
* Mise à jour en temps réel de l'état de la partie

---

## Système de messagerie

Une messagerie intégrée permet aux joueurs de communiquer pendant une partie.

### Fonctionnalités

* Messages publics
* Messages privés
* Liste des joueurs connectés
* Historique des conversations

---

## Base de données

Les données sont stockées dans une base MySQL via JDBC.

### Informations enregistrées

* Joueurs
* Parties
* Scores

### Technologies utilisées

* JDBC
* MySQL
* PreparedStatement
* DriverManager

---

## Tests

Des tests unitaires ont été réalisés pour valider :

* Les coups légaux
* Les coups illégaux
* Les pénalités
* Les cartes spéciales
* Les règles du UNO

Classes de test principales :

* TestLegaux
* TestPunitions
* TestDuUno
* TestCartePlusDeux
* TestPasseTonTour

---

## Technologies utilisées

* Java
* JavaFX
* FXML
* JDBC
* MySQL
* TCP Sockets
* Threads
* IntelliJ IDEA
* Git
* GitHub

---

## Exécution du projet

Le projet peut être utilisé de deux façons : en mode console ou via l'interface graphique JavaFX.

### Mode console

Pour lancer la version console du jeu, exécuter :

```java
main.Main
```

Cette version permet d'utiliser le jeu directement depuis le terminal.

---

### Mode graphique (JavaFX)

Pour utiliser l'interface graphique, plusieurs composants doivent être lancés dans l'ordre :

#### 1. Démarrer MySQL

La base de données `projet_uno` doit être accessible avec les paramètres configurés dans les classes JDBC.

#### 2. Lancer le serveur

Exécuter :

```java
server.app.AppServeurUno
```

Le serveur gère les connexions des joueurs, la logique réseau et l'enregistrement des données.

#### 3. Lancer le client graphique

Exécuter :

```java
view.Accueil
```

Une fenêtre de connexion s'ouvre alors.

#### 4. Se connecter

- Saisir un pseudo.
- Cliquer sur **Connexion**.
- Attendre que les autres joueurs rejoignent la partie.
- Le premier joueur connecté peut démarrer la partie.

---

### Remarque

Si MySQL n'est pas démarré, l'interface graphique peut tout de même s'ouvrir, mais certaines fonctionnalités liées à la persistance des données (joueurs, parties, scores) ne seront pas disponibles.


## Architecture du projet

```text
src/
├── model/          # Logique métier du jeu
├── network/        # Client réseau
├── server/         # Serveur multijoueur
├── jdbc/           # Accès aux données
├── view/           # Interface JavaFX
├── console/        # Outils console
└── test/           # Tests unitaires
```

## Captures d'écran

### Connexion

*(Ajouter une capture d'écran de la fenêtre de connexion)*

### Partie

*(Ajouter une capture d'écran d'une partie en cours)*

### Messagerie

*(Ajouter une capture d'écran de la messagerie)*

### Fin de partie

*(Ajouter une capture d'écran de l'écran de fin)*

---

## Améliorations possibles

* Ajout des cartes Joker
* Ajout des cartes +4
* Statistiques des joueurs
* Historique des parties
* Gestion de plusieurs parties simultanées
* Déploiement en ligne

---

## Configuration JavaFX

Le projet a été développé avec JavaFX.

Si l'erreur suivante apparaît :

```text
JavaFX runtime components are missing
```

ajouter les options VM suivantes dans IntelliJ IDEA :

```text
--module-path "CHEMIN_VERS_JAVAFX/lib" --add-modules javafx.controls,javafx.fxml
```

## Auteurs

Projet réalisé dans le cadre du Projet de Synthèse de Licence Informatique.

Équipe :

* Fatima Ezzahra Assali
* Buse Cosar
* Tessnim Khelifa

Encadrants :

* Yann Lanuel
* Ajdin Topalovic
