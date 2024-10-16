package org.NoiQing.itemFunction;

import org.NoiQing.api.QinMenu;
import org.NoiQing.util.Configuration;
import org.NoiQing.util.Function;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;

import java.util.*;

public class ItemsFunction {
    public static ItemStack getItemStackFromPath(Configuration config, String path){
        if (!config.contains(path)) return null;

        ItemStack item = new ItemStack(Material.BEDROCK,1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if (config.contains(path + ".Name")) {
            meta.setDisplayName(Function.changeColorCharacters(Objects.requireNonNull(config.getString(path + ".Name"))));
        }
        if (config.contains(path + ".Lore")) {
            meta.setLore(Function.changeColorCharacters(config.getStringList(path + ".Lore")));
        }

        item.setItemMeta(meta);

        if (config.contains(path + ".Material")) {
            String materialValue = config.getString(path + ".Material");
            Material possibleMaterial = null;
            if (materialValue != null) {
                possibleMaterial = Material.getMaterial(materialValue);
            }
            item.setType(Objects.requireNonNullElse(possibleMaterial, Material.BEDROCK));
            if(item.getItemMeta() instanceof CrossbowMeta crossbowMeta && !crossbowMeta.hasChargedProjectiles()){
                if(config.contains(path + ".GlowArrow")){
                    crossbowMeta.addChargedProjectile(new ItemStack(Material.SPECTRAL_ARROW));
                    item.setItemMeta(crossbowMeta);
                }else{
                    crossbowMeta.addChargedProjectile(new ItemStack(Material.ARROW));
                    item.setItemMeta(crossbowMeta);
                }
            }
        }

        if (config.contains(path + ".Amount")) {
            item.setAmount(config.getInt(path + ".Amount"));
        } else {
            item.setAmount(1);
        }

        if (config.contains(path + ".Dye")) {
            dyeItem(item,Function.getColorFromConfig(config,path));
        }

        if (config.contains(path + ".ArmorTrim")) {
            trimArmor(item,Function.getArmorTrimFromConfig(config,path));
        }

        if (config.contains(path + ".Durability")) {
            ItemMeta durabilityMeta = item.getItemMeta();

            if (durabilityMeta instanceof Damageable) {

                ((Damageable) durabilityMeta).setDamage(config.getInt(path + ".Durability"));
                item.setItemMeta(durabilityMeta);

            }
        }

        if (config.contains(path + ".AttackDamageBase") && !config.contains(path + ".AttackSpeedBase")) {
            ItemMeta damageMeta = item.getItemMeta();
            AttributeModifier damageModifier = new AttributeModifier(UUID.randomUUID(), "damageModifier", config.getDouble(path + ".AttackDamageBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            Collection<AttributeModifier> collection = item.getType().getDefaultAttributeModifiers(EquipmentSlot.HAND).get(Attribute.GENERIC_ATTACK_SPEED);
            double attackSpeed = collection.iterator().hasNext() ? collection.iterator().next().getAmount() : 0;
            AttributeModifier damageSpeedModifier = new AttributeModifier(UUID.randomUUID(), "damageSpeedModifier", attackSpeed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            damageMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, damageSpeedModifier);
            damageMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);
            item.setItemMeta(damageMeta);
        }
        if (config.contains(path + ".AttackSpeedBase") && !config.contains(path + ".AttackDamageBase")) {
            ItemMeta speedMeta = item.getItemMeta();
            AttributeModifier speedModifier = new AttributeModifier(UUID.randomUUID(), "speedModifier", config.getDouble(path + ".AttackSpeedBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            Collection<AttributeModifier> collection = item.getType().getDefaultAttributeModifiers(EquipmentSlot.HAND).get(Attribute.GENERIC_ATTACK_DAMAGE);
            double attackDamage = collection.iterator().hasNext() ? collection.iterator().next().getAmount() : 0;
            AttributeModifier damageModifier = new AttributeModifier(UUID.randomUUID(), "damageModifier", attackDamage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            speedMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, speedModifier);
            speedMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);
            item.setItemMeta(speedMeta);
        }
        if(config.contains(path + ".AttackSpeedBase") && config.contains(path + ".AttackDamageBase")) {
            ItemMeta attributeMeta = item.getItemMeta();
            AttributeModifier damageModifier = new AttributeModifier(UUID.randomUUID(), "damageModifier", config.getDouble(path + ".AttackDamageBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            AttributeModifier speedModifier = new AttributeModifier(UUID.randomUUID(), "speedModifier", config.getDouble(path + ".AttackSpeedBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            attributeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, speedModifier);
            attributeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);
            item.setItemMeta(attributeMeta);
        }

        if (config.contains(path + ".ArmorDefenseBase")) {
            if(item.getType().name().endsWith("_HELMET")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorModifier", config.getDouble(path + ".ArmorDefenseBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
                armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorModifier);
                item.setItemMeta(armorMeta);
            }else if(item.getType().name().endsWith("_CHESTPLATE")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorModifier", config.getDouble(path + ".ArmorDefenseBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
                armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorModifier);
                item.setItemMeta(armorMeta);
            }else if(item.getType().name().endsWith("_LEGGINGS")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorModifier", config.getDouble(path + ".ArmorDefenseBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
                armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorModifier);
                item.setItemMeta(armorMeta);
            }else if(item.getType().name().endsWith("_BOOTS")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorModifier", config.getDouble(path + ".ArmorDefenseBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
                armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, armorModifier);
                item.setItemMeta(armorMeta);
            }
        }

