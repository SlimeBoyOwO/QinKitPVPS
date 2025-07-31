package org.NoiQing.util;

import org.NoiQing.ExtraModes.AllayWar.AWAPI.AllayGame;
import org.NoiQing.ExtraModes.AllayWar.AWUtils.AWPlayer;
import org.NoiQing.ExtraModes.PvzGame.Game.PvzRound;
import org.NoiQing.ExtraModes.PvzGame.PVZUtils.PvzEntity;
import org.NoiQing.BukkitRunnable.PluginScoreboard;
import org.NoiQing.EventListener.GuiListeners.CustomMenuListeners;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinTeam;
import org.NoiQing.commands.CompleteCommands;
import org.NoiQing.util.itemFunction.ItemsFunction;
import org.NoiQing.mainGaming.QinTeams;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Lantern;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Wall;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;

public class Function {
    /* 获 取 方 法 */
    public static ItemStack getMainHandItem(Player player){
        return player.getInventory().getItemInMainHand();
    }

    public static ItemStack getOffHandItem(Player player){
        return player.getInventory().getItemInOffHand();
    }
    public static String getMainHandItemNameWithoutColor(Player player) {
        if (player.getInventory().getItemInMainHand().getItemMeta() == null) return "";
        return Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().replaceAll("§.","");
    }
    public static String getOffHandItemNameWithoutColor(Player player) {
        return Objects.requireNonNull(player.getInventory().getItemInOffHand().getItemMeta()).getDisplayName().replaceAll("§.","");
    }
    public static double getPlayerMaxHealth(Player player){
        return Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
    }
    public static String getItemNameWithoutColor(ItemStack item){
        return Objects.requireNonNull(item.getItemMeta()).getDisplayName().replaceAll("§.","");
    }
    public static String spreadString(String str, Boolean ifBold) {
        if(ifBold) str = boldString(str);
        StringBuilder stringBuilder = new StringBuilder(str);
        for(int i = str.length() - 1; i > 0; i--) {
            while(i - 2 >= 0 && stringBuilder.charAt(i - 2) == '§') i -= 2;
            if(i > 0) stringBuilder.insert(i," ");
        }
        return stringBuilder.toString();
    }
    public static String boldString(String str) {
        StringBuilder stringBuilder = new StringBuilder(str);
        for(int i = 0; i < str.length(); i++) {
            while(i + 2 < stringBuilder.length() && stringBuilder.charAt(i) == '§') i += 2;
            if(stringBuilder.charAt(i - 2) == '§') {
                stringBuilder.insert(i,"§l");
                i += 2;
            }
        }
        return stringBuilder.toString();
    }
    public static String getNameWithoutColor(String s){
        if(s == null) return "";
        return s.replaceAll("§.","").replaceAll("&.","");
    }
    public static String getNameRemovedNotes(String s){
        return s.replaceAll("#.*?#","");
    }
    public static boolean isBlockHasTag(Block b, String tag) {return b.hasMetadata(tag);}
    public static Color getColorFromConfig(Configuration config, String path) {
        return Color.fromRGB(config.getInt(path + ".Dye.Red"),
                config.getInt(path + ".Dye.Green"),
                config.getInt(path + ".Dye.Blue"));
    }
    public static int getPlayerItemAmount(Player player, Material material){
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }

