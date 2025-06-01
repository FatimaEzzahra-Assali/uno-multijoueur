package jdbc;

import java.sql.*;

public class DaoScore {

    private static final String URL = "jdbc:mysql://localhost:3306/projet_uno";
    private static final String USER = "user_uno";
    private static final String PASSWORD = "uno123";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

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
}
