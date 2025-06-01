package jdbc;

import java.sql.*;
import jdbc.metier.JoueurBDD;

public class DaoJoueur {
    private static final String URL = "jdbc:mysql://localhost:3306/projet_uno";
    private static final String USER = "user_uno";
    private static final String PASSWORD = "uno123"; // adapte selon ton mot de passe réel

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Méthode pour insérer ou récupérer un joueur
    public static JoueurBDD getOrCreateJoueur(String pseudo) {
        try (Connection conn = getConnection()) {

            // 1. Vérifier si le joueur existe déjà
            String selectSQL = "SELECT id_joueur FROM Joueur WHERE pseudo = ?";
            try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
                ps.setString(1, pseudo);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("id_joueur");
                    return new JoueurBDD(id, pseudo);
                }
            }

            // 2. S'il n'existe pas, l'insérer
            String insertSQL = "INSERT INTO Joueur(pseudo) VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, pseudo);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new JoueurBDD(id, pseudo);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Si erreur
    }
}
