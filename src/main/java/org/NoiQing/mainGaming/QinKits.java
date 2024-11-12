package org.NoiQing.mainGaming;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinKit;
import org.NoiQing.itemFunction.ItemsFunction;
import org.NoiQing.util.Configuration;
import org.NoiQing.util.CreateFileConfig;
import org.NoiQing.util.QinKitsDataSave;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;

public class QinKits {
    private final CreateFileConfig kitConfig;

    public QinKits(QinKitPVPS plugin){
        this.kitConfig = plugin.getResource();

    }
    public void giveQinKitToPlayer(Player player, QinKit QinKit){
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        QinKit.apply(player);
    }
    private QinKit createKitFromResource(Configuration resource) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        QinKit kit = new QinKit(trimName(resource.getName()));

        kit.setDisplayName(resource.contains("Kit.DisplayName") ? resource.getString("Kit.DisplayName") : "没有名字");

        kit.setAvailable(!resource.contains("Kit.Available") || resource.getBoolean("Kit.Available")) ;
        kit.setPermission(resource.getString("Kit.Permission"));
        kit.setPrice(resource.contains("Kit.Price") ? resource.getInt("Kit.Price") : 1000);

        kit.setHealth(resource.contains("Kit.Health") ? resource.getInt("Kit.Health") : 20);
        kit.setNewAttack(resource.contains("Kit.NewAttack") && resource.getBoolean("Kit.NewAttack"));

        kit.setHelmet(ItemsFunction.getItemStackFromPath(resource, "Inventory.Armor.Helmet"));
        kit.setChestplate(ItemsFunction.getItemStackFromPath(resource, "Inventory.Armor.Chestplate"));
        kit.setLeggings(ItemsFunction.getItemStackFromPath(resource, "Inventory.Armor.Leggings"));
        kit.setBoots(ItemsFunction.getItemStackFromPath(resource, "Inventory.Armor.Boots"));

        for (int i = 0; i < 36; i++) {
            if (resource.contains("Inventory.Items." + i)) {
                kit.setInventoryItem(i, ItemsFunction.getItemStackFromPath(resource, "Inventory.Items." + i));
            }
        }

        kit.setOffhand(ItemsFunction.getItemStackFromPath(resource, "Inventory.Items.Offhand"));
        kit.setCommands(resource.contains("Commands") ? resource.getStringList("Commands") : null);
        kit.setIntroduction(resource.contains("Introduction") ? resource.getStringList("Introduction") : null);

        if(resource.contains("Effects")) {
            ConfigurationSection effectSection = resource.getConfigurationSection("Effects");

            if (effectSection != null) {
                for (String effectName : effectSection.getKeys(false)) {
                    PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.minecraft(effectName.toLowerCase()));
                    if (effectType == null) {
                        effectType = PotionEffectType.MINING_FATIGUE;
                    }
                    int amplifier = resource.getInt("Effects." + effectName + ".Amplifier");
                    int duration = resource.getInt("Effects." + effectName + ".Duration");

                    kit.setEffect(effectType, amplifier, duration);
                }
            }
        }

        return kit;

    }

    private String trimName(String kitNameWithFileEnding) {
        String[] splitName = kitNameWithFileEnding.split(".yml");
        return splitName[0];
    }

    public QinKit getKitByName(String kitName) {
        return loadKitFromCacheOrCreate(kitName);
    }

    private QinKit loadKitFromCacheOrCreate(String kitName) {
        if (!QinKitsDataSave.getKitCache().containsKey(kitName)) {
            if (kitConfig.getPluginDirectoryFiles("Kits", false).contains(kitName)) {
                Configuration kit = kitConfig.getKit(kitName);
                try {
                    QinKitsDataSave.getKitCache().put(kitName, createKitFromResource(kit));
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return QinKitsDataSave.getKitCache().get(kitName);
    }


}
