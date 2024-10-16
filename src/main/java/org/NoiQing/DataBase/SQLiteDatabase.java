package org.NoiQing.DataBase;

import org.NoiQing.util.Function;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;

public class SQLiteDatabase {
    private final Connection connection;

    public SQLiteDatabase(String path) throws SQLException {
        //通过路径建立和数据库的链接
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        //尝试执行语句
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL, " +
                    "money INTEGER NOT NULL DEFAULT 0, " +
                    "rankExp INTEGER NOT NULL DEFAULT 0, " +
                    "killEffects TEXT, " +
                    "playerPrefix TEXT, " +
                    "keys INTEGER NOT NULL DEFAULT 0)");
        }
    }

    //关闭与数据的链接
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayer(Player player) throws SQLException{
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid,username) VALUES (?, ?)")){
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        }
    }

    public boolean playerExists(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return !resultSet.next();
        }
    }

    public void updatePlayerMoney(Player player, int money) throws SQLException{
        //if the player doesn't exist, add them
        if (playerExists(player)){
            addPlayer(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET money = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public int getPlayerMoney(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT money FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("money");
            } else {
                return 0; // Return 0 if the player has no money
            }
        }
    }

    public void updatePlayerRankExp(Player player, int exp) throws SQLException{
        if (playerExists(player)){
            addPlayer(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET rankExp = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, exp);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public int getPlayerRankExp(Player player) throws SQLException{
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT rankExp FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("rankExp");
            } else {
                return 0; // Return 0 if the player has no rankEXP
            }
        }
    }

    public void initializeTextData(Player player, String column) throws SQLException {
        if (playerExists(player)){
            addPlayer(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET " + column + " = ? WHERE uuid = ?")) {
            if(getPlayerTextData(player, column) == null){
                preparedStatement.setString(1, "[]");
                preparedStatement.setString(2, player.getUniqueId().toString());
                preparedStatement.executeUpdate();
            }
        }
    }

    public String getPlayerTextData(Player player, String column) throws SQLException{
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(column);
            } else {
                return null;
            }
        }
    }

    public ArrayList<String> getPlayerTextDataArray(Player player, String column) throws SQLException{
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                initializeTextData(player, column);
                String strings = resultSet.getString(column);
                return Function.getArrayFromTextData(strings);
            } else {
                return null;
            }
        }
    }

    public void removePlayerTextData(Player player, String column, String data) throws SQLException {
        ArrayList<String> strings = getPlayerTextDataArray(player, column);
        if(strings == null) strings = new ArrayList<>();
        strings.remove(data);
        strings.remove("*" + data);
        updatePlayerTextData(player, column, textDataArrayToString(strings));
    }

    public void updatePlayerTextData(Player player, String column, String newString) throws SQLException{
        if (playerExists(player)){
            addPlayer(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET " + column + " = ? WHERE uuid = ?")) {
            initializeTextData(player, column);
            preparedStatement.setString(1, newString);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public void addPlayerTextData(Player player, String column, String data) throws SQLException{
        ArrayList<String> strings = getPlayerTextDataArray(player, column);
        if(strings == null) strings = new ArrayList<>();
        if(strings.contains(data)) return;
        strings.add(data);
        updatePlayerTextData(player, column, textDataArrayToString(strings));
    }

    public void setPlayerTextDataSelection(Player player, String column, String selection) throws SQLException {
        ArrayList<String> list = getPlayerTextDataArray(player, column);
        //排除玩家已经选用了这个特效的情况
        if(list.contains("*" + selection)){
            player.sendMessage("你已经选用了该特效！"); return;
        }
        //排除玩家没有这个特效的情况
        if(!list.contains(selection)){
            player.sendMessage("你没有该特效！"); return;
        }

        for(int i = 0; i < list.size(); i++){
            String s = list.get(i);
            if(s.startsWith("*")) list.set(i,s.replace("*",""));
            if(s.equals(selection)) {
                list.set(i,"*" + s);
                player.sendMessage("你已经选用了特效： " + selection);
            }
        }

        updatePlayerTextData(player, column, textDataArrayToString(list));
    }

    public String getPlayerTextDataSelection(Player player, String column) throws SQLException {
        for(String s : getPlayerTextDataArray(player, column))
            if(s.startsWith("*")) return s.replace("*","");
        return "None";
    }

    //算法部分
    private void insertSortArray(ArrayList<String> strings) {
        int j;
        for(int i = 0; i < strings.size(); i++){
            String temp = strings.get(i);
            for(j = i - 1; j >= 0 && compareString(temp,strings.get(j)); j--)
                strings.set(j + 1, strings.get(j));
            strings.set(j + 1, temp);
        }
    }

    private boolean compareString(String str1, String str2) {
        for(int i = 0; i < str1.length(); i++){
            if(i > str2.length() - 1) return false;
            if(str1.charAt(i) != str2.charAt(i)){
                return str1.charAt(i) < str2.charAt(i);
            }

        }
        return false;
    }

    private String textDataArrayToString(ArrayList<String> strings){
        insertSortArray(strings);
        StringBuilder str = new StringBuilder();
        for(String s : strings){
            if(s.isEmpty()) continue;
            str.append("\"").append(s).append("\"").append(",");
        }
        str.deleteCharAt(str.length() - 1);
        str = new StringBuilder(new StringBuilder("[" + str + "]"));
        return str.toString();
    }
}
