package org.NoiQing.AllayWar.PvzGame.PVZUtils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.NoiQing.AllayWar.PvzGame.Game.PvzRound;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.Function;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SpawnPlant {
    public static void summonPlant(Player p, String plantType, Location loc) {
        if(!p.getGameMode().equals(GameMode.CREATIVE)) {
            if(!loc.clone().add(0,-1,0).getBlock().getType().equals(Material.GREEN_CONCRETE)) {
                Function.sendPlayerSystemMessage(p,"请把植物放在指定地点！");
                return;
            }
        }
        switch (plantType) {
            case "豌豆射手" -> {
                if(notHaveEnoughSun(p,100,"豌豆射手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("peaShooter");
                z.setCustomName("豌豆射手");
                PVZFunction.summonPlant(z,"豌豆射手",0.54f,false);
                setAllPlayerCD(p,"豌豆射手",7.5);
            }
            case "向日葵" -> {
                if(notHaveEnoughSun(p,50,"向日葵")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("sunFlower");
                z.setCustomName("向日葵");
                PVZFunction.summonPlant(z,"向日葵",0.54f,false);
                PvzEntity.setPlantAttackCD(z,12 * 20);
                setAllPlayerCD(p,"向日葵",7.5);
            }
            case "樱桃炸弹" -> {
                if(notHaveEnoughSun(p,150,"樱桃炸弹")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("cherryBoom");
                z.setCustomName("樱桃炸弹");
                PVZFunction.summonPlant(z,"樱桃炸弹",0.54f,false);
                setAllPlayerCD(p,"樱桃炸弹",50);
            }
            case "坚果墙" -> {
                if(notHaveEnoughSun(p,50,"坚果墙")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("wallNut");
                z.addScoreboardTag("pvz_nut");
                z.setCustomName("坚果墙");
                Function.setEntityHealth(z,267);
                PVZFunction.summonPlant(z,"坚果墙",0.54f,false);
                setAllPlayerCD(p,"坚果墙",30);
            }
            case "高坚果" -> {
                if(notHaveEnoughSun(p,125,"高坚果")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("tallWallNut");
                z.addScoreboardTag("pvz_nut");
                z.setCustomName("高坚果");
                Function.setEntityHealth(z,267 * 2);
                PVZFunction.summonPlant(z,"高坚果",0.54f,false);
                setAllPlayerCD(p,"高坚果",30);
            }
            case "土豆地雷" -> {
                if(notHaveEnoughSun(p,25,"土豆地雷")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,-0.55,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("potatoMine");
                z.setCustomName("土豆地雷");
                PVZFunction.summonPlant(z,"土豆地雷",0.54f,false);
                setAllPlayerCD(p,"土豆地雷",30);
            }
            case "大嘴花" -> {
                if(notHaveEnoughSun(p,150,"大嘴花")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("eaterFlower");
                z.setCustomName("大嘴花");
                PVZFunction.summonPlant(z,"大嘴花",0.54f,false);
                setAllPlayerCD(p,"大嘴花",7.5);
            }
            case "寒冰射手" -> {
                if(notHaveEnoughSun(p,175,"寒冰射手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("icePeaShooter");
                z.setCustomName("寒冰射手");
                PVZFunction.summonPlant(z,"寒冰射手",0.54f,false);
                setAllPlayerCD(p,"寒冰射手",7.5);
            }
            case "双发射手" -> {
                if(notHaveEnoughSun(p,200,"双发射手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("doublePeaShooter");
                z.setCustomName("双发射手");
                PVZFunction.summonPlant(z,"双发射手",0.54f,false);
                setAllPlayerCD(p,"双发射手",7.5);
            }
            case "卷心菜投手" -> {
                if(notHaveEnoughSun(p,100,"卷心菜投手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("cabbagePitcher");
                z.setCustomName("卷心菜投手");
                PVZFunction.summonPlant(z,"卷心菜投手",0.54f,false);
                setAllPlayerCD(p,"卷心菜投手",7.5);
            }

            case "机枪射手" -> {
                if(notHaveEnoughSun(p,450,"机枪投手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("machinePeaShooter");
                z.setCustomName("机枪射手");
                PVZFunction.summonPlant(z,"机枪射手",0.54f,false);
                setAllPlayerCD(p,"机枪射手",50);
            }
            case "玉米投手" -> {
                if(notHaveEnoughSun(p,100,"玉米投手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("cornPitcher");
                z.setCustomName("玉米投手");
                PVZFunction.summonPlant(z,"玉米投手",0.54f,false);
                setAllPlayerCD(p,"玉米投手",7.5);
            }
            case "西瓜投手" -> {
                if(notHaveEnoughSun(p,300,"西瓜投手")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("melonPitcher");
                z.setCustomName("西瓜投手");
                PVZFunction.summonPlant(z,"西瓜投手",0.54f,false);
                setAllPlayerCD(p,"西瓜投手",7.5);
            }
            case "冰冻生菜" -> {
                if(notHaveEnoughSun(p,0,"冰冻生菜")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("icebergLettuce");
                z.addScoreboardTag("pvz_freeze");
                z.setCustomName("冰冻生菜");
                PVZFunction.summonPlant(z,"冰冻生菜",0.54f,false);
                setAllPlayerCD(p,"冰冻生菜",22.5);
            }
            case "寒冰菇" -> {
                if(notHaveEnoughSun(p,75,"寒冰菇")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("iceRoom");
                z.addScoreboardTag("pvz_freeze");
                z.setCustomName("寒冰菇");
                PVZFunction.summonPlant(z,"寒冰菇",0.54f,false);
                setAllPlayerCD(p,"寒冰菇",50);
            }
            case "毁灭菇" -> {
                if(notHaveEnoughSun(p,125,"毁灭菇")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("doomRoom");
                z.setCustomName("毁灭菇");
                PVZFunction.summonPlant(z,"毁灭菇",0.54f,false);
                setAllPlayerCD(p,"毁灭菇",50);
            }
            case "小喷菇" -> {
                if(notHaveEnoughSun(p,0,"小喷菇")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("puffRoom");
                z.setCustomName("小喷菇");
                PVZFunction.summonPlant(z,"小喷菇",0.54f,false);
                setAllPlayerCD(p,"小喷菇",7.5);
            }
            case "胆小菇" -> {
                if(notHaveEnoughSun(p,25,"胆小菇")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("afraidRoom");
                z.setCustomName("胆小菇");
                PVZFunction.summonPlant(z,"胆小菇",0.54f,false);
                setAllPlayerCD(p,"胆小菇",7.5);
            }

            case "阳光菇" -> {
                if(notHaveEnoughSun(p,25,"阳光菇")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("sunRoom");
                z.setCustomName("阳光菇");
                PVZFunction.summonPlant(z,"小阳光菇",0.54f,false);
                setAllPlayerCD(p,"阳光菇",7.5);
                PvzEntity.setPlantAttackCD(z,12 * 20);
            }

            case "大喷菇" -> {
                if(notHaveEnoughSun(p,75,"大喷菇")) return;
                Allay z = spawnPlantCore(p, Allay.class,loc.clone().add(0.5,0,0.5));
                PVZFunction.hidePlantCore(z);
                z.addScoreboardTag("bigPuffRoom");
                z.addScoreboardTag("pvz_piercing");
                z.setCustomName("大喷菇");
                PVZFunction.summonPlant(z,"大喷菇",0.54f,false);
                setAllPlayerCD(p,"大喷菇",7.5);
            }
        }
    }

    private static <T extends Entity> T spawnPlantCore(Player p, Class<T> entityClass, Location loc) {
        T e = p.getWorld().spawn(loc,entityClass);
        QinTeam playerTeam = QinTeams.getEntityTeam(p);
        if(playerTeam != null) {
            playerTeam.addTeamEntities(e);
        } else {
            QinTeam defaultPlantTeam = QinTeams.getQinTeamByName("植物");
            if(defaultPlantTeam != null) defaultPlantTeam.addTeamEntities(e);
        }
        e.addScoreboardTag("pvz_plant");

        return e;
    }
    private static boolean notHaveEnoughSun(Player p, int sunCost, String plant) {
        if(p.getGameMode().equals(GameMode.CREATIVE)) {
            p.getWorld().playSound(p.getLocation(), Sound.BLOCK_GRASS_PLACE,1,1);
            return false;
        }
        if(!PvzEntity.ifPlayerPlantPassCoolDownTime(p,plant)) return true;
        int playerSun = PvzRound.getTotalSun();
        if(playerSun < sunCost) {
            Function.sendPlayerSystemMessage(p,"你没有足够的阳光！");
            return true;
        }
        playerSun -= sunCost;
        PvzRound.setTotalSun(playerSun);
        for(Player player :p.getWorld().getPlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy("§e阳光: " + playerSun));
        }
        p.getWorld().playSound(p.getLocation(),Sound.BLOCK_GRASS_PLACE,1,1);
        return false;
    }


    private static void setAllPlayerCD(Player p, String plant, double cooldownTime) {
        Material type = Function.getMainHandItem(p).getType();
        for(Player player : p.getWorld().getPlayers()) {
            if(!player.getGameMode().equals(GameMode.CREATIVE))
                PvzEntity.setPlayerPlantCoolDownTime(player,plant,cooldownTime,type);
        }
    }
}
