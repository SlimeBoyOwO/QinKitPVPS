package org.NoiQing.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.Function;
import org.NoiQing.util.WeatherDataSave;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class WeatherRunnable extends BukkitRunnable {
    private final QinKitPVPS plugin;
    private int longerWeather = 0;
    private static BossBar bossBar;
    public WeatherRunnable(QinKitPVPS plugin){
        this.plugin = plugin;
        bossBar = Bukkit.createBossBar("=w=",BarColor.BLUE,BarStyle.SOLID);
    }

    @Override
    public void run() {
        //天气开关控制按钮
        if(!WeatherDataSave.getWeatherSwitch()) return;

        int mapID = plugin.getRandomMapID();
        String weather = "§e晴天";
        String weatherLore = "§b和蔼的晴天，无特殊效果";
        String weatherBefore = WeatherDataSave.getWeatherStorage().isEmpty() ? "晴天" : WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world"));
        if(longerWeather == 1){
            weather = weatherBefore;
            longerWeather += 1;
        }else if(longerWeather >= 2 || longerWeather == 0){
            longerWeather = 0;
            Random random = new Random();
            int chance = random.nextInt(1,1000 + 1);
            weather = chooseWeather(chance).get(0);
            weatherLore = chooseWeather(chance).get(1);
        }
        if(!weatherBefore.equals(weather)){
            longerWeather += 1;
            WeatherDataSave.getWeatherStorage().put(Bukkit.getWorld("world"), weather);
            bossBar.removeAll();
            Function.setBossBar(bossBar,"\uD83D\uDCE1 §7❃ §l" + Function.spreadString(weather, true) + " §7❃" + "  " + Function.getNameWithoutColor(weatherLore), BarColor.BLUE, BarStyle.SEGMENTED_6,20,20);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PluginScoreboard.changeScoreboard(onlinePlayer);
                if(!onlinePlayer.getScoreboardTags().contains("QinKitLobby")){
                    onlinePlayer.sendTitle("§7>> " +weather+ " §7<<",weatherLore,20,3 * 20,20);
                }
                bossBar.addPlayer(onlinePlayer);
                resetWeatherEffects();
            }
        }
    }

    public static void changeWeather(int chance){
        String weather = chooseWeather(chance).get(0);
        String weatherLore = chooseWeather(chance).get(1);
        WeatherDataSave.getWeatherStorage().put(Bukkit.getWorld("world"),weather);
        bossBar.removeAll();
        Function.setBossBar(bossBar,"\uD83D\uDCE1 §7❃ §l" + Function.spreadString(weather, true) + " §7❃" + "  " + Function.getNameWithoutColor(weatherLore), BarColor.BLUE, BarStyle.SEGMENTED_6,20,20);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PluginScoreboard.changeScoreboard(onlinePlayer);
            onlinePlayer.sendTitle("§7>> " +weather+ " §7<<",weatherLore,20,3 * 20,20);
            resetWeatherEffects();
            bossBar.addPlayer(onlinePlayer);
        }
    }

    private static void resetWeatherEffects(){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            final int defaultSaturatedRegenRate = 80;
            final int defaultUnsaturatedRegenRate = 10;
            onlinePlayer.setSaturatedRegenRate(defaultUnsaturatedRegenRate);
            onlinePlayer.setUnsaturatedRegenRate(defaultSaturatedRegenRate);
            onlinePlayer.setWalkSpeed(0.2f);
            Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(1);
            Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(0);
            Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(100);
            Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE)).setBaseValue(3);
            Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(1.0);
            if(onlinePlayer.getScoreboardTags().contains("WarFight")){
                Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(4);
            }
        }
    }

    private static ArrayList<String> chooseWeather(int chance){
        String weather;
        String weatherLore;
        if(chance <= 15){
            weather = "§8阴天";
            weatherLore = "§b适合跑步的天气，提高移速";
        }else if(chance <= 45){
            weather = ChatColor.of("#c1cc5a") + "风尘";
            weatherLore = "§b固化的沙子，无击退效果";
        }else if(chance <= 75){
            weather = ChatColor.of("#4668e3") + "小雨";
            weatherLore = "§b舒适的雨滴，提高自然回血";
        }else if(chance <= 100){
            weather = ChatColor.of("#50b7ba") + "小雪";
            weatherLore = "§b清爽的天气，攻击力小幅加强";
        }else if(chance <= 110){
            weather = "§c鸡§e尾§b酒";
            weatherLore = "§b天降鸡尾酒？随机效果II";
        }else if(chance <= 120){
            weather = "§cTNT雨";
            weatherLore = "§b爆炸的天气，天降炸药";
        }else if(chance <= 130){
            weather = "§7台风";
            weatherLore = "§b狂风的天气，增加击退";
        }else if(chance <= 140){
            weather = "§a酸雨";
            weatherLore = "§b侵蚀的天气，在水中必死";
        }else if(chance <= 150){
            weather = "§b春风";
            weatherLore = "§b惬意的天气，攻击距离翻倍";
        }else if(chance <= 160){
            weather = "§b梦游";
            weatherLore = "§b虚幻的天气，玩家体型缩小";
        }else if(chance > 875 && chance <= 900){
            weather = ChatColor.of("#FFB6C1") + "梅雨";
            weatherLore = "§b新的一年的雨，机制回归原版";
        }else if(chance > 900 && chance <= 1000){
            weather = "§b春雨";
            weatherLore = "§b春雨催生新事物，攻击变为1.9";
        }else{
            weather = "§e晴天";
            weatherLore = "§b和蔼的晴天，无特殊效果";
        }
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(weather);
        arrayList.add(weatherLore);
        return arrayList;

    }

    public static BossBar getBossbar() {return bossBar;}
}
