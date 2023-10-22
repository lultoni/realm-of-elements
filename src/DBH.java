import java.sql.*;
import java.util.ArrayList;

public class DBH {

    private static final String filename = "realm_of_elements_db.db";

    public static void SQL_command(String command) {
        try{
            Connection connection = getConnection(filename);
            PreparedStatement statement = connection.prepareStatement(command);
            ResultSet resultSet = statement.executeQuery();

            try {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                String header = "\n| ";
                for (int i = 1; i <= columnCount; i++) {
                    header += metaData.getColumnName(i);
                    if (i + 1 <= columnCount) header += " | ";
                }
                System.out.println(header + " |");
                while (resultSet.next()) {
                    String line = " - | ";
                    for (int i = 1; i <= columnCount; i++) {
                        line += resultSet.getObject(i);
                        if (i + 1 <= columnCount) line += " | ";
                    }
                    System.out.println(line + " |");
                }
                System.out.println();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection(String filename){
        Connection connection = null;
        try{
            //Verbindung zur SQLite-Datenbank herstellen
            connection = DriverManager.getConnection("jdbc:sqlite:"+filename);
            //System.out.println("Verbindung zur SQLite-Datenbank hergestellt.");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public static ArrayList<Player> getAllPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        String command = "SELECT * FROM Players;";

        try {
            Connection connection = getConnection(filename);
            PreparedStatement statement = connection.prepareStatement(command);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                int gamesPlayed = resultSet.getInt("GamesPlayed");
                int elo = resultSet.getInt("Elo");

                // Create a Player object and add it to the players ArrayList
                Player player = new HumanPlayer(name, elo, gamesPlayed);
                players.add(player);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }

    public static void updatePlayer(int elo, int gamesPlayed, String name) {
        String command = "UPDATE Players SET Elo = ?, GamesPlayed = ? WHERE Name = ?";

        try {
            Connection connection = getConnection(filename);
            PreparedStatement statement = connection.prepareStatement(command);

            // Set the parameter values
            statement.setInt(1, elo); // Elo
            statement.setInt(2, gamesPlayed);    // GamesPlayed
            statement.setString(3, name); // Player name

            // Execute the SQL command
            statement.executeUpdate();

            // Close the statement and connection
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}