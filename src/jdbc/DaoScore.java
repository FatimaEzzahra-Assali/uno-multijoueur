package jdbc;

import jdbc.metier.JoueurBDD;

import java.sql.*;

public class DaoScore {

    private static final String URL = "jdbc:mysql://localhost:3306/projet_uno";
    private static final String USER = "user_uno";
    private static final String PASSWORD = "uno123";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Méthode pour appeler avec le pseudo
    public static void enregistrerScore(String pseudo, int idPartie, int score) {
        JoueurBDD joueur = DaoJoueur.getOrCreateJoueur(pseudo);
        enregistrerScore(joueur.getId(), idPartie, score);
    }

    // Requête SQL réelle
    public static void enregistrerScore(int idJoueur, int idPartie, int score) {
        String sql = "INSERT INTO Score(id_joueur, id_partie, score) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idJoueur);
            ps.setInt(2, idPartie);
            ps.setInt(3, score);
            ps.executeUpdate();

            System.out.println("Score enregistré : joueur=" + idJoueur + ", partie=" + idPartie + ", score=" + score);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void afficherTousLesScores() {
        String sql = """
        SELECT j.pseudo, p.date_partie, s.score
        FROM Score s
        JOIN Joueur j ON s.id_joueur = j.id_joueur
        JOIN Partie p ON s.id_partie = p.id_partie
        ORDER BY p.date_partie DESC
    """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== Tous les scores enregistrés ===");
            while (rs.next()) {
                String pseudo = rs.getString("pseudo");
                String date = rs.getString("date_partie");
                int score = rs.getInt("score");

                System.out.println(pseudo + " | " + date + " | " + score + " points");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void afficherClassementGlobal() {
        String sql = """
        SELECT j.pseudo, SUM(s.score) AS total_score
        FROM Score s
        JOIN Joueur j ON s.id_joueur = j.id_joueur
        GROUP BY j.pseudo
        ORDER BY total_score DESC
    """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("=== Classement global ===");
            while (rs.next()) {
                System.out.println(rs.getString("pseudo") + " : " + rs.getInt("total_score") + " points");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void afficherTop5() {
        String sql = """
        SELECT j.pseudo, SUM(s.score) AS total_score
        FROM Score s
        JOIN Joueur j ON s.id_joueur = j.id_joueur
        GROUP BY j.pseudo
        ORDER BY total_score DESC
        LIMIT 5
    """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== TOP 5 des joueurs ===");
            int rank = 1;
            while (rs.next()) {
                String pseudo = rs.getString("pseudo");
                int score = rs.getInt("total_score");
                System.out.println(rank + ". " + pseudo + " - " + score + " points");
                rank++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getScoreTotalDuJoueur(int idJoueur) {
        String sql = "SELECT SUM(score) FROM Score WHERE id_joueur = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJoueur);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // total actuel
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
