package me.whityfox.upgradedWhitelist.database;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class WhiteListDatabase {

    private final Connection connection;

    public WhiteListDatabase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try(Statement statement = connection.createStatement()){
            statement.execute("""
                CREATE TABLE IF NOT EXISTS whitelisted_players(player_name TEXT)
                """);
        }

    }
    public void AddPlayerToWhileList(String name) throws SQLException{
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO whitelisted_players(player_name) VALUES (?)")){
            preparedStatement.setString(1, name) ;
            preparedStatement.executeUpdate();
        }

    }
    public void RemoveFromWhiteList(String name) throws SQLException{
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM whitelisted_players WHERE player_name = ?")){
            preparedStatement.setString(1, name) ;
            preparedStatement.executeUpdate();
        }
    }
    public Boolean isWhitelisted(String name) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM whitelisted_players WHERE player_name = ?")){
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        }
    }
    public List<String> getWhitelistedPlayers() {
        List<String> players = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT player_name FROM whitelisted_players ORDER BY player_name"
            );
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String playerName = resultSet.getString("player_name");
                players.add(playerName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    public void closeConnection() throws SQLException{
        if (connection != null && !connection.isClosed()){
            connection.close();
        }
    }

}