    public static int getPlayerItemAmountWithName(Player player, String displayName){
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && Function.getItemNameWithoutColor(item).equals(Function.getNameWithoutColor(displayName))) {
                count += item.getAmount();
            }
        }
        return count;
    }
    public static void addPotionEffect(Player player, PotionEffectType type, int duration, int amplifier){
        List<PotionEffect> potionEffectList = new ArrayList<>();
        PotionEffect effect1 = new PotionEffect(type, duration, amplifier);
        potionEffectList.add(effect1);
        Function.executeEffects(player, potionEffectList);
    }
    public static ArmorTrim getArmorTrimFromConfig(Configuration config, String path){
        ArmorTrim defaultArmorTrim = new ArmorTrim(TrimMaterial.DIAMOND,TrimPattern.SNOUT);
        TrimMaterial trimMaterial = null;
        TrimPattern trimPattern = null;
        String materialValue = config.getString(path + ".ArmorTrim.Material");
        String trimPatternValue = config.getString(path + ".ArmorTrim.Pattern");
        if (materialValue != null) {
            switch (materialValue) {
                case "AMETHYST_SHARD" -> trimMaterial = TrimMaterial.AMETHYST;
                case "COPPER_INGOT" -> trimMaterial = TrimMaterial.COPPER;
                case "DIAMOND" -> trimMaterial = TrimMaterial.DIAMOND;
                case "EMERALD" -> trimMaterial = TrimMaterial.EMERALD;
                case "GOLD_INGOT" -> trimMaterial = TrimMaterial.GOLD;
                case "IRON_INGOT" -> trimMaterial = TrimMaterial.IRON;
                case "LAPIS_LAZULI" -> trimMaterial = TrimMaterial.LAPIS;
                case "QUARTZ" -> trimMaterial = TrimMaterial.QUARTZ;
                case "NETHERITE_INGOT" -> trimMaterial = TrimMaterial.NETHERITE;
                case "REDSTONE" -> trimMaterial = TrimMaterial.REDSTONE;
                default -> {}
            }
        }
        if (trimPatternValue != null) {
            switch (trimPatternValue) {
                case "DUNE" -> trimPattern = TrimPattern.DUNE;
                case "EYE" -> trimPattern = TrimPattern.EYE;
                case "COAST" -> trimPattern = TrimPattern.COAST;
                case "RIB" -> trimPattern = TrimPattern.RIB;
                case "HOST" -> trimPattern = TrimPattern.HOST;
                case "RAISER" -> trimPattern = TrimPattern.RAISER;
                case "SENTRY" -> trimPattern = TrimPattern.SENTRY;
                case "SHAPER" -> trimPattern = TrimPattern.SHAPER;
                case "SILENCE" -> trimPattern = TrimPattern.SILENCE;
                case "SNOUT" -> trimPattern = TrimPattern.SNOUT;
                case "SPIRE" -> trimPattern = TrimPattern.SPIRE;
                case "TIDE" -> trimPattern = TrimPattern.TIDE;
                case "VEX" -> trimPattern = TrimPattern.VEX;
                case "WARD" -> trimPattern = TrimPattern.WARD;
                case "WAYFINDER" -> trimPattern = TrimPattern.WAYFINDER;
                case "WILD" -> trimPattern = TrimPattern.WILD;
                default -> {}
            }
        }
        if(trimMaterial != null && trimPattern != null){
            return new ArmorTrim(trimMaterial,trimPattern);
        }else{
            return defaultArmorTrim;
        }
    }
    public static String[] getNearestPlayer(Player player, int maxY) {
        String nearest = "player:100000.0";
        for (Player all : Objects.requireNonNull(Bukkit.getWorld(player.getWorld().getName())).getPlayers()) {
            String[] list = nearest.split(":");
            double cal = player.getLocation().distance(all.getLocation());
            if (cal <= Double.parseDouble(list[1]) && all != player) {
                if (all.getLocation().getBlockY() < maxY) {
                    if (all.getGameMode() != GameMode.SPECTATOR) {
                        nearest = all.getName() + ":" + cal;
                    }
                }
            }
        }

        if (nearest.equals("player:100000.0")) {
            return null;
        }

        return nearest.split(":");
    }
    public static Player getPlayer(World world,String name){
        for (Player player : world.getPlayers()) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    /* 设 置 方 法 */
    public static void setMainHandItem(Player player, ItemStack item){
        player.getInventory().setItemInMainHand(item);
    }
    public static void setOffHandItem(Player player, ItemStack item){
        player.getInventory().setItemInOffHand(item);
    }
    public static void setPlayerMaxHealth(Player player, int amount){
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert healthAttribute != null;
        healthAttribute.setBaseValue(amount);
        player.setHealth(amount);
    }

    /* 功 能 方 法 */
    public static double round(double value, int precision) {

        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;

    }
    public static void useSPItem(Player player, String name){
        if(getMainHandItem(player).getType() == Material.AIR){
            getOffHandItem(player).setAmount(getOffHandItem(player).getAmount() - 1);
        }else{
            if(getItemNameWithoutColor(getMainHandItem(player)).equals(name)){
                getMainHandItem(player).setAmount(getMainHandItem(player).getAmount() - 1);
            }else{
                getOffHandItem(player).setAmount(getOffHandItem(player).getAmount() - 1);
            }
        }
    }
    public static void recoverHealth(Player player, double health){
        player.setHealth(Math.min(player.getHealth() + health,Function.getPlayerMaxHealth(player)));
    }
    public static void clearPlayerTeam(Player player){
        Scoreboard sc = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        Team t = sc.getEntryTeam(player.getName());
        if(t != null) t.removeEntry(player.getName());
    }
    public static void playerReset(Player player){
        player.getInventory().clear();
        /* 给 予 玩 家 职 业 菜 单 */
        menuPlayer(player);
        /* 恢 复 玩 家 正 常 属 性 */
        Function.setPlayerMaxHealth(player,20);
        player.setWalkSpeed(0.2f);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(100);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(0);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(1);
        Objects.requireNonNull(player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE)).setBaseValue(3.0);
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(1.0);
        player.setLevel(0);
        player.setInvisible(false);
        /* 删 除 玩 家 所 有 t a g s */
        Object[] objects = player.getScoreboardTags().toArray();
        for (Object object : objects) {
            String s = (String) object;
            ArrayList<String> banTags = new ArrayList<>();
            banTags.add("NoWallJump");
            banTags.add("ShowDamage");
            if(!banTags.contains(s))
                player.removeScoreboardTag(s);
        }
        /* 清 除 玩 家 药 水 效 果 */
        List<String> commands = new ArrayList<>();
        commands.add("console: effect clear %player%");
        Function.executeCommands(player,commands,"none","none");
        /* 给 予 玩 家 无 敌 */
        player.setInvulnerable(true);
        player.setSaturation(99999);
        player.setFoodLevel(99999);
        /*玩 家 击 杀 重 置 */
        PlayerDataSave.clearLastAttackPlayerRecord(player);
        PlayerDataSave.clearPlayerPassiveSkillRecords(player);
        PlayerDataSave.clearPlayerSkillCoolDownTime(player);
        /*重置玩家计分板*/
        PluginScoreboard.changeScoreboard(player);
        /*玩家大厅标志*/
        player.addScoreboardTag(QinConstant.LOBBY_MARK);
    }

    public static boolean banItemsInLobby(Player player, boolean showMessage){
        if(player.getScoreboardTags().contains("QinKitLobby") && player.getGameMode() == GameMode.ADVENTURE){
            if(Function.getMainHandItem(player).getItemMeta() == null) return false;
            if(!Function.getNameWithoutColor(Objects.requireNonNull(Function.getMainHandItem(player).getItemMeta()).getDisplayName()).equals("职业菜单")
                    && !Function.getNameWithoutColor(Objects.requireNonNull(Function.getMainHandItem(player).getItemMeta()).getDisplayName()).equals("职业商店")
                    && !Function.getNameWithoutColor(Objects.requireNonNull(Function.getMainHandItem(player).getItemMeta()).getDisplayName()).equals("抽奖宝箱")
                    && !Function.getNameWithoutColor(Objects.requireNonNull(Function.getMainHandItem(player).getItemMeta()).getDisplayName()).equals("个性化设置")
                    && !Function.getNameWithoutColor(Objects.requireNonNull(Function.getMainHandItem(player).getItemMeta()).getDisplayName()).equals("返回大厅")){
                if(showMessage) player.sendMessage("§b你无法在大厅使用技能！");
                return true;
            }
        }
        return false;
    }
    private static void menuPlayer(Player player) {
        givePlayerItem(player,Material.CLOCK,"§e§l职业菜单",0);
        givePlayerItem(player,Material.ENCHANTING_TABLE,"§a§l职业商店",1);
        givePlayerItem(player,Material.BEACON,"§b§l个性化设置",4);
        int chest = 0;
        try {
            chest = QinKitPVPS.getPlugin().getMySQLDataBase().getPlayerKey(player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        givePlayerItem(player,Material.CHEST,"§d§l抽奖宝箱",7, chest);
        givePlayerItem(player,Material.IRON_DOOR,"§7§l返回大厅",8);
    }

    private static void givePlayerItem(Player player, Material material, String displayName, int location) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
        }
        item.setItemMeta(meta);
        player.getInventory().setItem(location,item);
    }

    private static void givePlayerItem(Player player, Material material, String displayName, int location, int amount) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
        }
        item.setItemMeta(meta);
        item.setAmount(amount);
        player.getInventory().setItem(location,item);
    }

    public static void playerTpLobby(Player player){
        Location location = new Location(Bukkit.getWorld("world"),984.5,7,998.5);
        player.teleport(location);
        Function.executeCommand(player,"console: tp %player% ~ ~ ~ 270 0");
    }

    /**该方法用于改变占位符字符串来改变字体颜色
     * @param str 需要改变颜色的字符串
     */
    public static String changeColorCharacters(String str){
        if(str == null) return "无名字";
        str = gradientColorTranslate(str);
        str = str.replaceAll("&","§");
        String subString;
        for(int i = 0; i < str.length(); i++)
            if(i + 8 < str.length() && str.charAt(i) == '§' && str.charAt(i + 1) == '#') {
                subString = str.substring(i+1,i+8);
                str = str.substring(0,i) + net.md_5.bungee.api.ChatColor.of(subString) + str.substring(i+8);
            }
        return str;
    }
    public static String gradientColorTranslate(String input) {
        String leftColor = "",rightColor = "";
        String text,restTextBefore,restTextAfter;
        int startText = 0,begin = 0;
        int endText = input.length();

        for(int i = 0; i < input.length() - 9; i++) {
            String color = input.substring(i + 2, i + 9);
            if(input.substring(i,i+10).matches(">&#[0-9a-fA-F]{6}>")) {
                leftColor = color;
                startText = i + 10;
                begin = i;
            } else if(input.substring(i,i+10).matches("<&#[0-9a-fA-F]{6}<")) {
                rightColor = color;
                endText = i;
            }
            if(!leftColor.isEmpty() && !rightColor.isEmpty()) break;
        }

        if(leftColor.isEmpty() || rightColor.isEmpty()) return input;

        text = input.substring(startText, endText);
        restTextBefore = input.substring(0, begin);
        restTextAfter = input.substring(endText + 10);

        boolean bold = false, italic = false, underline = false, strikethrough = false, obfuscated = false;
        if(text.contains("&l")) bold = true;
        if(text.contains("&o")) italic = true;
        if(text.contains("&n")) underline = true;
        if(text.contains("&m")) strikethrough = true;
        if(text.contains("&k")) obfuscated = true;
        text = text.replace("&l","");
        text = text.replace("&o","");
        text = text.replace("&n","");
        text = text.replace("&m","");
        text = text.replace("&k","");

        int red1 = Integer.parseInt(leftColor.substring(1, 3), 16);
        int green1 = Integer.parseInt(leftColor.substring(3, 5), 16);
        int blue1 = Integer.parseInt(leftColor.substring(5, 7), 16);

        int red2 = Integer.parseInt(rightColor.substring(1, 3), 16);
        int green2 = Integer.parseInt(rightColor.substring(3, 5), 16);
        int blue2 = Integer.parseInt(rightColor.substring(5, 7), 16);

        StringBuilder newText = new StringBuilder();
        int divide = text.length() - 1;

        for(int i = 0; i < text.length(); i++) {
            int newRed = red1 + (red2 - red1) / divide * i;
            int newGreen = green1 + (green2 - green1) / divide * i;
            int newBlue = blue1 + (blue2 - blue1) / divide * i;
            String newHex = String.format("&#%02X%02X%02X", newRed, newGreen, newBlue);

            newText.append(newHex);
            if(bold) newText.append("&l");
            if(italic) newText.append("&o");
            if(underline) newText.append("&n");
            if(strikethrough) newText.append("&m");
            if(obfuscated) newText.append("&k");
            newText.append(text.charAt(i));
        }

        newText.append("&f");   //重置字体
        String translated = restTextBefore + newText + restTextAfter;
        return gradientColorTranslate(translated);
    }
    public static List<String> changeColorCharacters(List<String> strings) {
        strings.replaceAll(Function::changeColorCharacters);
        return strings;
    }
    public static void executeCommands(Player player, List<String> commands, String replaceFrom, String replaceTo){
        if (commands == null) return;

        for (String commandString : commands) {

            if (commandString.startsWith("[buy]")) {
                playerBuyItem(player, commandString);
                continue;
            }

            else if (commandString.startsWith("[teamWool]")) {
                playerBuyWool(player, commandString);
                continue;
            }

            else if (commandString.startsWith("[team_upgrade]")) {
                playerTeamUpgrade(player, commandString);
                continue;
            }

            else if (commandString.startsWith("[team_sword]")) {
                playerGetTeamSword(player, commandString);
                continue;
            }

            else if (commandString.startsWith("[team_armor]")) {
                playerGetTeamArmor(player, commandString);
                continue;
            }

            else if (commandString.startsWith("[openMenu]")) {
                playerOpenMenu(player, commandString);
                continue;
            }

            else if(commandString.equals("[close]")){
                player.closeInventory();
                continue;
            }

            String[] commandPhrase = commandString.split(":", 2);

            if (commandPhrase.length == 1) {
                player.sendMessage("[QinKitPVPS] 猪比你写错了(*O^O*)");
                return;
            }

            commandPhrase[1] = commandPhrase[1].trim();

            String sender = commandPhrase[0];
            String command = commandPhrase[1];

            if (sender.equals("console")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%",player.getName()));
            } else if (sender.equals("player")) {
                player.performCommand(command.replaceAll("%player%",player.getName()));
            } else {
                player.sendMessage("[QinKitPVPS] 猪比你写错了(*O^O*)");
                return;
            }
        }
    }

    private static void playerGetTeamSword(Player player, String commandString) {
        AllayGame allayGame = QinKitPVPS.getPlugin().getGames().getAllayGame();
        Material sword = Material.getMaterial(commandString.split(" ")[1]);
        ItemStack item = new ItemStack(Material.BEDROCK);
        QinTeam t = QinTeams.getEntityTeam(player);
        if(sword != null)
            item.setType(sword);
        int sharpnessLevel = 0;
        if(t != null) sharpnessLevel = allayGame.getTeamLevels(t.getTeamName()).get("Sharpness");
        if(sharpnessLevel > 0) item.addUnsafeEnchantment(Enchantment.SHARPNESS, sharpnessLevel);
        player.getInventory().addItem(item);
    }

    private static void playerGetTeamArmor(Player player, String commandString) {
        Material material = Material.getMaterial(commandString.split(" ")[1]);
        ItemStack item = new ItemStack(Material.BEDROCK);
        AllayGame allayGame = QinKitPVPS.getPlugin().getGames().getAllayGame();
        QinTeam t = QinTeams.getEntityTeam(player);
        if(material != null)
            item.setType(material);
        int protectionLevel = 0;
        if(t != null) protectionLevel = allayGame.getTeamLevels(t.getTeamName()).get("Protection");
        if(protectionLevel > 0) item.addUnsafeEnchantment(Enchantment.PROTECTION, protectionLevel);
        ItemsFunction.setUnbreakable(item);
        if(item.getType().toString().endsWith("_BOOTS"))
            player.getInventory().setBoots(item);
        else if(item.getType().toString().endsWith("_LEGGINGS"))
            player.getInventory().setLeggings(item);
        else if(item.getType().toString().endsWith("_CHESTPLATE"))
            player.getInventory().setChestplate(item);
        else
            player.getInventory().setHelmet(item);

    }

    private static void playerTeamUpgrade(Player player, String commandString) {
        String upgrade = commandString.split(" ")[1];
        Configuration shop = QinKitPVPS.getPlugin().getResource().getShop();
        AllayGame allayGame = QinKitPVPS.getPlugin().getGames().getAllayGame();
        int price = shop.getInt("Market." + upgrade + ".PurchasePrice");
        int level = Integer.parseInt(upgrade.substring(upgrade.length() - 1));

        if(upgrade.startsWith("sharpness_up_")) {
            if (!allayGame.upgradeTeamSharpnessLevel(player, level)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        AWPlayer.setPlayerAWMoney(player, AWPlayer.getPlayerAWMoney(player) + price);
                    }
                }.runTaskLater(QinKitPVPS.getPlugin(), 1);
            }
        } else if(upgrade.startsWith("protection_up_")) {
            if (!allayGame.upgradeTeamProtectionLevel(player, level)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        AWPlayer.setPlayerAWMoney(player, AWPlayer.getPlayerAWMoney(player) + price);
                    }
                }.runTaskLater(QinKitPVPS.getPlugin(), 1);
            }
        } else if(upgrade.startsWith("money_up_")) {
            if (!allayGame.upgradeTeamMoneyLevel(player, level)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        AWPlayer.setPlayerAWMoney(player, AWPlayer.getPlayerAWMoney(player) + price);
                    }
                }.runTaskLater(QinKitPVPS.getPlugin(), 1);
            }
        }
    }

    private static void playerBuyWool(Player player, String commandString) {
        QinTeam playerTeam = QinTeams.getEntityTeam(player);
        String amount = commandString.substring(11);
        ItemStack wool = new ItemStack(Material.WHITE_WOOL);
        if(playerTeam != null) {
            if (playerTeam.getTeamColor().equals(Color.RED)) {
                wool = new ItemStack(Material.RED_WOOL);
            } else if (playerTeam.getTeamColor().equals(Color.BLUE)) {
                wool = new ItemStack(Material.BLUE_WOOL);
            } else if (playerTeam.getTeamColor().equals(Color.YELLOW)) {
                wool = new ItemStack(Material.YELLOW_WOOL);
            } else if (playerTeam.getTeamColor().equals(Color.GREEN)) {
                wool = new ItemStack(Material.LIME_WOOL);
            }
        }
        int intAmount = 4;
        try {
            intAmount =  Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        wool.setAmount(intAmount);
        player.getInventory().addItem(wool);
    }

    private static void playerOpenMenu(Player p, String commandString) {
        String menuName = commandString.substring(11);
        CustomMenuListeners.openCreatedInventory(p, QinMenusDataSave.getMenuInventory(QinKitPVPS.getPlugin().getKitGame().getQinMenus().getQinMenuFromQinMenuID(menuName), p));
    }

    private static void playerBuyItem(Player player, String commandString) {
        String itemID = commandString.substring(6);

        Configuration shop = QinKitPVPS.getPlugin().getResource().getShop();
        if(!shop.contains("Market." + itemID)) {
            player.sendMessage("你要购买的 " + itemID + " 不存在！");
            return;
        }

        int money = PvzRound.isRunning() ? PvzEntity.getPlayerMoney(player) : AWPlayer.getPlayerAWMoney(player);
        int price = shop.getInt("Market." + itemID + ".PurchasePrice");

        if(money < price) {
            player.sendMessage("你的余额不足！还需要 " + (price - money) + " 钱钱！");
            return;
        }

        List<String> commands = shop.contains("Market." + itemID + ".Commands") ? shop.getStringList("Market." + itemID + ".Commands") : new ArrayList<>();
        if(commands.size() != 0) executeCommands(player, commands, "","");


        if (PvzRound.isRunning()) {
            PvzEntity.setPlayerMoney(player, money - price);
        } else {
            AWPlayer.setPlayerAWMoney(player, money - price);
        }

        ItemStack item = ItemsFunction.getItemStackFromPath(shop, "Market." + itemID);
        player.getInventory().addItem(item);
        player.playSound(player,Sound.ENTITY_VILLAGER_CELEBRATE,1,1f);
    }

    public static Location getLocationInFront(Entity entity, double distance) {
        // 获取实体当前位置
        Location currentLocation = entity.getLocation();

        // 获取实体的朝向向量，并将其乘以指定距离
        Vector direction = currentLocation.getDirection().normalize().multiply(distance);

        // 返回新的位置，将朝向向量加到当前位置上
        return currentLocation.add(direction);
    }

    public static boolean isEntityNearLine(Location from, Location to, Entity zombie, double maxDistance) {
        // 确保起点和终点在同一世界中
        if (!Objects.equals(from.getWorld(), to.getWorld()) || !Objects.equals(from.getWorld(), zombie.getWorld())) {
            return false;
        }

        // 将from和to转换为向量
        Vector start = from.toVector();
        Vector end = to.toVector();
        Vector zombiePos = zombie.getLocation().toVector();

        // 计算from到to的向量
        Vector lineVec = end.clone().subtract(start);

        // 计算from到僵尸的向量
        Vector fromToZombie = zombiePos.clone().subtract(start);

        // 计算僵尸到直线的垂直距离
        double distanceToLine = fromToZombie.clone().crossProduct(lineVec).length() / lineVec.length();

        double distanceSquared = zombie.getLocation().distanceSquared(from);
        if(distanceSquared + Math.pow(distanceToLine,2) > from.distanceSquared(to) + Math.pow(maxDistance,2)) return false;

        // 判断是否在指定距离内
        return distanceToLine <= maxDistance;
    }

    public static void showMagicParticle(Location rayStart, Location rayEnd, Particle particle, int amount, float xSpread, float ySpread, float zSpread, float speed) {
        double distance = rayStart.distance(rayEnd);
        Vector directionNormalized = rayEnd.subtract(rayStart).toVector().normalize();

        for (double i = 0; i < distance; i += 0.5) {
            Vector particleLocation = rayStart.clone().add(directionNormalized.clone().multiply(i)).toVector();
            Objects.requireNonNull(rayStart.getWorld()).spawnParticle(particle, particleLocation.toLocation(rayStart.getWorld()), amount,xSpread,ySpread,zSpread,speed);
        }
    }

    public static void executeDelayCommands(Player player, List<String> commands, QinKitPVPS plugin){
        if (commands == null) return;

        int delay = 0;
        int divide = 0;
        int times = 0;

        for (String commandString : commands) {
            times++;
            if(commandString.startsWith("delay")) {
                if(delay == 0) executeCommands(player ,commands.subList(divide,times - 1),"","");
                else runDelayedCommands(player, plugin, commands.subList(divide,times - 1), delay);
                divide = times;
                delay = Integer.parseInt(commandString.split(":", 2)[1].trim());
                continue;
            }
            if(commandString.equals(commands.get(commands.size() - 1))) {
                runDelayedCommands(player, plugin, commands.subList(divide,commands.size()), delay);
            }
        }
    }

    private static void runDelayedCommands(Player player, QinKitPVPS plugin, List<String> delayedCommands, int delay) {
        new BukkitRunnable() {
            final List<String> newCommands = delayedCommands;
            @Override
            public void run() {
                executeCommands(player, newCommands,"","");
            }
        }.runTaskLater(plugin, delay);
    }


    public static void showTextsToPlayer(Player player ,List<String> introduction){
        for (String introductionString : introduction){
            player.sendMessage(introductionString);
        }
    }

    public static void executeCommand(Player player, String command){
        List<String> commands = new ArrayList<>();
        commands.add(command);
        executeCommands(player,commands,"none","none");
    }
    public static void executeEffects(Player player, List<PotionEffect> effects){
        if(effects == null) return;

        for(PotionEffect effect : effects) {
            effect.apply(player);
        }
    }
    public static void executeSounds(Player player, Sound soundType, int soundPitch, int soundVolume) {
        if(soundType == null){
            soundType = Sound.ENTITY_ENDER_DRAGON_HURT;
        }
        player.playSound(player,soundType,soundPitch,soundVolume);
    }

    /* 判 断 方 法 */
    public static boolean isHoldingSPItem(Player player, String name){
        if(ensureHoldingSPItem(player, name))
            return !banItemsInLobby(player,true);
        return false;
    }

    private static boolean ensureHoldingSPItem(Player player, String name) {
        if(getMainHandItem(player).getType() == Material.AIR && getOffHandItem(player).getType() == Material.AIR){
            return false;
        }
        if(getMainHandItem(player).getType() != Material.AIR && getOffHandItem(player).getType() == Material.AIR){
            return getItemNameWithoutColor(getMainHandItem(player)).equals(name);
        }
        if(getMainHandItem(player).getType() == Material.AIR && getOffHandItem(player).getType() != Material.AIR){
            return getItemNameWithoutColor(getOffHandItem(player)).equals(name);
        }
        if(getMainHandItem(player).getType() != Material.AIR && getOffHandItem(player).getType() != Material.AIR){
            return getItemNameWithoutColor(getMainHandItem(player)).equals(name) ||
                    getItemNameWithoutColor(getOffHandItem(player)).equals(name);
        }
        return false;
    }

    public static Sound safeSound(String soundName) {
        Sound sound = null;
        try {
            sound = Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return sound;
    }

    public static boolean isRightClicking(PlayerInteractEvent event){
        if(!Objects.equals(event.getHand(), EquipmentSlot.HAND)) return false;
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public static boolean isLeftClicking(PlayerInteractEvent event){
        if(!Objects.equals(event.getHand(), EquipmentSlot.HAND)) return false;
        return event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR);
    }

    public static void setBossBar(BossBar bossBar, String title, BarColor color, BarStyle style, double max, double data) {
        bossBar.setTitle(title);
        bossBar.setStyle(style);
        bossBar.setColor(color);
        bossBar.setProgress(data / max); // 设置 BOSS 栏当前数值
    }

    public static void updateBossBar(BossBar bossBar, double max, double data) {
        bossBar.setProgress(data / max); // 设置 BOSS 栏当前数值
    }

    public static void removeExtraItems(Player player, String itemName, int itemMaxAmount){
        int repeat = 0;
        for(ItemStack item : player.getInventory()){
            if(item!=null && Function.getNameWithoutColor(Objects.requireNonNull(item.getItemMeta()).getDisplayName()).equals(itemName)){
                repeat += item.getAmount();
                if(repeat > itemMaxAmount){
                    int extra = repeat - itemMaxAmount;
                    ItemStack stack = new ItemStack(item.getType());
                    stack.setAmount(extra);
                    player.getInventory().removeItem(stack);
                }
            }
        }
    }
    public static void removeExtraItems(Player player, Material material, int itemMaxAmount){
        int repeat = 0;
        for(ItemStack item : player.getInventory()){
            if(item!=null && item.getType().equals(material)){
                repeat += item.getAmount();
                if(repeat > itemMaxAmount){
                    int extra = repeat - itemMaxAmount;
                    ItemStack stack = new ItemStack(material);
                    stack.setAmount(extra);
                    player.getInventory().removeItem(stack);
                }
            }
        }
    }
    public static void removeExtraTagItems(Player player, String tag, int itemMaxAmount){
        int repeat = 0;
        for(ItemStack item : player.getInventory()){
            if(item!=null && Function.getNameWithoutColor(Objects.requireNonNull(item.getItemMeta()).getDisplayName()).contains(tag)){
                repeat += item.getAmount();
                if(repeat > itemMaxAmount){
                    int extra = repeat - itemMaxAmount;
                    ItemStack stack = new ItemStack(item.getType());
                    stack.setAmount(extra);
                    player.getInventory().removeItem(stack);
                }
            }
        }
    }

    public static void removeExtraItemsAuto(Player player, Material material, int itemMaxAmount){
        if(Function.getPlayerItemAmount(player,material) > itemMaxAmount){
            removeExtraItems(player,material,itemMaxAmount);
        }
    }
    public static void reloadTeams(){
        createTeamIfNotExist("Red",ChatColor.RED);
        createTeamIfNotExist("Yellow",ChatColor.YELLOW);
        createTeamIfNotExist("Green",ChatColor.GREEN);
        createTeamIfNotExist("Blue",ChatColor.AQUA);
        createTeamIfNotExist("AWRed",ChatColor.RED);
        createTeamIfNotExist("AWYellow",ChatColor.YELLOW);
        createTeamIfNotExist("AWGreen",ChatColor.GREEN);
        createTeamIfNotExist("AWBlue",ChatColor.AQUA);
        createTeamIfNotExist("noCol",null);
    }

    private static void createTeamIfNotExist(String teamName, ChatColor color) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard scoreboard = manager.getMainScoreboard();
        if(scoreboard.getTeam(teamName) == null){
            Team blue = scoreboard.registerNewTeam(teamName);
            blue.setAllowFriendlyFire(false);
            if(color != null) blue.setColor(color);
        }
    }
    public static boolean isPlayerParkour(Player player){
        return player.getScoreboardTags().contains("QinKit_Parkour") || player.getScoreboardTags().contains("QinKit_ParkourExtra");
    }
    public static void addSkillPower(Player player , String skillName, long max, long add){
        long record = PlayerDataSave.getPlayerPassiveSkillRecords(player,skillName);
        if (record <= max - add) {
            record += add;
            PlayerDataSave.setPlayerPassiveSkillRecords(player, skillName, record);
            player.setLevel((int)record);
        }
    }

    public static void giveAdvancement(Player p, String advancementName) {
        Advancement advancement = Bukkit.getAdvancement(new NamespacedKey("kitspvp",advancementName));
        // 3. 获取玩家在该成就上的进度
        AdvancementProgress progress = null;
        if (advancement != null) {
            progress = p.getAdvancementProgress(advancement);
        } else sendPlayerSystemMessage(p,"该成就丢失，联系管理员; w;");

        if(progress == null) {
            sendPlayerSystemMessage(p,"你好像不存在这个世界上...");
            return;
        }
        // 4. 完成所有条件
        if (progress.isDone()) return;

        for (String criteria : progress.getRemainingCriteria()) {
            progress.awardCriteria(criteria);
        }
    }
    public static boolean isHasPower(Player player, String skillName, long require){
        long record = PlayerDataSave.getPlayerPassiveSkillRecords(player,skillName);
        return record >= require;
    }

    public static boolean hasTag(Entity entity, String tag) {
        return entity.getScoreboardTags().contains(tag);
    }

    public static void dyeCloth(ItemStack item, Color color) {
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
    public static boolean isHasPower(Player player,String skillName,long require,String hit){
        if(PlayerDataSave.getPlayerPassiveSkillRecords(player,skillName) < require){
            player.sendMessage(hit);
        }
        return isHasPower(player,skillName,require);
    }
    public static void updatePlayerPower(Player player, String skillName, long change){
        PlayerDataSave.setPlayerPassiveSkillRecords(player,skillName,change);
    }
    public static void reducePlayerPower(Player player, String skillName, long reduce){
        PlayerDataSave.setPlayerPassiveSkillRecords(player,skillName,PlayerDataSave.getPlayerPassiveSkillRecords(player,skillName) - reduce);

    }
    public static void reducePlayerPower(Player player, String skillName, long reduce, boolean level){
        PlayerDataSave.setPlayerPassiveSkillRecords(player,skillName,PlayerDataSave.getPlayerPassiveSkillRecords(player,skillName) - reduce, level);
    }
    public static void sendPlayerSystemMessage(Player player, String message) {
        message = changeColorCharacters(message);
        player.sendMessage("§7> > §b§lQinKitPVPS §7--> " + message);
    }
    public static void broadcastSystemMessage(String s) {
        String message = changeColorCharacters(s);
        Bukkit.broadcastMessage("§7> > §b§lQinKitPVPS §7--> " + message);
    }
    public static boolean meetRequirements(Player player, List<String> requirements) {
        if(requirements.isEmpty()) return true;
        for(String str : requirements) {
            if(str.startsWith("%player_hasKit_")) {
                String kitID = str.replace("%player_hasKit_", "");
                kitID = kitID.substring(0,kitID.length() - 1);
                return DataBaseCache.getPlayerOwnKits(player).contains(kitID);
            }
        }
        return false;
    }
    public static void changeItemPlaceHolders(ItemStack item, Player player) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lora = meta.getLore();
        if(lora == null) return;
        for(int i = 0; i < lora.size(); i++) {
            lora.set(i, lora.get(i).replace("%player_name%", player.getName()));
            String str = lora.get(i);
            str = replaceKillEffectRare(str);
            str = replaceHasKillEffect(str, player);
            str = replaceKillEffectPrice(str);
            str = replaceKillSoundRare(str);
            str = replaceKillSoundPrice(str);
            str = replaceHasKillSound(str, player);
            lora.set(i,str);
        }
        meta.setLore(lora);
        item.setItemMeta(meta);
    }

    private static String replaceHasKillSound(String input, Player player) {
        int startIndex = input.indexOf("%player_hasKillSound_");
        if (startIndex == -1) return input;
        String suffix = input.substring(startIndex + "%player_hasKillSound_".length());
        int endIndex = suffix.indexOf("%");
        if (endIndex == -1) return input;
        String extractedString = suffix.substring(0, endIndex);
        String replacement = DataBaseCache.getPlayerOwnKillSounds(player).contains(extractedString) ? "§a是" : "§c否";

        return input.substring(0, startIndex) + replacement + input.substring(startIndex + "%player_hasKillSound_".length() + endIndex + 1);
    }

    private static String replaceKillSoundPrice(String input) {
        int startIndex = input.indexOf("%killSound_price_");
        if (startIndex == -1) return input;
        String suffix = input.substring(startIndex + "%killSound_price_".length());
        int endIndex = suffix.indexOf("%");
        if (endIndex == -1) return input;
        String extractedString = suffix.substring(0, endIndex);
        String replacement = "ERROR";
        if(DataBaseCache.getKillSoundColumns().containsKey(extractedString))
            replacement = DataBaseCache.getKillSoundColumns().get(extractedString).get("price");

        return input.substring(0, startIndex) + replacement + input.substring(startIndex + "%killSound_price_".length() + endIndex + 1);
    }

    private static String replaceKillSoundRare(String input) {
        int startIndex = input.indexOf("%killSound_rare_");
        if (startIndex == -1) return input;
        String suffix = input.substring(startIndex + "%killSound_rare_".length());
        int endIndex = suffix.indexOf("%");
        if (endIndex == -1) return input;
        String killEffect = suffix.substring(0, endIndex);
        String replacement = "ERROR";
        if(DataBaseCache.getKillSoundColumns().containsKey(killEffect))
            replacement = changeRareColor(DataBaseCache.getKillSoundColumns().get(killEffect).get("rare"));
        return input.substring(0, startIndex) + replacement + input.substring(startIndex + "%killSound_rare_".length() + endIndex + 1);
    }

    private static String replaceKillEffectRare(String input) {
        int startIndex = input.indexOf("%killEffect_rare_");
        if (startIndex == -1) return input;
        String suffix = input.substring(startIndex + "%killEffect_rare_".length());
        int endIndex = suffix.indexOf("%");
        if (endIndex == -1) return input;
        String killEffect = suffix.substring(0, endIndex);
        String replacement = "ERROR";
        if(DataBaseCache.getKillEffectColumns().containsKey(killEffect))
            replacement = changeRareColor(DataBaseCache.getKillEffectColumns().get(killEffect).get("rare"));
        return input.substring(0, startIndex) + replacement + input.substring(startIndex + "%killEffect_rare_".length() + endIndex + 1);
    }
    private static String replaceKillEffectPrice(String input) {
        int startIndex = input.indexOf("%killEffect_price_");
        if (startIndex == -1) return input;
        String suffix = input.substring(startIndex + "%killEffect_price_".length());
        int endIndex = suffix.indexOf("%");
        if (endIndex == -1) return input;
        String extractedString = suffix.substring(0, endIndex);
        String replacement = "ERROR";
        if(DataBaseCache.getKillEffectColumns().containsKey(extractedString))
            replacement = DataBaseCache.getKillEffectColumns().get(extractedString).get("price");

        return input.substring(0, startIndex) + replacement + input.substring(startIndex + "%killEffect_price_".length() + endIndex + 1);
    }

    public static String replaceHasKillEffect(String input, Player player) {
        int startIndex = input.indexOf("%player_hasKillEffect_");
        if (startIndex == -1) return input;
        String suffix = input.substring(startIndex + "%player_hasKillEffect_".length());
        int endIndex = suffix.indexOf("%");
        if (endIndex == -1) return input;
        String extractedString = suffix.substring(0, endIndex);
        String replacement = DataBaseCache.getPlayerOwnKillEffects(player).contains(extractedString) ? "§a是" : "§c否";

        return input.substring(0, startIndex) + replacement + input.substring(startIndex + "%player_hasKillEffect_".length() + endIndex + 1);
    }
    public static ArrayList<String> getArrayFromTextData(String strings) {
        if(strings == null) strings = "[]";
        strings = strings.replace("[","");
        strings = strings.replace("]","");
        strings = strings.replace("\"","");
        return new ArrayList<>(Arrays.asList(strings.split(",")));
    }
    public static void copyWorld(String originalWorld, String copiedWorld){

        deleteWorld(copiedWorld);

        if(Bukkit.getWorld(originalWorld) != null) {
            Bukkit.unloadWorld(Objects.requireNonNull(Bukkit.getWorld(originalWorld)),true);
        }

        // 获取原始世界和复制出的新世界的文件夹
        File originalWorldFolder = new File(Bukkit.getWorldContainer(), originalWorld);
        File copiedWorldFolder = new File(Bukkit.getWorldContainer(), copiedWorld);
        // 复制世界文件夹
        try {
            copyFolder(originalWorldFolder, copiedWorldFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 加载复制出的新世界
        Bukkit.createWorld(new WorldCreator(originalWorld));

    }

    public static void deleteWorld(String deleteWorld){
        // 获取复制出的新世界
        World copiedWorld = Bukkit.getWorld(deleteWorld);
        // 删除新世界
        if (copiedWorld != null) {
            Bukkit.unloadWorld(copiedWorld,true);
            // 删除新世界文件夹
            File copiedWorldFolder = new File(Bukkit.getWorldContainer(), deleteWorld);
            deleteFolder(copiedWorldFolder);
            Bukkit.getServer().unloadWorld(copiedWorld, false);
        }else{
            Bukkit.broadcastMessage("World deleted failed!");
        }
    }

    public static void summonTower(Location loc, String towerName) {
        Block midBlock = Objects.requireNonNull(loc.getWorld()).getBlockAt(loc);
        Random r = new Random();
        ArrayList<BlockFace> list = new ArrayList<>(){};
        list.add(BlockFace.EAST); list.add(BlockFace.SOUTH); list.add(BlockFace.NORTH); list.add(BlockFace.WEST);
        summonTower(midBlock, towerName, list.get(r.nextInt(4)));
    }

    public static float calculateYaw(Location previousLocation, Location currentLocation) {
        double deltaX = currentLocation.getX() - previousLocation.getX();
        double deltaZ = currentLocation.getZ() - previousLocation.getZ();

        // 计算角度，使用atan2计算z轴和x轴之间的角度
        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90;

        // 确保yaw在-180到180之间
        yaw = (yaw + 360) % 360;
        if (yaw > 180) {
            yaw -= 360;
        }

        return yaw;
    }

    public static void setEntityHealth(LivingEntity a, double health) {
        Objects.requireNonNull(a.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(health);
        a.setHealth(health);
    }

    public static boolean isShootAble(LivingEntity attacker, LivingEntity target, Location start) {
        if(target==null) return false;
        Predicate<Entity> predicate = x -> !x.equals(attacker);
        Location end = target.getLocation().clone().add(0,1,0);
        // 创建一条射线
        RayTraceResult result = Objects.requireNonNull(start.getWorld()).rayTrace(
                start, // 起始点
                end.subtract(start).toVector(),               // 方向向量
                30,                     // 最大距离
                FluidCollisionMode.NEVER, // 流体模式
                true,                    // 忽略非可视方块
                0.1,                     // 检测范围（宽度）
                predicate              // 过滤器
        );

        return result != null && result.getHitEntity() instanceof LivingEntity;
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

    public static Vector calculateVelocity(Vector from, Vector to, double heightGain, double gravity)
    {

        // Block locations
        int endGain = to.getBlockY() - from.getBlockY();
        double horizDist = Math.sqrt(distanceSquared(from, to));

        // Height gain
        double gain = heightGain;

        double maxGain = Math.max(gain, (endGain + gain));

        // Solve quadratic equation for velocity
        double a = -horizDist * horizDist / (4 * maxGain);
        double b = horizDist;
        double c = -endGain;

        double slope = -b / (2 * a) - Math.sqrt(b * b - 4 * a * c) / (2 * a);

        // Vertical velocity
        double vy = Math.sqrt(maxGain * gravity);

        // Horizontal velocity
        double vh = vy / slope;

        // Calculate horizontal direction
        int dx = to.getBlockX() - from.getBlockX();
        int dz = to.getBlockZ() - from.getBlockZ();
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;

        // Horizontal velocity components
        double vx = vh * dirx;
        double vz = vh * dirz;

        return new Vector(vx, vy, vz);
    }

    private static double distanceSquared(Vector from, Vector to)
    {
        double dx = to.getBlockX() - from.getBlockX();
        double dz = to.getBlockZ() - from.getBlockZ();

        return dx * dx + dz * dz;
    }

    public static boolean isShootAble(LivingEntity attacker, LivingEntity target) {
        Predicate<Entity> predicate = x -> !x.equals(attacker);
        Location start = attacker.getEyeLocation();
        Location end = target.getBoundingBox().getCenter().toLocation(target.getWorld());
        // 创建一条射线
        RayTraceResult result = attacker.getWorld().rayTrace(
                attacker.getEyeLocation(), // 起始点
                end.subtract(start).toVector(),               // 方向向量
                start.distance(end),                     // 最大距离
                FluidCollisionMode.NEVER, // 流体模式
                true,                    // 忽略非可视方块
                0.1,                     // 检测范围（宽度）
                predicate              // 过滤器
        );

        return result != null && result.getHitEntity() instanceof LivingEntity;
    }

    public static int createRandom(int start, int end) {
        Random r = new Random();
        return r.nextInt(start, end);
    }
    public static boolean noAllowBuildTower(Player p, String towerName, Location loc) {

        if(p.getGameMode().equals(GameMode.CREATIVE)) return false;
        Block midBlock = loc.getBlock();

        Configuration data = QinKitPVPS.getPlugin().getResource().getData();

        String basePath = "TowerData." + towerName;
        int buildingSize = data.contains(basePath + ".size") ? data.getInt(basePath + ".size") : 3;
        int height = buildingSize == 3 ? 6 : 8;

        for (int x = buildingSize == 3 ? -1 : -2; x < buildingSize / 2 + 1; x++) {
            for (int z = buildingSize == 3 ? -1 : -2; z < buildingSize/ 2 + 1; z++) {
                Block block = midBlock.getWorld().getBlockAt(midBlock.getLocation().add(x, -1, z));
                if(!isBlockHasTag(block,"OriginalMap")) {
                    sendPlayerSystemMessage(p,"放置的防御塔基底悬空原地图方块，无法放置");
                    return true;
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = buildingSize == 3 ? -1 : -2; x < buildingSize / 2 + 1; x++) {
                for (int z = buildingSize == 3 ? -1 : -2; z < buildingSize / 2 + 1; z++) {
                    Block block = midBlock.getWorld().getBlockAt(midBlock.getLocation().add(x, y, z));
                    if(isBlockHasTag(block,"OriginalMap")) {
                        sendPlayerSystemMessage(p,"你的塔周围存在原地图方块，无法放置");
                        return true;
                    }
                    else if(isBlockHasTag(block,"AllayTower")) {
                        sendPlayerSystemMessage(p,"你的塔距离周围的防御塔过近，无法放置");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean summonTower(Block midBlock, String towerName, BlockFace playerFacing) {
        Configuration data = QinKitPVPS.getPlugin().getResource().getData();

        String basePath = "TowerData." + towerName;
        int buildingSize = data.contains(basePath + ".size") ? data.getInt(basePath + ".size") : 3;
        int height = buildingSize == 3 ? 6 : 8;

        boolean ifWasteLand = Objects.equals(towerName, "荒地") || Objects.equals(towerName, "大荒地");


        for (int y = -1; y < height; y++) {
            for (int x = buildingSize == 3 ? -1 : -2; x < buildingSize / 2 + 1; x++) {
                for (int z = buildingSize == 3 ? -1 : -2; z < buildingSize / 2 + 1; z++) {
                    String path = basePath + "." + x + "_" + y + "_" + z;

                    int offSetX = x;
                    int offSetZ = z;

                    switch (playerFacing) {
                        case WEST -> {
                            offSetX = -x; offSetZ = -z;
                        }
                        case SOUTH -> {
                            offSetX = -z;
                            offSetZ = x;
                        }
                        case NORTH -> {
                            offSetZ = -x;
                            offSetX = z;
                        }
                    }

                    Material material = data.contains(path + ".type") ? Material.valueOf(data.getString(path + ".type")) : Material.AIR;
                    Block block = midBlock.getWorld().getBlockAt(midBlock.getLocation().add(offSetX, y, offSetZ));
                    block.setType(material, false);

                    BlockState blockState = block.getState();

                    if (blockState.getBlockData() instanceof Fence fence) {
                        List<String> connectedFaces = data.getStringList(path + ".connectedFaces");
                        for (String face : connectedFaces) {
                            fence.setFace(BlockFace.valueOf(face), true);
                        }
                        blockState.setBlockData(fence);
                    }

                    if (blockState.getBlockData() instanceof Wall wall) {
                        wall.setHeight(BlockFace.NORTH, Wall.Height.valueOf(data.getString(path + ".wallNorth")));
                        wall.setHeight(BlockFace.SOUTH, Wall.Height.valueOf(data.getString(path + ".wallSouth")));
                        wall.setHeight(BlockFace.EAST, Wall.Height.valueOf(data.getString(path + ".wallEast")));
                        wall.setHeight(BlockFace.WEST, Wall.Height.valueOf(data.getString(path + ".wallWest")));
                        wall.setUp(data.getBoolean(path + ".wallIsUp"));
                        blockState.setBlockData(wall);
                    }

                    if (blockState.getBlockData() instanceof Slab slab) {
                        Slab.Type slabType = Slab.Type.valueOf(data.getString(path + ".slabType"));
                        slab.setType(slabType);
                        blockState.setBlockData(slab);
                    }

                    if (blockState instanceof Directional directional) {
                        BlockFace facing = BlockFace.valueOf(data.getString(path + ".facing"));
                        directional.setFacing(facing);
                    }

                    if (blockState.getBlockData() instanceof Rotatable rotation) {
                        BlockFace rotationFace = BlockFace.valueOf(data.getString(path + ".rotation"));
                        rotation.setRotation(rotationFace);
                        blockState.setBlockData(rotation);
                    }

                    if (blockState.getBlockData() instanceof Lantern lantern) {
                        boolean hanging = data.getBoolean(path + ".hanging");
                        lantern.setHanging(hanging);
                        blockState.setBlockData(lantern);
                    }

                    if (blockState.getBlockData() instanceof Fence fence) {
                        fence.setFace(BlockFace.NORTH, data.getBoolean(path + ".north"));
                        fence.setFace(BlockFace.SOUTH, data.getBoolean(path + ".south"));
                        fence.setFace(BlockFace.EAST, data.getBoolean(path + ".east"));
                        fence.setFace(BlockFace.WEST, data.getBoolean(path + ".west"));
                        block.setBlockData(fence);
                    }

                    blockState.update(true, false);

                    if(!ifWasteLand)
                        block.setMetadata("AllayTower", new org.bukkit.metadata.FixedMetadataValue(QinKitPVPS.getPlugin(), true));
                    else if(isBlockHasTag(block,"AllayTower"))
                        block.removeMetadata("AllayTower", QinKitPVPS.getPlugin());


                }
            }
        }
        return false;
    }
    public static boolean summonTower(Player player, String towerName) {
        Block midBlock = player.getTargetBlockExact(8);
        if(summonTower(midBlock,towerName,player.getFacing())) {
            Function.sendPlayerSystemMessage(player, "你没有指向一个有效的方块或没有这个塔");
            return true;
        }
        return false;
    }

    public static void summonTower(Player player, String towerName, Location loc) {
        Block midBlock = loc.getBlock();
        if(summonTower(midBlock,towerName,player.getFacing())) {
            Function.sendPlayerSystemMessage(player, "你没有指向一个有效的方块或没有这个塔");
        }
    }

    public static boolean cannotPlaceEntity(PlayerInteractEvent e) {
        if(!Function.isRightClicking(e)) return true;
        if(e.getClickedBlock() == null || e.getClickedBlock().isEmpty()) return true;
        return e.getItem() == null;
    }

    private static String changeRareColor(String input) {
        switch (input) {
            case "普通" -> input = "§f"+ input + "§f";
            case "稀有" -> input = "§b"+ input + "§f";
            case "高级" -> input = "§6"+ input + "§f";
            case "史诗" -> input = "§d"+ input + "§f";
            case "传说" -> input = "§e"+ input + "§f";
            case "神话" -> input = "§4"+ input + "§f";
        }
        return input;
    }

    public static Location predictFutureLocation(Entity entity, double seconds) {

        if(entity instanceof Player player) {
            // 获取玩家的当前朝向（Yaw）
            float yaw = player.getLocation().getYaw();

            // 将Yaw转换为弧度
            double yawRad = Math.toRadians(yaw);

            // 计算玩家的移动方向向量
            Vector direction = new Vector(-Math.sin(yawRad), 0, Math.cos(yawRad));

            // 获取玩家当前的移动速度（假设玩家在奔跑）
            double speed = player.isSprinting() ? 5.612 : 4.317;

            // 计算未来的位置增量
            Vector futureMovement = direction.multiply(speed * seconds);

            // 计算并返回玩家的未来位置
            Location currentLocation = player.getLocation();
            return currentLocation.add(futureMovement);
        } else return entity.getLocation();

    }

    private static void copyFolder(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String[] files = source.list();
            if (files != null) {
                for (String file : files) {
                    File srcFile = new File(source, file);
                    if(srcFile.getName().equals("uid.dat")) continue;

                    File destFile = new File(destination, file);

                    // 递归复制子文件夹
                    copyFolder(srcFile, destFile);
                }
            }
        } else {
            // 复制文件
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归删除子文件夹
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }

        // 删除空文件夹
        folder.delete();
    }

    public static String addTab (String arg) {
        CompleteCommands.getTabArryList().add(arg);
        return arg;
    }

    public static Entity getTopEntity(Entity e) {
        Entity topEntity;
        if(e.getPassengers().isEmpty()) topEntity = e;
        else topEntity = e.getPassengers().getLast();
        return topEntity;
    }

    public static boolean isSameQinTeam(Entity a, Entity b) {
        QinTeam aTeam = QinTeams.getEntityTeam(a);
        QinTeam bTeam = QinTeams.getEntityTeam(b);
        return aTeam != null && aTeam.equals(bTeam);
    }
}
