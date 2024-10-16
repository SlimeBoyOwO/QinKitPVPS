package org.NoiQing.AllayWar.AWUtils;

import org.NoiQing.AllayWar.AWAPI.AWRound;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinMap;
import org.NoiQing.api.QinTeam;
import org.NoiQing.itemFunction.ItemsFunction;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Configuration;
import org.NoiQing.util.Function;
import org.NoiQing.util.QinConstant;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AWFunction {

    private static final Map<ChatColor, Color> chatColorColorMap = Map.of(
            ChatColor.AQUA,Color.AQUA,
            ChatColor.BLUE,Color.BLUE,
            ChatColor.RED,Color.RED,
            ChatColor.YELLOW,Color.YELLOW,
            ChatColor.GREEN,Color.GREEN
    );
    private static final Map<Color, ChatColor> colorChatColorMap = Map.of(
            Color.AQUA,ChatColor.AQUA,
            Color.BLUE,ChatColor.BLUE,
            Color.RED,ChatColor.RED,
            Color.YELLOW,ChatColor.YELLOW,
            Color.GREEN,ChatColor.GREEN
    );
    public static void setNameByTeam(Entity e, String name) {
        QinTeam team = QinTeams.getEntityTeam(e);
        if(team != null) {
            Color c = team.getTeamColor();
            e.setCustomName(colorChatColorMap.get(c) + name);
        } else
            e.setCustomName(name);

        e.setCustomNameVisible(true);
    }

    public static String getNameByTeam(Entity e, String name) {
        QinTeam team = QinTeams.getEntityTeam(e);
        if(team != null) {
            Color c = team.getTeamColor();
            return colorChatColorMap.get(c) + name;
        }
        return name;
    }
    public static String getAWTeam(Player p) {
        if(p.getScoreboardTags().contains("AW_Team_蓝")) return "Blue";
        else if(p.getScoreboardTags().contains("AW_Team_黄")) return "Yellow";
        else if(p.getScoreboardTags().contains("AW_Team_红")) return "Red";
        else if(p.getScoreboardTags().contains("AW_Team_绿")) return "Green";
        else return "None";
    }

    public static boolean isEntityInTeam(LivingEntity lv) {
        return QinTeams.getEntityTeam(lv) != null;
    }
    public static Location getTeamRespawnPoint(String team) {
        QinMap m = QinKitPVPS.getPlugin().getGame().getMaps().getAllayQinMapByMapID(AWRound.getChooseMapID());
        int x = switch (team) {
            case "Yellow" -> 1;
            case "Blue" -> 2;
            case "Green" -> 3;
            default -> 0;
        };
        return m.getLocation().get(String.valueOf(x));
    }

    public static boolean isAllayTower(Entity a) {
        for(String s : a.getScoreboardTags()) {
            if(s.contains(QinConstant.ALLAY_TOWER_TAG)) return true;
        }
        return false;
    }

    public static boolean isTank(Entity a) {
        for(String s : a.getScoreboardTags()) {
            if(s.contains("tank")) return true;
        }
        return false;
    }

    public static String getAllayTowerName(Entity a) {
        return Function.getNameWithoutColor(a.getCustomName()).replaceAll("[^\\p{IsHan}]", "");
    }

    public static boolean isNotFriendlyTower(Entity a) {
        String towerName = Function.getNameWithoutColor(a.getCustomName()).replaceAll("[^\\p{IsHan}]", "");
        return towerName.endsWith("塔") || towerName.endsWith("炮") || towerName.equals("生命核心");
    }

    public static void setMobEquipment(LivingEntity m, ItemStack... item) {
        ItemStack air = new ItemStack(Material.AIR);
        int length = item.length;
        Objects.requireNonNull(m.getEquipment()).setItemInMainHand(item[0] == null ? air : item[0]);
        if(length > 1) m.getEquipment().setHelmet(item[1] == null ? air : item[1]);
        if(length > 2) m.getEquipment().setChestplate(item[2] == null ? air : item[2]);
        if(length > 3) m.getEquipment().setLeggings(item[3] == null ? air : item[3]);
        if(length > 4) m.getEquipment().setBoots(item[4] == null ? air : item[4]);
    }

    public static void givePlayerRespawnItem(Player p) {
        QinTeam t = QinTeams.getEntityTeam(p);
        int sharpnessLevel = 0; int protectionLevel = 0; int haveRaider = 0;
        if(t != null) {
            sharpnessLevel = AWRound.getTeamLevels(t.getTeamName()).getOrDefault("Sharpness",0);
            protectionLevel = AWRound.getTeamLevels(t.getTeamName()).getOrDefault("Protection",0);
            haveRaider = AWRound.getTeamLevels(t.getTeamName()).getOrDefault("HaveRaider",0);
        }

        ItemStack raider = new ItemStack(Material.SPYGLASS);
        ItemMeta raiderMeta = raider.getItemMeta();
        if (raiderMeta != null) {
            raiderMeta.setDisplayName("§7§l雷达视野");
        }
        raider.setItemMeta(raiderMeta);


        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        ItemMeta meta = helmet.getItemMeta();
        if(meta instanceof LeatherArmorMeta lm) {
            if(t != null) lm.setColor(t.getTeamColor());

            helmet.setItemMeta(lm);
            chestplate.setItemMeta(lm);
            leggings.setItemMeta(lm);
            boots.setItemMeta(lm);
        }

        ItemsFunction.setUnbreakable(helmet,chestplate,leggings,boots);
        helmet.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
        chestplate.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
        leggings.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);


        if(p.getScoreboardTags().contains("netherite_armored")) {
            ItemStack nethertie_leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
            ItemStack nethertie_boots = new ItemStack(Material.NETHERITE_BOOTS);
            ItemsFunction.setUnbreakable(nethertie_boots,nethertie_leggings);
            nethertie_boots.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
            nethertie_leggings.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
            p.getInventory().setLeggings(nethertie_leggings);
            p.getInventory().setBoots(nethertie_boots);
        }
        if(p.getScoreboardTags().contains("diamond_armored")) {
            ItemStack diamond_leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
            ItemStack diamond_boots = new ItemStack(Material.DIAMOND_BOOTS);
            ItemsFunction.setUnbreakable(diamond_leggings,diamond_boots);
            diamond_leggings.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
            diamond_boots.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
            p.getInventory().setLeggings(diamond_leggings);
            p.getInventory().setBoots(diamond_boots);
        }
        else if(p.getScoreboardTags().contains("iron_armored")) {
            ItemStack iron_leggings = new ItemStack(Material.IRON_LEGGINGS);
            ItemStack iron_boots = new ItemStack(Material.IRON_BOOTS);
            ItemsFunction.setUnbreakable(iron_leggings,iron_boots);
            iron_leggings.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
            iron_boots.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
            p.getInventory().setLeggings(iron_leggings);
            p.getInventory().setBoots(iron_boots);
        }else if(p.getScoreboardTags().contains("chainmail_armored")) {
            ItemStack chainmail_leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
            ItemStack chainmail_boots = new ItemStack(Material.CHAINMAIL_BOOTS);
            ItemsFunction.setUnbreakable(chainmail_leggings,chainmail_boots);
            chainmail_leggings.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
            chainmail_boots.addUnsafeEnchantment(Enchantment.PROTECTION,protectionLevel);
            p.getInventory().setLeggings(chainmail_leggings);
            p.getInventory().setBoots(chainmail_boots);
        }else {
            p.getInventory().setLeggings(leggings);
            p.getInventory().setBoots(boots);
        }
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);

        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        if(sharpnessLevel > 0) sword.addUnsafeEnchantment(Enchantment.SHARPNESS, sharpnessLevel);

        ItemsFunction.setUnbreakable(sword);
        p.getInventory().addItem(sword);
        if(haveRaider == 1) p.getInventory().addItem(raider);
    }

    public static Allay findNearestTower(Mob mob, double findDistance) {
        QinTeam team = QinTeams.getEntityTeam(mob);


        double nearestAllay = findDistance;
        Allay closestAllay = null;
        Location allayLocation = mob.getLocation();

        for (Entity entity : mob.getNearbyEntities(findDistance, findDistance, findDistance)) {
            if(entity.equals(mob)) continue;
            if(team != null && team.equals(QinTeams.getEntityTeam(entity)))
                continue;
            if(entity.isDead()) continue;
            if(!(entity instanceof Allay)) continue;
            double distance = entity.getLocation().distance(allayLocation);

            if (distance < nearestAllay) {
                nearestAllay = distance;
                closestAllay = (Allay) entity;
            }
        }

        return closestAllay;
    }

    public static void summonTank(Entity tankCore, String tankName, float entityHeight) {
        Configuration data = QinKitPVPS.getPlugin().getResource().getData();
        if(!data.contains("TankData." + tankName)) {
            Bukkit.broadcastMessage("坦克" + tankName + "不存在");
            return;
        }

        ItemDisplay lastItemDisplay = null;

        for(int i = 0; i < 100; i++) {
            if(data.contains("TankData." + tankName + "." + i + ".Item")) {
                ItemStack item = data.getItemStack("TankData." + tankName + "." + i + ".Item");
                ItemDisplay.ItemDisplayTransform transform = ItemDisplay.ItemDisplayTransform.valueOf(data.getString("TankData." + tankName + "." + i + ".ItemDisplay"));
                boolean isTankHead = data.getBoolean("TankData." + tankName + "." + i + ".IsTankHead");
                ItemDisplay display = tankCore.getWorld().spawn(tankCore.getLocation(), ItemDisplay.class);
                display.setItemStack(item);
                display.setItemDisplayTransform(transform);
                /*填空***/
                // 从 List 加载 Translation (Vector)
                List<Double> translationList = data.getDoubleList("TankData." + tankName + "." + i + ".Transform.Translation");
                Vector3f translation = new Vector3f(translationList.get(0).floatValue(), translationList.get(1).floatValue() - entityHeight, translationList.get(2).floatValue());

                // 从 List 加载 LeftRotation 和 RightRotation (Quaternion)
                List<Double> leftRotationList = data.getDoubleList("TankData." + tankName + "." + i + ".Transform.LeftRotation");
                Quaternionf leftRotation = new Quaternionf(leftRotationList.get(0).floatValue(), leftRotationList.get(1).floatValue(), leftRotationList.get(2).floatValue(), leftRotationList.get(3).floatValue());

                List<Double> rightRotationList = data.getDoubleList("TankData." + tankName + "." + i + ".Transform.RightRotation");
                Quaternionf rightRotation = new Quaternionf(rightRotationList.get(0).floatValue(), rightRotationList.get(1).floatValue(), rightRotationList.get(2).floatValue(), rightRotationList.get(3).floatValue());

                // 从 List 加载 Scale (Vector)
                List<Double> scaleList = data.getDoubleList("TankData." + tankName + "." + i + ".Transform.Scale");
                Vector3f scale = new Vector3f(scaleList.get(0).floatValue(), scaleList.get(1).floatValue(), scaleList.get(2).floatValue());

                // 创建 Transformation 对象并应用
                Transformation transformation = new Transformation(translation, leftRotation, scale, rightRotation);
                display.setTransformation(transformation);

                display.addScoreboardTag("Tank_Material");
                if(isTankHead) display.addScoreboardTag("Tank_Head");

                /*填空***/
                Objects.requireNonNullElse(lastItemDisplay, tankCore).addPassenger(display);
                lastItemDisplay = display;

                AWAllay.getTankDisplays(tankCore).add(display);
            } else break;
        }

        TextDisplay textDisplay = tankCore.getWorld().spawn(tankCore.getLocation(), TextDisplay.class);
        textDisplay.setLineWidth(50);
        textDisplay.setText(getNameByTeam(tankCore,"§l" + tankName));
        Transformation t = textDisplay.getTransformation();
        t.getScale().set(2.5);
        t.getTranslation().set(0,3.5 - entityHeight,0);
        textDisplay.setTransformation(t);
        textDisplay.addScoreboardTag("Tank_Material");
        textDisplay.setSeeThrough(true);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setDefaultBackground(false);
        AWAllay.getTankDisplays(tankCore).add(textDisplay);
        Objects.requireNonNullElse(lastItemDisplay, tankCore).addPassenger(textDisplay);

    }

    public static boolean isTankArmy(Entity a) {
        for(String s : a.getScoreboardTags()) {
            if(s.contains("_tank")) return true;
        }
        return false;
    }

    public static void showMagicParticle(Location rayStart, Location rayEnd, Particle particle) {
        double distance = rayStart.distance(rayEnd);
        Vector directionNormalized = rayEnd.subtract(rayStart).toVector().normalize();

        for (double i = 0; i < distance; i += 0.25) {
            Vector particleLocation = rayStart.clone().add(directionNormalized.clone().multiply(i)).toVector();
            Objects.requireNonNull(rayStart.getWorld()).spawnParticle(particle, particleLocation.toLocation(rayStart.getWorld()), 1,0,0,0,0);
        }
    }


}
