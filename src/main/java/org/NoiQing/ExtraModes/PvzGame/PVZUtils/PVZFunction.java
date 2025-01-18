package org.NoiQing.ExtraModes.PvzGame.PVZUtils;

import org.NoiQing.ExtraModes.PvzGame.Game.PvzRound;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

public class PVZFunction {
    public static void summonPlant(Entity plantCore, String plantName, float entityHeight, boolean isBullet) {
        Configuration data = QinKitPVPS.getPlugin().getResource().getPvzData();
        if(!data.contains("PlantData." + plantName)) {
            Bukkit.broadcastMessage("植物" + plantName + "不存在");
            return;
        }

        ItemDisplay lastItemDisplay = null;
        if(isBullet) plantCore.addScoreboardTag("plant_bullet");

        for(int i = 0; i < 100; i++) {
            if(data.contains("PlantData." + plantName + "." + i + ".Item")) {
                ItemStack item = data.getItemStack("PlantData." + plantName + "." + i + ".Item");
                ItemDisplay.ItemDisplayTransform transform = ItemDisplay.ItemDisplayTransform.valueOf(data.getString("PlantData." + plantName + "." + i + ".ItemDisplay"));
                boolean isPlantHead = data.getBoolean("PlantData." + plantName + "." + i + ".IsPlantHead");
                ItemDisplay display = plantCore.getWorld().spawn(plantCore.getLocation(), ItemDisplay.class);
                display.setItemStack(item);
                display.setItemDisplayTransform(transform);
                /*填空***/
                // 从 List 加载 Translation (Vector)
                List<Double> translationList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.Translation");
                Vector3f translation = new Vector3f(translationList.get(0).floatValue(), translationList.get(1).floatValue() - entityHeight, translationList.get(2).floatValue());

                // 从 List 加载 LeftRotation 和 RightRotation (Quaternion)
                List<Double> leftRotationList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.LeftRotation");
                Quaternionf leftRotation = new Quaternionf(leftRotationList.get(0).floatValue(), leftRotationList.get(1).floatValue(), leftRotationList.get(2).floatValue(), leftRotationList.get(3).floatValue());

                List<Double> rightRotationList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.RightRotation");
                Quaternionf rightRotation = new Quaternionf(rightRotationList.get(0).floatValue(), rightRotationList.get(1).floatValue(), rightRotationList.get(2).floatValue(), rightRotationList.get(3).floatValue());

                // 从 List 加载 Scale (Vector)
                List<Double> scaleList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.Scale");
                Vector3f scale = new Vector3f(scaleList.get(0).floatValue(), scaleList.get(1).floatValue(), scaleList.get(2).floatValue());

                // 创建 Transformation 对象并应用
                Transformation transformation = new Transformation(translation, leftRotation, scale, rightRotation);
                display.setTransformation(transformation);

                if(isBullet) display.addScoreboardTag("plant_bullet");
                display.addScoreboardTag("Plant_Material");
                if(isPlantHead) display.addScoreboardTag("Plant_Head");

                /*填空***/
                Objects.requireNonNullElse(lastItemDisplay, plantCore).addPassenger(display);
                lastItemDisplay = display;

                PvzEntity.getPlantDisplays(plantCore).add(display);
            } else break;
        }
    }

    public static void changePlant(Entity core, String newPlant, float entityHeight) {
        PvzEntity.getPlantDisplays(core).forEach(Entity::remove);
        PvzEntity.removePlantDisplays(core);
        PVZFunction.summonPlant(core,newPlant,entityHeight,false);
    }

    public static void zombieDreamfiy(Entity plant, Entity zombie) {
        if(zombie == null) return;
        if(!zombie.getScoreboardTags().contains("pvz_zombie")) return;
        if(zombie.getScoreboardTags().contains("pvz_grave")) return;
        zombie.addScoreboardTag("pvz_dreamed");
        QinTeams.leaveTeam(zombie);
        QinTeam team = QinTeams.getEntityTeam(plant);
        if(team != null) team.addTeamEntities(zombie);
        if(zombie instanceof Mob lv) lv.setTarget(null);
        PvzRound.addZombieOffSet();
    }

