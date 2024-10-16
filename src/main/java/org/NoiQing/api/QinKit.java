package org.NoiQing.api;

import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QinKit {
    private final String name;
    private String displayName = "木有名字";
    private boolean available;
    private String permission;
    private int price;
    private int health;
    private boolean newAttack;
    private final Map<Integer, ItemStack> inventory;
    private ItemStack kitHelmet;
    private ItemStack kitChestplate;
    private ItemStack kitLeggings;
    private ItemStack kitBoots;
    private ItemStack offhand;
    private List<String> commands;
    private List<String> introduction;
    private final List<PotionEffect> effects;
    public QinKit(String name){
        this.name = name;

        this.inventory = new HashMap<>();
        this.effects = new ArrayList<>();
    }
    public void setPermission(String permission) {this.permission = permission;}
    public void setDisplayName(String displayName){this.displayName = displayName;}
    public void setAvailable(boolean available){this.available = available;}
    public void setPrice(int price) {this.price = price;}
    public void setHealth(int health) {
        this.health = health;
    }
    public void setNewAttack(boolean newAttack) {this.newAttack = newAttack; }
    public void setInventoryItem(int slot, ItemStack item) {
        inventory.put(slot, item);
    }
    public void setHelmet(ItemStack helmet) {
        this.kitHelmet = helmet;
    }

    public void setChestplate(ItemStack chestplate) {
        this.kitChestplate = chestplate;
    }

    public void setLeggings(ItemStack leggings) {
        this.kitLeggings = leggings;
    }

    public void setBoots(ItemStack boots) {
        this.kitBoots = boots;
    }

    public void setOffhand(ItemStack offhand) {
        this.offhand = offhand;
    }
    public void setCommands(List<String> commands) {this.commands = commands;}
    public void setIntroduction(List<String> introduction) {
        if(introduction != null){
            List<String> newIntroduction = new ArrayList<>();
            for (String string : introduction) {
                newIntroduction.add(string.replaceAll("&","§"));
            }
            this.introduction = newIntroduction;
        }else{
            this.introduction = null;
        }
    }
    public void setEffect(PotionEffectType type, int amplifier, int durationSeconds) {
        PotionEffect effect = new PotionEffect(type, durationSeconds * 20, amplifier - 1);
        effects.add(effect);
    }

    public void apply(Player player) {
        PlayerDataSave.setPlayerKitRecord(player,displayName);

        player.getInventory().setHelmet(kitHelmet);
        player.getInventory().setChestplate(kitChestplate);
        player.getInventory().setLeggings(kitLeggings);
        player.getInventory().setBoots(kitBoots);

        if(player.getScoreboardTags().contains("InTeamedGame") || player.getScoreboardTags().contains("InTeamedGaming")){
            Function.setPlayerMaxHealth(player, health * 2);
        }else{
            Function.setPlayerMaxHealth(player, health * 2);
        }
        player.setHealth(Function.getPlayerMaxHealth(player));
        AttributeInstance attributeInstance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attributeInstance != null) {
            if(newAttack){
                attributeInstance.setBaseValue(4);
                player.addScoreboardTag("WarFight");
            }else{
                attributeInstance.setBaseValue(100);
            }
        }

        for (int i = 0; i < 36; i++) {
            player.getInventory().setItem(i, inventory.get(i));
        }

        player.getInventory().setItemInOffHand(offhand);
        player.addScoreboardTag("Kitted");

        Function.executeCommands(player,commands,"none","none");

        player.getActivePotionEffects().clear();
        Function.executeEffects(player,effects);
    }

    public void showIntroduction(Player player){
        if(introduction != null){
            Function.showTextsToPlayer(player,introduction);
        }else{
            player.sendMessage("§7> > §b§lQinKitPVPS §7--> §3啊嘞，这个职业的作者并没有写介绍呢 §bΣσ(・A・;)");
        }
    }

    public String getName() {return name;}
    public String getDisplayName() {return displayName;}
    public boolean getAvailable() {return available;}
    public String getPermission() {return permission;}
    public int getPrice() {return price;}
}
