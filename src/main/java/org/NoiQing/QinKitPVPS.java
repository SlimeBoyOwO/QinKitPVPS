package org.NoiQing;

import org.NoiQing.AllayWar.AllayRunnable.AGameRunnable;
import org.NoiQing.AllayWar.AllayRunnable.TowerAttackRunnable;
import org.NoiQing.AllayWar.AWListeners.CustomShopListener;
import org.NoiQing.AllayWar.AWListeners.TowerListener;
import org.NoiQing.AllayWar.PvzGame.PVZListeners.PlantsListeners;
import org.NoiQing.AllayWar.PvzGame.PvzRunnable.PvzGameRunnable;
import org.NoiQing.BukkitRunnable.*;
import org.NoiQing.DataBase.MySQLDataBase;
import org.NoiQing.DataBase.RegisterDatabase;
import org.NoiQing.DataBase.SQLiteDatabase;
import org.NoiQing.EventListener.GuiListeners.CustomMenuListeners;
import org.NoiQing.EventListener.Guns.GunsListener;
import org.NoiQing.EventListener.SkillListeners.CustomSkillItems;
import org.NoiQing.EventListener.SkillListeners.PassiveSkillsListener;
import org.NoiQing.EventListener.SkillListeners.RecoveryItems;
import org.NoiQing.EventListener.SkillListeners.SkillItems;
import org.NoiQing.EventListener.System.*;
import org.NoiQing.EventListener.Temp.BirthDayListener;
import org.NoiQing.commands.CompleteCommands;
import org.NoiQing.commands.MainCommands;
import org.NoiQing.mainGaming.Game;
import org.NoiQing.util.CreateFileConfig;
import org.NoiQing.util.DataBaseCache;
import org.NoiQing.util.Function;
import org.NoiQing.util.MapDataSave;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;

