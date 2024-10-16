package org.NoiQing.BukkitRunnable;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinMap;
import org.NoiQing.mainGaming.Game;
import org.NoiQing.util.Function;
import org.NoiQing.util.WeatherDataSave;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Random;

public class WeatherEffectsRunnable extends BukkitRunnable {
    private int delay = 200;
    private final QinKitPVPS plugin;
    private final Game game;
    public WeatherEffectsRunnable(QinKitPVPS plugin){
        this.plugin = plugin;
        this.game = plugin.getGame();
    }
    @Override
    public void run() {
        int mapID = plugin.getRandomMapID();
        QinMap map = game.getMaps().getQinMapByMapID(mapID);
        Location center = map.getCenterLocation();
        double spreadX = map.getSpreadRadius().getX();
        double spreadY = map.getSpreadRadius().getY();
        double spreadZ = map.getSpreadRadius().getZ();
        String currentWeather = WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world"));
        Particle weatherParticle = null;
        int spawnAmount = 0;

        switch (Function.getNameWithoutColor(currentWeather)){
            case "小雨" -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(onlinePlayer.getScoreboardTags().contains("InMixedGame")){
                        onlinePlayer.setSaturatedRegenRate(5);
                        onlinePlayer.setUnsaturatedRegenRate(40);
                    }
                }
                weatherParticle = Particle.FALLING_WATER;
                spawnAmount = 80;
            }

            case "阴天" -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(onlinePlayer.getScoreboardTags().contains("InMixedGame")){
                        onlinePlayer.setWalkSpeed(0.4f);
                    }
                }
                weatherParticle = Particle.CAMPFIRE_COSY_SMOKE;
                spawnAmount = 10;
            }

            case "小雪" -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(onlinePlayer.getScoreboardTags().contains("InMixedGame")){
                        Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(3);
                    }
                }
                weatherParticle = Particle.SNOWFLAKE;
                spawnAmount = 40;
            }

            case "春风" -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(onlinePlayer.getScoreboardTags().contains("InMixedGame")){
                        Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE)).setBaseValue(6);
                    }
                }
                weatherParticle = Particle.TRIAL_SPAWNER_DETECTION_OMINOUS;
                spawnAmount = 40;
            }

            case "梦游" -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(onlinePlayer.getScoreboardTags().contains("InMixedGame")){
                        Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(0.5);
                    }
                }
                weatherParticle = Particle.TRIAL_SPAWNER_DETECTION_OMINOUS;
                spawnAmount = 40;
            }

            case "风尘" -> {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(onlinePlayer.getScoreboardTags().contains("InMixedGame")){
                        Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(1);
                    }
                }
                weatherParticle = Particle.WHITE_ASH;
                spawnAmount = 80;
            }

            case "春雨" -> {
                toHigherFight();
                weatherParticle = Particle.FALLING_WATER;
                spawnAmount = 20;
            }

            case "梅雨" -> {
                toHigherFight();
                weatherParticle = Particle.CHERRY_LEAVES;
                spawnAmount = 10;
            }

            case "鸡尾酒" -> {
                delay++;
                if(delay >= 200){
                    delay = 0;
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if(onlinePlayer.getScoreboardTags().contains("InMixedGame")){
                            Function.addPotionEffect(onlinePlayer,randomPotion(),10*20,1);
                            Function.executeSounds(onlinePlayer, Sound.valueOf("BLOCK_BREWING_STAND_BREW"),1,1);
                        }
                    }
                }
                weatherParticle = Particle.DUST;
                spawnAmount = 60;
            }

            case "TNT雨" -> {
                delay++;
                if(delay >= 100){
                    delay = 0;
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if(onlinePlayer.getScoreboardTags().contains("InMixedGame")){
                            Location newLocation = generateRandomLocation(onlinePlayer.getLocation().clone());
                            TNTPrimed tnt = onlinePlayer.getWorld().spawn(newLocation, TNTPrimed.class);
                            tnt.setFuseTicks(60);
                        }
                    }
                }
                weatherParticle = Particle.SMOKE;
                spawnAmount = 100;
            }

            case "酸雨" ->{
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if(onlinePlayer.getScoreboardTags().contains("InMixedGame")){
                        if(onlinePlayer.getLocation().getBlock().getType().equals(Material.WATER)){
                            if(onlinePlayer.getPotionEffect(PotionEffectType.POISON) == null)
                                onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.POISON,20,2));
                        }
                    }
                }
                weatherParticle = Particle.TOTEM_OF_UNDYING;
                spawnAmount = 40;
            }

            case "台风" -> {
                weatherParticle = Particle.SWEEP_ATTACK;
                spawnAmount = 40;
            }
        }
        if (weatherParticle != null && center.getWorld() != null) {
            if(weatherParticle == Particle.DUST){
                center.getWorld().spawnParticle(weatherParticle,center,spawnAmount,spreadX,spreadY,spreadZ,0,new Particle.DustOptions(Color.fromRGB(255,255,255), 0.5F),true);
            }else{
                center.getWorld().spawnParticle(weatherParticle,center,spawnAmount,spreadX,spreadY,spreadZ,0,null,true);
            }
        }
    }

    private void toHigherFight() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.getScoreboardTags().contains("InMixedGame")){
                if(onlinePlayer.getScoreboardTags().contains("WarFight")){
                    Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(3.6);
                }else{
                    Objects.requireNonNull(onlinePlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(4);
                }
            }
        }
    }

    private Location generateRandomLocation(Location playerLocation) {
        Random random = new Random();
        playerLocation.add(random.nextInt(-5,5),random.nextInt(5,10),random.nextInt(-5,5));
        return playerLocation;
    }

    private PotionEffectType randomPotion(){
        PotionEffectType potionEffectType;
        Random random = new Random();
        int roll = random.nextInt(1000);
        if(roll < 50){
            potionEffectType = PotionEffectType.REGENERATION;
        }else if(roll < 100){
            potionEffectType = PotionEffectType.RESISTANCE;
        }else if(roll < 150){
            potionEffectType = PotionEffectType.ABSORPTION;
        }else if(roll < 200){
            potionEffectType = PotionEffectType.HUNGER;
        }else if(roll < 250){
            potionEffectType = PotionEffectType.SLOWNESS;
        }else if(roll < 300){
            potionEffectType = PotionEffectType.BLINDNESS;
        }else if(roll < 350){
            potionEffectType = PotionEffectType.SPEED;
        }else if(roll < 400){
            potionEffectType = PotionEffectType.NAUSEA;
        }else if(roll < 450){
            potionEffectType = PotionEffectType.INVISIBILITY;
        }else if(roll < 500){
            potionEffectType = PotionEffectType.DARKNESS;
        }else if(roll < 550){
            potionEffectType = PotionEffectType.JUMP_BOOST;
        }else if(roll < 600){
            potionEffectType = PotionEffectType.SLOW_FALLING;
        }else if(roll < 650){
            potionEffectType = PotionEffectType.HASTE;
        }else if(roll < 700){
            potionEffectType = PotionEffectType.INSTANT_HEALTH;
        }else if(roll < 750){
            potionEffectType = PotionEffectType.GLOWING;
        }else if(roll < 800){
            potionEffectType = PotionEffectType.WEAKNESS;
        }else if(roll < 850){
            potionEffectType = PotionEffectType.DOLPHINS_GRACE;
        }else if(roll < 900){
            potionEffectType = PotionEffectType.FIRE_RESISTANCE;
        }else if(roll < 950){
            potionEffectType = PotionEffectType.STRENGTH;
        }else{
            potionEffectType = PotionEffectType.SLOW_FALLING;
        }
        return potionEffectType;
    }

}
