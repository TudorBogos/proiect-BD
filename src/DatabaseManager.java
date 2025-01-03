import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class DatabaseManager extends JOptionPane {
    private static final String URL = "jdbc:mysql://localhost:3306/admitere_facultate";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static Connection connection = null;

    // Function to open connection
    public static Connection openConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connection established");
            }
            return connection;
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            return null;
        }
    }

    // Function to close connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    /// Functie pentru selectarea tuturor studentilor cu try-with-resources
    public static ResultSet selectAllStudents() {
        try  {
            Connection conn = openConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                return stmt.executeQuery("SELECT * FROM Student");
            }
        } catch (SQLException e) {
            System.out.println("Error querying students: " + e.getMessage());
        }
        return null;
    }

    /// Functie delete cu try-with-resources
    public static int deleteStudent(int id) {
        StringBuilder sql = new StringBuilder("DELETE FROM STUDENT WHERE ID=?");
        int rowsAffected = 0;
        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            pstmt.setInt(1, id);
            rowsAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsAffected;

    }

    /// Functie insert cu try-with-resources
    public static int insertStudent(String nume, String prenume, String cnp, float nota) {
        StringBuilder sql = new StringBuilder("INSERT INTO STUDENT (");
        ArrayList<String> inserts = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        int rowsAffected = 0;

        // Add non-empty fields to the update
        if (!nume.isEmpty()) {
            inserts.add("Nume");
            values.add(nume);
        }
        if (!prenume.isEmpty()) {
            inserts.add("Prenume");
            values.add(prenume);
        }
        if (!cnp.isEmpty()) {
            inserts.add("CNP");
            values.add(cnp);
        }

        if (nota != 0f) {
            inserts.add("Nota");
            values.add(String.valueOf(nota));
        }

        // If nothing to update, return
        if (inserts.isEmpty()) {
            return 0;
        }

        // Build the query
        sql.append(String.join(", ", inserts));
        sql.append(") VALUES (");
        sql.append("?,".repeat(inserts.size()-1));
        sql.append("?)");

        // for debugging purposes
        System.out.println(sql.toString());

        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // Set all the values in order
            int paramIndex = 1;
            for (Object value : values) {
                pstmt.setObject(paramIndex++, value);
            }

            rowsAffected = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }


    /// Functie update cu try-with-resources
    public static int updateStudent(int id, String nume, String prenume, String cnp, float nota) {
        StringBuilder sql = new StringBuilder("UPDATE Student SET ");
        ArrayList<String> updates = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        int rowsAffected = 0;  // Declare here

        // Add non-empty fields to the update
        if (!nume.isEmpty()) {
            updates.add("Nume=?");
            values.add(nume);
        }
        if (!prenume.isEmpty()) {
            updates.add("Prenume=?");
            values.add(prenume);
        }
        if (!cnp.isEmpty()) {
            updates.add("CNP=?");
            values.add(cnp);
        }

        if (nota != 0f) {
            updates.add("Nota=?");
            values.add(nota);
        }

        // If nothing to update, return
        if (updates.isEmpty()) {
            return 0;
        }

        // Build the query
        sql.append(String.join(", ", updates));
        sql.append(" WHERE idStudent=?");

        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // Set all the values in order
            int paramIndex = 1;
            for (Object value : values) {
                pstmt.setObject(paramIndex++, value);
            }
            // Set the ID last
            pstmt.setInt(paramIndex, id);

            rowsAffected = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    /// Functie de authenticare a adminului cu try-with-resources
    public static boolean authenticateAdmin(String username, String password) {
        // Updated SQL query to match your table structure
        String sql = "SELECT * FROM authentication WHERE username=? AND password=?";
        boolean adminExists = false;

        try (Connection conn = DatabaseManager.openConnection();  // Use DatabaseManager's connection
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set both the username and password parameters
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            // Execute the query and check if we found a matching admin
            ResultSet rs = pstmt.executeQuery();
            adminExists = rs.next(); // Will be true if we found a matching admin

            rs.close();  // Clean up our ResultSet
        } catch (SQLException e) {
            System.out.println("Eroare la autentificarea adminului: " + e.getMessage());
        }

        return adminExists;
    }
}




    /// Functie de testare
    /*public static void selectAllStudentsTest(){
        try {
            Connection conn = openConnection();
            if (conn != null) {
                String query="SELECT * FROM Student";
                Statement stmt = conn.createStatement();
                ResultSet rs= stmt.executeQuery(query);

                while (rs.next()) {
                    int id = rs.getInt("idStudent");
                    String nume = rs.getString("Nume");
                    String prenume = rs.getString("Prenume");
                    String cnp = rs.getString("CNP");
                    float nota = rs.getFloat("nota");

                    System.out.printf("ID: %d, Nume: %s, Prenume: %s, CNP: %s, Nota: %.2f %n ",
                            id, nume, prenume, cnp, nota);
                }
            }
            closeConnection();
        } catch (SQLException e) {
            System.out.println("Error querying students: " + e.getMessage());
        }
    }*/
