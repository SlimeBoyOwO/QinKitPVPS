package org.NoiQing.EventListener.System;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Objects;

public class PlayerKillPlayerListener implements Listener {
    private final QinKitPVPS plugin;
    public PlayerKillPlayerListener(QinKitPVPS plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKillPlayer(PlayerDeathEvent event){
        Player victim = event.getEntity();
        Player killer = PlayerDataSave.getLastAttackPlayerRecord(victim);
        if (killer != null) {

            //玩家击杀物资奖励系统
            if(!killer.isInvulnerable()){
                addPlayerItems(killer,Material.COOKED_BEEF,"NONE",4);

                if(!killer.getScoreboardTags().contains("Clls")){
                    if(killer.getScoreboardTags().contains("SunSender")){
                        addPlayerItems(killer,Material.SPECTRAL_ARROW,"NONE",16);
                    }else{
                        addPlayerItems(killer,Material.ARROW,"NONE",16);
                    }
                }

                addPlayerItems(killer,Material.GREEN_GLAZED_TERRACOTTA,"§a医疗包",1);
                PlayerDataSave.setPlayerKillStreaks(killer,"KillStreaks", (PlayerDataSave.getPlayerKillStreaks(killer,"KillStreaks") + 1L));
            }

            //玩家击杀特效系统
            showPlayerKillEffect(victim,killer);

            //连杀提示系统
            if(PlayerDataSave.getPlayerKillStreaks(killer,"KillStreaks") == 3
                    ||PlayerDataSave.getPlayerKillStreaks(killer,"KillStreaks") == 5
                    ||PlayerDataSave.getPlayerKillStreaks(killer,"KillStreaks") == 8
                    ||PlayerDataSave.getPlayerKillStreaks(killer,"KillStreaks") >= 10){
                for(Player allPlayers : Objects.requireNonNull(Bukkit.getWorld("world")).getPlayers()){
                    allPlayers.sendMessage("§7[§b§lQinKitPVPS§7] §3有一位叫 §b" +killer.getName()+ " §3的玩家已经完成了 §b" + PlayerDataSave.getPlayerKillStreaks(killer,"KillStreaks") + " §3连杀！");
                }
            }

            //连杀奖励机制
            if(PlayerDataSave.getPlayerKillStreaks(killer,"KillStreaks") > 1){
                int killStreaks = (int) PlayerDataSave.getPlayerKillStreaks(killer,"KillStreaks");
                addPlayerMoney(killer,5);
                addPlayerExp(killer,2 * 7);
                switch (killStreaks){
                    case 3->{
                        addPlayerMoney(killer,10);
                        addPlayerExp(killer,5 * 7);
                    }
                    case 5->{
                        addPlayerMoney(killer,25);
                        addPlayerExp(killer,7 * 7);
                    }
                    case 8->{
                        addPlayerMoney(killer,50);
                        addPlayerExp(killer,10 * 7);
                    }
                    case 10->{
                        addPlayerMoney(killer,100);
                        addPlayerExp(killer,15 * 7);
                    }
                }
            }

            addPlayerMoney(killer,20);
            addPlayerExp(killer,5 * 7);
        }

        PlayerDataSave.setPlayerKillStreaks(victim,"KillStreaks",0);
    }

    private void showPlayerKillEffect(Player victim, Player killer) {
        String effect = PlayerDataSave.getPlayerTextDataRecord(killer,"killEffects");
        if(effect == null || effect.equals("None")) return;
        showKillEffect(victim, effect);
    }

    private void showKillEffect(Player victim, String effect) {
        switch (effect){
            case "Fire" -> victim.getWorld().spawnParticle(Particle.FLAME,victim.getLocation().clone().add(0,1,0),50,0.2,0.2,0.2,0.5);
            case "Ice" -> victim.getWorld().spawnParticle(Particle.SNOWFLAKE,victim.getLocation().clone().add(0,1,0),50,0.2,0.2,0.2,0.5);
            case "Explosion" -> victim.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,victim.getLocation().clone().add(0,1,0),50,2,2,2,0.5);
            default -> {}
        }
    }

    //设置最近一次攻击的玩家的记录
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageByEntityEvent event){
        if(event.getDamager().equals(event.getEntity())) return;
        if(event.getDamager() instanceof Player damager && event.getEntity() instanceof Player victim){
            if(damager.getScoreboardTags().contains("ShowDamage")) {
                String damage = String.format("%.2f",event.getFinalDamage());
                damager.sendTitle("","                        §7造成了 §b" + damage + " §7伤害",1,10,0);
            }
            PlayerDataSave.setLastAttackPlayerRecord(victim,damager);
        }else if(event.getDamager() instanceof  Arrow arrow){
            if(arrow.getShooter() instanceof Player damager && event.getEntity() instanceof Player victim){
                if(damager.getScoreboardTags().contains("ShowDamage")) {
                    String damage = String.format("%.2f",event.getFinalDamage());
                    damager.sendTitle("","                        §7造成了 §b" + damage + " §7伤害",1,10,0);
                }
                PlayerDataSave.setLastAttackPlayerRecord(victim,damager);
            }
        }else if(event.getDamager() instanceof LivingEntity entity && event.getEntity() instanceof Player victim){
            if(Bukkit.getPlayer(entity.getName()) != null){
                PlayerDataSave.setLastAttackPlayerRecord(victim,Bukkit.getPlayer(entity.getName()));
            }
        }
    }

    private void addPlayerItems(Player player, Material material, String customName, int amount){
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if(!customName.equals("NONE")){
                meta.setDisplayName(customName);
            }
            meta.setUnbreakable(true);
            if(material.equals(Material.ARROW) || material.equals(Material.SPECTRAL_ARROW)){
                meta.setUnbreakable(false);
            }
        }
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }

    private void addPlayerMoney(Player player, int addMoney){
        try{
            //更新玩家的钱
            int money = plugin.getSQLiteDatabase().getPlayerMoney(player);
            money += addMoney;
            plugin.getSQLiteDatabase().updatePlayerMoney(player,money);
            PlayerDataSave.updatePlayerMoneyRecord(player, money);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void addPlayerExp(Player player, int addExp){
        try{
            //更新玩家的经验
            int exp = plugin.getMySQLDataBase().getPlayerExp(player);
            exp += addExp;
            plugin.getMySQLDataBase().updatePlayerExp(player,exp);
            PlayerDataSave.updatePlayerExpRecord(player,exp);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