        if (config.contains(path + ".ArmorKnockBackResistBase")) {
            if(item.getType().name().endsWith("_HELMET")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorKnockModifier", config.getDouble(path + ".ArmorKnockBackResistBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
                armorMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, armorModifier);
                item.setItemMeta(armorMeta);
            }else if(item.getType().name().endsWith("_CHESTPLATE")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorKnockModifier", config.getDouble(path + ".ArmorKnockBackResistBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
                armorMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, armorModifier);
                item.setItemMeta(armorMeta);
            }else if(item.getType().name().endsWith("_LEGGINGS")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorKnockModifier", config.getDouble(path + ".ArmorKnockBackResistBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
                armorMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, armorModifier);
                item.setItemMeta(armorMeta);
            }else if(item.getType().name().endsWith("_BOOTS")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorKnockModifier", config.getDouble(path + ".ArmorKnockBackResistBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
                armorMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, armorModifier);
                item.setItemMeta(armorMeta);
            }
        }

        if (config.contains(path + ".ArmorToughnessBase")) {
            if(item.getType().name().endsWith("_HELMET")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorToughnessModifier", config.getDouble(path + ".ArmorToughnessBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD);
                armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, armorModifier);
                item.setItemMeta(armorMeta);
            }else if(item.getType().name().endsWith("_CHESTPLATE")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorToughnessModifier", config.getDouble(path + ".ArmorToughnessBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST);
                armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, armorModifier);
                item.setItemMeta(armorMeta);
            }else if(item.getType().name().endsWith("_LEGGINGS")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorToughnessModifier", config.getDouble(path + ".ArmorToughnessBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS);
                armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, armorModifier);
                item.setItemMeta(armorMeta);
            }else if(item.getType().name().endsWith("_BOOTS")){
                ItemMeta armorMeta = item.getItemMeta();
                AttributeModifier armorModifier = new AttributeModifier(UUID.randomUUID(), "armorToughnessModifier", config.getDouble(path + ".ArmorToughnessBase"), AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
                armorMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, armorModifier);
                item.setItemMeta(armorMeta);
            }
        }

        if(!config.contains(path + ".Unbreakable") || config.getBoolean(path + ".Unbreakable")) {
            ItemMeta durabilityMeta = item.getItemMeta();
            if (durabilityMeta instanceof Damageable) {
                setUnbreakable(item);
            }
        }

        enchantItem(config, path, item);

        if(item.getType() == Material.ARROW || item.getType().equals(Material.SPECTRAL_ARROW)){
            setBreakable(item);
        }

        return item;
    }

    public static QinMenu.MenuItem getMenuItemFromPath(Configuration config, String path) {
        List<String> leftCommands = config.getStringList(path + ".LeftClickCommands");
        List<String> rightCommands = config.getStringList(path + ".RightClickCommands");
        List<String> requirements = config.getStringList(path+ ".Requirements");
        ItemStack item = new ItemStack(Material.BEDROCK,1);
        ItemStack alternativeItem = new ItemStack(Material.BEDROCK,1);
        List<String> leftCommandsATL = config.getStringList(path + ".Else.LeftClickCommands");
        List<String> rightCommandsATL = config.getStringList(path + ".Else.RightClickCommands");

        String itemMaterial = config.getString(path + ".Material");
        String alternativeItemMaterial = config.getString(path + ".Else.Material");
        if(itemMaterial != null) {
            itemMaterial = itemMaterial.toUpperCase();
            item.setType(Objects.requireNonNullElse(Material.getMaterial(itemMaterial),Material.BEDROCK));
        }
        if(alternativeItemMaterial != null) {
            alternativeItemMaterial = alternativeItemMaterial.toUpperCase();
            alternativeItem.setType(Objects.requireNonNullElse(Material.getMaterial(alternativeItemMaterial),Material.BEDROCK));
        }

        ItemMeta meta = item.getItemMeta(); assert meta != null;
        ItemMeta alterMeta = alternativeItem.getItemMeta(); assert alterMeta != null;
        if(config.contains(path + ".DisplayName")) meta.setDisplayName(Function.changeColorCharacters(config.getString(path + ".DisplayName")));
        if(config.contains(path + ".Lore")) meta.setLore(Function.changeColorCharacters(config.getStringList(path + ".Lore")));
        if(config.contains(path + ".Else.DisplayName")) alterMeta.setDisplayName(Function.changeColorCharacters(config.getString(path + ".Else.DisplayName")));
        if(config.contains(path + ".Else.Lore")) alterMeta.setLore(Function.changeColorCharacters(config.getStringList(path + ".Else.Lore")));
        item.setItemMeta(meta);
        alternativeItem.setItemMeta(alterMeta);
        enchantItem(config, path, item);
        enchantItem(config,path+ ".Else",alternativeItem);

        return new QinMenu.MenuItem(item,leftCommands,rightCommands,requirements,
                new QinMenu.MenuItem(alternativeItem,leftCommandsATL,rightCommandsATL,null,null));
    }

    private static void enchantItem(Configuration config, String path, ItemStack item) {
        if (config.contains(path + ".Enchantments")) {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            ConfigurationSection section = config.getConfigurationSection(path + ".Enchantments");

            if (section != null) {
                for (String enchantmentName : section.getKeys(false)) {
                    Enchantment enchantment = Enchantment.PROTECTION;
                    Enchantment enchantmentFromConfig = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantmentName.toLowerCase()));
                    if (enchantmentFromConfig != null) {
                        enchantment = enchantmentFromConfig;
                    }
                    int amplifier = config.getInt(path + ".Enchantments." + enchantmentName);

                    enchantments.put(enchantment, amplifier != 0 ? amplifier : 1);
                }
            }

            item.addUnsafeEnchantments(enchantments);
        }
    }


    private static void dyeItem(ItemStack item, Color color) {
        if (item.getType() == Material.LEATHER_HELMET ||
                item.getType() == Material.LEATHER_CHESTPLATE ||
                item.getType() == Material.LEATHER_LEGGINGS ||
                item.getType() == Material.LEATHER_BOOTS) {

            LeatherArmorMeta dyedMeta = (LeatherArmorMeta) item.getItemMeta();
            if (dyedMeta != null) {
                dyedMeta.setColor(color);
            }
            item.setItemMeta(dyedMeta);
        }
    }
    private static void trimArmor(ItemStack item, ArmorTrim trim) {
        ArmorMeta armorMeta = (ArmorMeta) item.getItemMeta();
        if(armorMeta != null) {
            armorMeta.setTrim(trim);
        }
        item.setItemMeta(armorMeta);
    }
    public static void setUnbreakable(ItemStack... items){
        for(ItemStack item : items) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setUnbreakable(true);
            }
            item.setItemMeta(itemMeta);
        }
    }

    private static void setBreakable(ItemStack item){
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setUnbreakable(false);
        }
        item.setItemMeta(itemMeta);
    }
}
