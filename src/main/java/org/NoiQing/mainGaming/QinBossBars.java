package org.NoiQing.mainGaming;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinBossBar;
import org.NoiQing.util.Configuration;
import org.NoiQing.util.Function;
import org.NoiQing.util.QinBossBarDataSave;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;
import java.util.Set;

public class QinBossBars {
    private final Configuration data;
    public QinBossBars(QinKitPVPS plugin) {
        data = plugin.getResource().getData();
        initializationBossBar();
    }

    public void initializationBossBar() {
        readConfigAndSendMessage(Objects.requireNonNull(data.getConfigurationSection("BossBars")),"BossBars");
    }

    public void reloadBossBars() {
        QinBossBarDataSave.clearQinBossBarDataSave();
        readConfigAndSendMessage(Objects.requireNonNull(data.getConfigurationSection("BossBars")),"BossBars");
    }

    private void readConfigAndSendMessage(ConfigurationSection section, String parentKey) {
        Set<String> keys = section.getKeys(false);
        for (String key : keys) {
            // 获取当前键对应的 ConfigurationSection
            ConfigurationSection subSection = section.getConfigurationSection(key);
            if (subSection != null) {
                // 如果当前键对应的是一个 ConfigurationSection，则递归处理
                readConfigAndSendMessage(subSection, key);
            } else {
                BossBar bossBar = Bukkit.createBossBar(Function.changeColorCharacters(data.getString("BossBars."+parentKey+".Title")), BarColor.BLUE, BarStyle.SEGMENTED_6);
                QinBossBar qinBossBar = new QinBossBar(bossBar, data.getString("BossBars."+parentKey+".ScoreBoard"), data.getBoolean("BossBars."+parentKey+".ifASC"), data.getInt("BossBars."+parentKey+".Max"), parentKey);
                QinBossBarDataSave.getBossBarCache().put(parentKey,qinBossBar);
                return;
            }
        }
    }
}