public class
QinKitPVPS extends JavaPlugin {
    private static QinKitPVPS plugin;
    private CreateFileConfig createFileConfig;
    private SQLiteDatabase SQLiteDatabase;
    private MySQLDataBase mySQLDataBase;
    private Game game;

    // 启 动 服 务 器 的 时 候 要 执 行 的 代 码
    @Override
    public void onEnable() {
        getLogger().info(
                """
                         
                         =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
                         ██████╗ ██╗███╗   ██╗██╗  ██╗██╗████████╗██████╗ ██╗   ██╗██████╗ ███████╗
                        ██╔═══██╗██║████╗  ██║██║ ██╔╝██║╚══██╔══╝██╔══██╗██║   ██║██╔══██╗██╔════╝
                        ██║   ██║██║██╔██╗ ██║█████╔╝ ██║   ██║   ██████╔╝██║   ██║██████╔╝███████╗
                        ██║▄▄ ██║██║██║╚██╗██║██╔═██╗ ██║   ██║   ██╔═══╝ ╚██╗ ██╔╝██╔═══╝ ╚════██║
                        ╚██████╔╝██║██║ ╚████║██║  ██╗██║   ██║   ██║      ╚████╔╝ ██║     ███████║
                         ╚══▀▀═╝ ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝╚═╝   ╚═╝   ╚═╝       ╚═══╝  ╚═╝     ╚══════╝
                                                   Version:0.8.0-MODIFIED      Author: NoiQing_Ling
                         =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
                         WARN: This plugin is a modified version of QinKitPVPS for non-commercial use, please follow the original plugin on GitHub.
                        """);

        plugin = this;
        // 创 建 配 置 文 件
        this.createFileConfig = new CreateFileConfig(this);
        // 初 始 化 游 戏 设 定
        this.game = new Game(this);

        // 监 听 器 注 册 器
        Bukkit.getPluginManager().registerEvents(new RecoveryItems(), this);
        Bukkit.getPluginManager().registerEvents(new SkillItems(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomSkillItems(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BornAndDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PreventBlockBoomListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CompassListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerKillPlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PassiveSkillsListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WallJumpListener(), this);
        Bukkit.getPluginManager().registerEvents(new GunsListener(this),this);
        Bukkit.getPluginManager().registerEvents(new WeatherEventListener(this),this);
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomMenuListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new BirthDayListener(),this);
        PlantsListeners plantsListeners = new PlantsListeners();
        Bukkit.getPluginManager().registerEvents(plantsListeners,this);

        //悦灵战争注册表
        Bukkit.getPluginManager().registerEvents(new TowerListener(),this);
        Bukkit.getPluginManager().registerEvents(new CustomShopListener(),this);

        // 注 册 指 令
        Objects.requireNonNull(getCommand("qinkit")).setExecutor(new MainCommands(this));
        Objects.requireNonNull(getCommand("qinkit")).setTabCompleter(new CompleteCommands());

        //注册世界
        Bukkit.createWorld(new WorldCreator("world"));
        // Bukkit.createWorld(new WorldCreator("teamkitpvp"));
        Bukkit.createWorld(new WorldCreator("skyblock"));
        Bukkit.createWorld(new WorldCreator("skyblock_copy"));

        // 循 环 任 务
        new WeatherRunnable(this).runTaskTimer(plugin, 0, 120 * 20);
        new WeatherEffectsRunnable(this).runTaskTimer(plugin, 1, 1);
        new PassiveSkillRunnable(this).runTaskTimer(plugin, 0, 1);
        new WallJumpRunnable().runTaskTimer(plugin, 0, 1);
        new TeamModeRunnable().runTaskTimer(plugin,0,1);
        new EffectRunnable().runTaskTimer(plugin,0,1);
        new AGameRunnable().runTaskTimer(plugin,0,1);
        PvzGameRunnable pubPvzGameRunnable = new PvzGameRunnable();
        plantsListeners.setRunnable(pubPvzGameRunnable);
        pubPvzGameRunnable.runTaskTimer(plugin,0,1);

        //悦灵战争循环任务
        new TowerAttackRunnable().runTaskTimer(plugin,0,1);

        //建造数据库
        try {
            SQLiteDatabase = new SQLiteDatabase(plugin.getDataFolder().getAbsolutePath()+"/GameData.db");
            getLogger().info("[QinKitPVPS] SQLite数据加载成功！owo");
            mySQLDataBase = new MySQLDataBase(
                    createFileConfig.getConfig().getString("Storage.MySQL.Host"),
                    createFileConfig.getConfig().getInt("Storage.MySQL.Port"),
                    createFileConfig.getConfig().getString("Storage.MySQL.Database"),
                    createFileConfig.getConfig().getString("Storage.MySQL.Username"),
                    createFileConfig.getConfig().getString("Storage.MySQL.Password")
            );
            getLogger().info("[QinKitPVPS] MySQL数据加载成功！=w=");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库创建失败了OAO" + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }


        Bukkit.getPluginManager().registerEvents(new RegisterDatabase(this),this);
        DataBaseCache.initCache();

        // 混 战 模 式 随 机 地 图 I D 轮 换 循 环 任 务
        new MapRunnable(this).runTaskTimer(plugin, 0, 15 * 60 * 20);

        for(Player player : Bukkit.getOnlinePlayers()) PlayerJoinListener.refreshPlayer(player,this);


        // 防 止 队 伍 系 统 紊 乱
        Function.reloadTeams();
    }

    // 关 闭 服 务 器 的 时 候 要 执 行 的 代 码
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
        try{
            SQLiteDatabase.closeConnection();
            mySQLDataBase.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
            BossBar bossBar = it.next();
            bossBar.removeAll();
        }
        Bukkit.getServer().getScheduler().cancelTasks(this);
        WeatherRunnable.getBossbar().removeAll();
    }

    public static QinKitPVPS getPlugin() {return plugin;}
    public CreateFileConfig getResource() {return createFileConfig;}
    public Game getGame() {return game;}
    public int getRandomMapID() {return MapDataSave.getMapStorage().get(Bukkit.getWorld("world"));}
    public int getRandomTeamMapID() {
        return 1;
    }
    public SQLiteDatabase getSQLiteDatabase() { return SQLiteDatabase;}
    public MySQLDataBase getMySQLDataBase() { return mySQLDataBase; }
}