    public static void summonEffect(Entity effectedMob, String plantName, float entityHeight) {
        Configuration data = QinKitPVPS.getPlugin().getResource().getPvzData();
        if(!data.contains("PlantData." + plantName)) {
            Bukkit.broadcastMessage("植物" + plantName + "不存在");
            return;
        }

        ItemDisplay lastItemDisplay = null;

        List<Display> displays = PvzEntity.getPlantDisplays(effectedMob);
        if(!displays.isEmpty()) {
            for(Display display : displays)
                effectedMob.removePassenger(display);
        }


        for(int i = 0; i < 100; i++) {
            if(data.contains("PlantData." + plantName + "." + i + ".Item")) {
                ItemStack item = data.getItemStack("PlantData." + plantName + "." + i + ".Item");
                ItemDisplay.ItemDisplayTransform transform = ItemDisplay.ItemDisplayTransform.valueOf(data.getString("PlantData." + plantName + "." + i + ".ItemDisplay"));
                boolean isPlantHead = data.getBoolean("PlantData." + plantName + "." + i + ".IsPlantHead");
                ItemDisplay display = effectedMob.getWorld().spawn(effectedMob.getLocation(), ItemDisplay.class);
                display.setItemStack(item);
                display.setItemDisplayTransform(transform);
                /*填空***/
                // 从 List 加载 Translation (Vector)
                List<Double> translationList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.Translation");
                Vector3f translation = new Vector3f(translationList.get(0).floatValue(), translationList.get(1).floatValue() - entityHeight, translationList.get(2).floatValue());

                // 从 List 加载 LeftRotation 和 RightRotation (Quaternion)
                List<Double> leftRotationList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.LeftRotation");
                Quaternionf leftRotation = new Quaternionf(leftRotationList.get(0).floatValue(), leftRotationList.get(1).floatValue(), leftRotationList.get(2).floatValue(), leftRotationList.get(3).floatValue());

                List<Double> rightRotationList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.RightRotation");
                Quaternionf rightRotation = new Quaternionf(rightRotationList.get(0).floatValue(), rightRotationList.get(1).floatValue(), rightRotationList.get(2).floatValue(), rightRotationList.get(3).floatValue());

                // 从 List 加载 Scale (Vector)
                List<Double> scaleList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.Scale");
                Vector3f scale = new Vector3f(scaleList.get(0).floatValue(), scaleList.get(1).floatValue(), scaleList.get(2).floatValue());

                // 创建 Transformation 对象并应用
                Transformation transformation = new Transformation(translation, leftRotation, scale, rightRotation);
                display.setTransformation(transformation);

                display.addScoreboardTag("Plant_Material");
                if(isPlantHead) display.addScoreboardTag("Plant_Head");

                /*填空***/
                Objects.requireNonNullElse(lastItemDisplay, effectedMob).addPassenger(display);
                lastItemDisplay = display;

                PvzEntity.getEffectDisplays(effectedMob).add(display);
            } else break;
        }

        if(displays.isEmpty()) return;
        if (lastItemDisplay != null) {
            lastItemDisplay.addPassenger(displays.getFirst());
        }
    }

    public static void hidePlantCore(LivingEntity z) {
        Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(0);
        Objects.requireNonNull(z.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1000);
        z.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,9999999,244,false,false,true));
        z.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999999, 0, false, false, true));
        z.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 9999999, 50, false, false, true));
        z.setSilent(true);
        z.setAI(false);
        NamespacedKey key = new NamespacedKey(QinKitPVPS.getPlugin(), "persistentEntity");
        z.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        z.setPersistent(true);
        if(z instanceof Zombie zm) zm.setAdult();
        if(z.getEquipment() != null) z.getEquipment().clear();
    }

    public static boolean isBullet(Entity e) {
        for(String str : e.getScoreboardTags()) {
            if(str.contains("bullet")) return true;
        }
        return false;
    }

    public static void summonExtra(Entity extraMob, String plantName, float entityHeight) {
        Configuration data = QinKitPVPS.getPlugin().getResource().getPvzData();
        if(!data.contains("PlantData." + plantName)) {
            Bukkit.broadcastMessage("植物" + plantName + "不存在");
            return;
        }

        ItemDisplay lastItemDisplay = null;

        List<Display> displays = PvzEntity.getPlantDisplays(extraMob);
        displays.addAll(PvzEntity.getEffectDisplays(extraMob));

        if(!displays.isEmpty()) {
            for(Display display : displays)
                extraMob.removePassenger(display);
        }

        for(int i = 0; i < 100; i++) {
            if(data.contains("PlantData." + plantName + "." + i + ".Item")) {
                ItemStack item = data.getItemStack("PlantData." + plantName + "." + i + ".Item");
                ItemDisplay.ItemDisplayTransform transform = ItemDisplay.ItemDisplayTransform.valueOf(data.getString("PlantData." + plantName + "." + i + ".ItemDisplay"));
                boolean isPlantHead = data.getBoolean("PlantData." + plantName + "." + i + ".IsPlantHead");
                ItemDisplay display = extraMob.getWorld().spawn(extraMob.getLocation(), ItemDisplay.class);
                display.setItemStack(item);
                display.setItemDisplayTransform(transform);
                /*填空***/
                // 从 List 加载 Translation (Vector)
                List<Double> translationList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.Translation");
                Vector3f translation = new Vector3f(translationList.get(0).floatValue(), translationList.get(1).floatValue() - entityHeight, translationList.get(2).floatValue());

                // 从 List 加载 LeftRotation 和 RightRotation (Quaternion)
                List<Double> leftRotationList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.LeftRotation");
                Quaternionf leftRotation = new Quaternionf(leftRotationList.get(0).floatValue(), leftRotationList.get(1).floatValue(), leftRotationList.get(2).floatValue(), leftRotationList.get(3).floatValue());

                List<Double> rightRotationList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.RightRotation");
                Quaternionf rightRotation = new Quaternionf(rightRotationList.get(0).floatValue(), rightRotationList.get(1).floatValue(), rightRotationList.get(2).floatValue(), rightRotationList.get(3).floatValue());

                // 从 List 加载 Scale (Vector)
                List<Double> scaleList = data.getDoubleList("PlantData." + plantName + "." + i + ".Transform.Scale");
                Vector3f scale = new Vector3f(scaleList.get(0).floatValue(), scaleList.get(1).floatValue(), scaleList.get(2).floatValue());

                // 创建 Transformation 对象并应用
                Transformation transformation = new Transformation(translation, leftRotation, scale, rightRotation);
                display.setTransformation(transformation);

                display.addScoreboardTag("Plant_Material");
                if(isPlantHead) display.addScoreboardTag("Plant_Head");

                /*填空***/
                Objects.requireNonNullElse(lastItemDisplay, extraMob).addPassenger(display);
                lastItemDisplay = display;

                PvzEntity.getExtraDisplays(extraMob).add(display);
            } else break;
        }

        if(displays.isEmpty()) return;
        if (lastItemDisplay != null) {
            lastItemDisplay.addPassenger(displays.getFirst());
        }
    }
}
