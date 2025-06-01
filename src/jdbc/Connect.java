package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/projet_uno",
                    "user_uno",
                    "uno123"
            );
            System.out.println("Connexion OK !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
