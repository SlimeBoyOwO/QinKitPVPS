package org.NoiQing.DataBase;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.NoiQing.util.Function;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLDataBase {
    private DataSource dataSource;
    public MySQLDataBase(String host, int port, String database, String username, String password) {
        setupMySQL(host,port, database, username, password);
    }

    private void setupMySQL(String host, int port, String database, String username, String password) {
        try {
            MysqlDataSource dataSource = new MysqlConnectionPoolDataSource(); // new MysqlDataSource();
            dataSource.setServerName(host);
            dataSource.setPortNumber(port);
            dataSource.setCreateDatabaseIfNotExist(true); // idk
            dataSource.setDatabaseName(database);
            dataSource.setUser(username);
            dataSource.setPassword(password);

            this.dataSource = dataSource;

            testDatabaseConnection();
            createMySQLDataBase();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("无法连接到mysql数据库！");
        }
    }

    private void createMySQLDataBase() throws SQLException {
        if (dataSource instanceof MysqlDataSource) {
            try (Statement mysqlStatement = dataSource.getConnection().createStatement()) {
                mysqlStatement.execute("""
                                create table if not exists qinkit
                                (
                                    id        int         not null
                                        primary key,
                                    file_name varchar(20) not null,
                                    name      varchar(20) not null,
                                    rare      char(4)     not null,
                                    price     int         null
                                );
                                """);
                mysqlStatement.execute("""
                                create table if not exists title_prefix
                                (
                                    title_id varchar(20) not null
                                        primary key,
                                    content  varchar(40) not null,
                                    rare     char(4)     not null,
                                    price    int         not null
                                );
                                """);
                mysqlStatement.execute("""
                                create table if not exists kill_effect
                                (
                                    id      int         not null
                                        primary key,
                                    name    varchar(20) not null,
                                    id_name varchar(20) not null,
                                    rare    char(4)     not null,
                                    price   int         not null
                                                                );
                                """);
                mysqlStatement.execute("""
                                create table if not exists player_data
                                (
                                   uuid                 char(36)      not null
                                        primary key,
                                   playerName           varchar(20)   not null,
                                   exp                  int default 0 not null,
                                   money                int default 0 not null,
                                   keyAmount            int default 0 not null,
                                   equip_kill_effect_id int           null,
                                   equip_title_id       varchar(20)   null,
                                   constraint player_data_kill_effect_id_fk
                                        foreign key (equip_kill_effect_id) references kill_effect (id_name),
                                   constraint player_data_title_prefix_title_id_fk
                                        foreign key (equip_title_id) references title_prefix (title_id)
                                );
                                """);
                mysqlStatement.execute("""
                                create table if not exists player_own_kill_effect
                                (
                                   uuid           varchar(36) not null,
                                   kill_effect_id int         not null,
                                   constraint player_own_kill_effect_kill_effect_id_fk
                                       foreign key (kill_effect_id) references kill_effect (id_name),
                                   constraint player_own_kill_effect_player_data_uuid_fk
                                       foreign key (uuid) references player_data (uuid)
                                );
                                """);
                mysqlStatement.execute("""
                                create table if not exists player_own_kit
                                (
                                   uuid    char(36)      not null
                                       primary key,
                                   kit_id  int           not null,
                                   kit_exp int default 0 not null,
                                   constraint player_own_player_data_uuid_fk
                                       foreign key (uuid) references player_data (uuid)
                                            on delete cascade,
                                   constraint player_own_qinkit_id_fk
                                       foreign key (kit_id) references qinkit (id)
                                            on delete cascade
                                );
                                """);
                mysqlStatement.execute("""
                                create table if not exists player_own_title
                                (
                                    uuid     char(36)    not null,
                                    title_id varchar(20) not null,
                                    constraint player_own_title_player_data_uuid_fk
                                        foreign key (uuid) references player_data (uuid),
                                    constraint player_own_title_title_prefix_title_id_fk
                                        foreign key (title_id) references title_prefix (title_id)
                                );
                                """);
                }
            }
    }

    private void testDatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(1)) {
                throw new SQLException("Could not establish database connection");
            }
        }
    }

    public void closeConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayer(Player player) throws SQLException{
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("INSERT INTO player_data (uuid, playerName) VALUES (?, ?)")){
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        }
    }

    public boolean playerNotExists(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("SELECT * FROM player_data WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return !resultSet.next();
        }
    }

    public int getPlayerKey(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("SELECT keyAmount FROM player_data WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt("keyAmount");
            else return 0;
        }
    }
    public void setPlayerKey(Player player, int keyAmount) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("UPDATE player_data SET keyAmount = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, keyAmount);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public boolean ifPlayerHasKit(Player player, String kitName) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT * FROM player_data p, qinkit k, player_own_kit o " +
                        "WHERE p.uuid = ? AND k.file_name = ? " +
                        "AND p.uuid = o.uuid AND k.id = o.kit_id")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, kitName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void givePlayerKit(Player player, String kitName) throws SQLException {
        if(ifPlayerHasKit(player, kitName)) return;


        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "INSERT INTO player_own_kit (uuid, kit_id) "+
                        "SELECT p.uuid, k.id " +
                        "FROM player_data p, qinkit k " +
                        "WHERE p.uuid = ? AND k.file_name = ?")) {
            preparedStatement.setString(1,player.getUniqueId().toString());
            preparedStatement.setString(2, kitName);
            preparedStatement.executeUpdate();
        }
    }

    public void refreshDefaultKits() throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT * FROM player_data"
        )) {
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                try (PreparedStatement preparedStatement1 = dataSource.getConnection().prepareStatement(
                        "INSERT INTO player_own_kit VALUES (?, 0, 0) "
                )) {
                    preparedStatement1.setString(1, set.getString("uuid"));
                    preparedStatement1.executeUpdate();
                }
                try (PreparedStatement preparedStatement1 = dataSource.getConnection().prepareStatement(
                        "INSERT INTO player_own_kit VALUES (?, 1, 0) "
                )) {
                    preparedStatement1.setString(1, set.getString("uuid"));
                    preparedStatement1.executeUpdate();
                }
                try (PreparedStatement preparedStatement1 = dataSource.getConnection().prepareStatement(
                        "INSERT INTO player_own_kit VALUES (?, 36, 0) "
                )) {
                    preparedStatement1.setString(1, set.getString("uuid"));
                    preparedStatement1.executeUpdate();
                }
            }
        }
    }

    public boolean ifPlayerHasKillEffect(Player player, String effectName) throws SQLException {
        try (PreparedStatement pr = dataSource.getConnection().prepareStatement(
                "SELECT * FROM player_own_kill_effect WHERE uuid = ? AND kill_effect_id = ?"
        )) {
            pr.setString(1, player.getUniqueId().toString());
            pr.setString(2, effectName);
            ResultSet set = pr.executeQuery();
            return set.next();
        }
    }

    public void givePlayerKillEffect(Player player, String effectName) throws SQLException {
        if (ifPlayerHasKillEffect(player, effectName)) {
            Function.sendPlayerSystemMessage(player, "&3你已经拥有这个效果了啊喂 &b=^=");
            return;
        }

        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "INSERT INTO player_own_kill_effect(uuid, kill_effect_id) " +
                        "VALUES (?, ?)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, effectName);
            preparedStatement.executeUpdate();
        }

        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT name FROM kill_effect WHERE id_name = ?")) {
            preparedStatement.setString(1, effectName);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String name = resultSet.getString("name");
            Function.sendPlayerSystemMessage(player, "&3你获得了新的效果"+ name +" &b=w=");
        }
    }

    public String getPlayerEquipKillEffect(Player player) throws SQLException {
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(
                "SELECT equip_kill_effect_id FROM player_data WHERE equip_kill_effect_id IS NOT NULL AND uuid = ?"
        )) {
            ps.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = ps.executeQuery();
            if(!resultSet.next()) return null;
            return resultSet.getString("equip_kill_effect_id");
        }
    }

    public void setPlayerEquipKillEffect(Player player, String killEffectID) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "UPDATE player_data SET equip_kill_effect_id = ? WHERE uuid = ?"
        )) {
            preparedStatement.setString(1,killEffectID);
            preparedStatement.setString(2,player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }
    public List<String> getPlayerAllKillEffects(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT kill_effect_id FROM player_own_kill_effect WHERE uuid = ?")) {
            List<String> effects = new ArrayList<>();
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet set = preparedStatement.executeQuery();
            while(set.next()) {
                effects.add(set.getString("kill_effect_id"));
            }
            return effects;
        }
    }
    public List<String> getPlayerAllKillSounds(Player player)throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT sound_id FROM player_own_kill_sound WHERE uuid = ?")) {
            List<String> sounds = new ArrayList<>();
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                sounds.add(set.getString("sound_id"));
            }
            return sounds;
        }
    }

    public void removePlayerEquipKillEffect(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "UPDATE player_data SET equip_kill_effect_id = ? WHERE uuid = ?"
        )) {
            preparedStatement.setNull(1,Types.INTEGER);
            preparedStatement.setString(2,player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public String getPlayerEquipTitle(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT equip_title_id FROM player_data WHERE uuid = ? AND equip_title_id IS NOT NULL"
        )) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return null;
            String titleID = resultSet.getString("equip_title_id");
            try (PreparedStatement preparedStatement2 = dataSource.getConnection().prepareStatement(
                    "SELECT content FROM title_prefix WHERE title_id = ?")) {
                preparedStatement2.setString(1, titleID);
                ResultSet resultSet2 = preparedStatement2.executeQuery();
                if(!resultSet2.next()) return null;
                return resultSet2.getString("content");
            }
        }
    }

    public void setPlayerEquipTitle(Player player, String titleID) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "UPDATE player_data SET equip_title_id = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, titleID);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public void removePlayerEquipTitle(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "UPDATE player_data SET equip_title_id = ? WHERE uuid = ?"
        )) {
            preparedStatement.setNull(1,Types.VARCHAR);
            preparedStatement.setString(2,player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public void givePlayerTitle(Player player, String titleID) throws SQLException {
        if(!ifTitleExists(titleID)){
            Function.sendPlayerSystemMessage(player, "&3该称号不存在... &b>A<");
            return;
        }
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "INSERT INTO player_own_title (uuid, title_id) VALUES (?,?)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, titleID);
            preparedStatement.executeUpdate();
        }
    }

    public boolean ifPlayerHasTitle(Player player, String titleID) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT * FROM player_own_title WHERE uuid = ? AND title_id = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, titleID);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public boolean ifPlayerHasKillSound(Player player, String killSoundID) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT * FROM player_own_kill_sound WHERE uuid = ? AND sound_id = ?"
        )) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, killSoundID);
            ResultSet set = preparedStatement.executeQuery();
            return set.next();
        }
    }

    public void givePlayerKillSound(Player player, String killSoundID) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "INSERT INTO player_own_kill_sound (uuid, sound_id) VALUES (?, ?)"
        )) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, killSoundID);
            preparedStatement.executeUpdate();
        }
    }

    public void setPlayerEquipKillSound(Player player, String killSoundID) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "UPDATE player_data SET equip_kill_sound = ? WHERE uuid = ?"
        )) {
            preparedStatement.setString(1, killSoundID);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public String getPlayerEquipKillSound(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT equip_kill_sound FROM player_data WHERE uuid = ?"
        )) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet set = preparedStatement.executeQuery();
            if (set.next()) {
                return set.getString("equip_kill_sound");
            }
        }
        return null;
    }

    public void removePlayerEquipKillSound(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "UPDATE player_data SET equip_kill_sound = ? WHERE uuid = ?"
        )) {
            preparedStatement.setNull(1, Types.VARCHAR);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public void updatePlayerMoney(Player player, int money) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "UPDATE player_data SET money = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, money);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public int getPlayerMoney(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT money FROM player_data WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt("money");
            else return 0;
        }
    }

    public void updatePlayerExp(Player player, int exp) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "UPDATE player_data SET exp = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, exp);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public int getPlayerExp(Player player) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT exp FROM player_data WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt("exp");
            else return 0;
        }
    }


    public String getContentFromTitleID(String titleID) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT content FROM title_prefix WHERE title_id = ?")) {
            preparedStatement.setString(1, titleID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getString("content");
            else return null;
        }
    }

    public String getKillEffectNameFromIDName(String idName)throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT name FROM kill_effect WHERE id_name = ?")) {
            preparedStatement.setString(1, idName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getString("name");
            else return null;
        }
    }

    public boolean ifTitleExists(String titleID) throws SQLException {
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT * FROM title_prefix WHERE title_id = ?")) {
            preparedStatement.setString(1, titleID);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }
    public List<String> getAllKits() throws SQLException {
        List<String> kits = new ArrayList<>();
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT file_name FROM qinkit"
        )) {
            ResultSet set = preparedStatement.executeQuery();
            while(set.next()) {
                kits.add(set.getString("file_name"));
            }
            return kits;
        }
    }

    public List<String> getAllAvailableKits() throws SQLException {
        List<String> kits = new ArrayList<>();
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT file_name FROM qinkit WHERE available = TRUE"
        )) {
            ResultSet set = preparedStatement.executeQuery();
            while(set.next()) {
                kits.add(set.getString("file_name"));
            }
            return kits;
        }
    }

    public List<String> getPlayerAllKits(Player player) throws SQLException {
        List<String> kits = new ArrayList<>();
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT file_name FROM qinkit k, player_own_kit p WHERE p.uuid = ? AND p.kit_id = k.id"
        )) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet set = preparedStatement.executeQuery();
            while(set.next()) {
                kits.add(set.getString("file_name"));
            }
            return kits;
        }
    }

    public Map<String, Map<String, String>> getKitInfo() throws SQLException{
        Map<String, Map<String, String>> kits = new HashMap<>();
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT * FROM qinkit"
        )) {
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                kits.put(set.getString("file_name"), new HashMap<>());
                kits.get(set.getString("file_name")).put("name", set.getString("name"));
                kits.get(set.getString("file_name")).put("rare", set.getString("rare"));
                kits.get(set.getString("file_name")).put("available", set.getString("available"));
                kits.get(set.getString("file_name")).put("price", set.getString("price"));
            }
        }
        return kits;
    }

    public List<String> getPlayerNotOwnKits(Player player) throws SQLException {
        List<String> kits = new ArrayList<>();
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT file_name FROM qinkit " +
                        "EXCEPT " +
                        "SELECT file_name FROM qinkit k, player_own_kit p WHERE p.uuid = ? AND p.kit_id = k.id"
        )) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet set = preparedStatement.executeQuery();
            while(set.next()) {
                kits.add(set.getString("file_name"));
            }
            return kits;
        }
    }

    public int getKillEffectPrice(String nameID) throws SQLException {
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT price FROM kill_effect WHERE id_name = ?"
        )) {
            preparedStatement.setString(1, nameID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("price");
            }
        }
        return -1;
    }

    public String getKitNameFromFileName(String fileName) throws SQLException {
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT name FROM qinkit WHERE file_name = ?"
        )) {
            preparedStatement.setString(1,fileName);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) return rs.getString("name");
            else return "未查找到该职业";
        }
    }

    public String getKitRareFromFileName(String fileName) throws SQLException {
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT rare FROM qinkit WHERE file_name = ?"
        )) {
            preparedStatement.setString(1,fileName);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) return rs.getString("rare");
            else return "未查找到该职业";
        }
    }

    public Map<String, Map<String, String>> getKillEffectInfo() throws SQLException{
        Map<String, Map<String, String>> killEffect = new HashMap<>();
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT * FROM kill_effect"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String primaryKey = resultSet.getString("id_name");
                killEffect.put(primaryKey,new HashMap<>());
                killEffect.get(primaryKey).put("price",resultSet.getString("price"));
                killEffect.get(primaryKey).put("rare",resultSet.getString("rare"));
                killEffect.get(primaryKey).put("name",resultSet.getString("name"));
            }
        }
        return killEffect;
    }

    public Map<String, Map<String, String>> getKillSoundInfo() throws SQLException{
        Map<String, Map<String, String>> killSound = new HashMap<>();
        try(PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(
                "SELECT * FROM kill_sound"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String primaryKey = resultSet.getString("id");
                killSound.put(primaryKey, new HashMap<>());
                killSound.get(primaryKey).put("name", resultSet.getString("name"));
                killSound.get(primaryKey).put("rare",resultSet.getString("rare"));
                killSound.get(primaryKey).put("price", resultSet.getString("price"));
            }
            return killSound;
        }
    }
}
