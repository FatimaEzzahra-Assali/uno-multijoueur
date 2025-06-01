package jdbc;

import jdbc.metier.PartieBDD;

import java.sql.*;
import java.time.LocalDateTime;

public class DaoPartie {
    private static final String URL = "jdbc:mysql://localhost:3306/projet_uno";
    private static final String USER = "user_uno";
    private static final String PASSWORD = "uno123"; // adapte si nécessaire

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static PartieBDD creerNouvellePartie() {
        String insertSQL = "INSERT INTO Partie() VALUES ()";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                int id = rs.getInt(1);
                LocalDateTime maintenant = LocalDateTime.now(); // approximation de la date stockée en SQL
                return new PartieBDD(id, maintenant);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
