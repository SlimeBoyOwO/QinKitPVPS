package org.NoiQing.commands;

import org.NoiQing.AllayWar.AWAPI.AWRound;
import org.NoiQing.AllayWar.AWUtils.AWPlayer;
import org.NoiQing.AllayWar.PvzGame.PVZAPI.PvzMap;
import org.NoiQing.BukkitRunnable.MapRunnable;
import org.NoiQing.BukkitRunnable.WeatherRunnable;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinKit;
import org.NoiQing.api.QinMap;
import org.NoiQing.api.QinMenu;
import org.NoiQing.mainGaming.Game;
import org.NoiQing.util.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Lantern;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Wall;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MainCommands implements CommandExecutor {
    private final QinKitPVPS plugin;
    private final Game game;
    private final CreateFileConfig configs;
    private int randomMapID;
    private int randomTeamMapID;

    public MainCommands(QinKitPVPS plugin) {
        this.plugin = plugin;
        game = plugin.getGame();
        configs = plugin.getResource();
        randomMapID = plugin.getRandomMapID();
        randomTeamMapID = plugin.getRandomTeamMapID();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("[QinKitPVPS] 欢迎使用本插件(*╹▽╹*)");
            return true;
        }
        else if (sender instanceof Player player) {
            //插件跑酷系统
            if(args[0].equalsIgnoreCase("parkour")){
                player.removeScoreboardTag("QinKit_ParkourExtra");
                player.addScoreboardTag("QinKit_Parkour");
                player.sendMessage("§7> > §b§lQinKitPVPS §7--> §3进入了原版跑酷模式！§b(〃'▽'〃)");
                return true;
            }
            if(args[0].equalsIgnoreCase("parkourExtra")){
                player.removeScoreboardTag("QinKit_Parkour");
                player.addScoreboardTag("QinKit_ParkourExtra");
                player.sendMessage("§7> > §b§lQinKitPVPS §7--> §3进入了身法跑酷模式！§b(〃'▽'〃)");
                return true;
            }
            if(args[0].equalsIgnoreCase("parkourStop")){
                player.removeScoreboardTag("QinKit_Parkour");
                player.removeScoreboardTag("QinKit_ParkourExtra");
                player.sendMessage("§7> > §b§lQinKitPVPS §7--> §3退出了跑酷模式！§b(〃>▽<〃)");
                return true;
            }
            //玩家自杀指令
            if (args[0].equalsIgnoreCase(Function.addTab("kill"))) {
                player.setHealth(0);
                return true;
            }
            //随机职业指令
            if(args[0].equalsIgnoreCase("randomKit")){
                kitRandomKit(player);
            }
            //抽取职业指令
            if(args[0].equalsIgnoreCase("rollKit")){
                rollRandomKit(player);
                return true;
            }
            //前往被复制的世界指令
            if(args[0].equalsIgnoreCase("tpCopiedWorld")){
                player.teleport(new Location(Bukkit.getWorld("skyblock_copy"),1000, 30 ,1000));
            }

            //前往大厅指令
            if (args[0].equalsIgnoreCase(Function.addTab("lobby"))) {
                Function.playerReset(player);
                Function.clearPlayerTeam(player);
                Function.playerTpLobby(player);
                return true;
            }
            //设定玩家职业指令
            if (args[0].equalsIgnoreCase("kit")) {
                executeKitCommandSelf(player, args);
                return true;
            }

            //向玩家提示职业信息指令
            if (args[0].equalsIgnoreCase("intro")) {
                executeShowKitIntroductionCommandSelf(player, args);
                return true;
            }

            //玩家购买职业指令
            if (args[0].equalsIgnoreCase("buyKit")) {
                executeBuyKitCommandSelf(player, args);
                return true;
            }

            //前往职业战争模式指令
            if (args[0].equalsIgnoreCase("tpMode")) {
                executeTpModeSpawnCommandSelf(player, args);
                return true;
            }

            //打开职战菜单指令
            if (args[0].equalsIgnoreCase("openMenu")) {
                executeOpenMenuCommandSelf(player, args);
                return true;
            }

            if(args[0].equalsIgnoreCase("buyKillEffect")) {
                executePlayerBuyKillEffect(player, args);
                return true;
            }

            if (args[0].equalsIgnoreCase("killEffectApply")) {
                executeApplyPlayerKillEffects(player,args);
                return true;
            }

            if (args[0].equalsIgnoreCase("killEffectClear")) {
                executeClearPlayerKillEffects(player);
                return true;
            }

            if (args[0].equalsIgnoreCase("killSoundApply")) {
                executeApplyPlayerKillSound(player, args);
                return true;
            }

            if (args[0].equalsIgnoreCase("buyKillSound")) {
                executePlayerBuyKillSound(player, args);
                return true;
            }

            if (args[0].equalsIgnoreCase("playerPrefixApply")) {
                executeApplyPlayerPrefix(player,args);
                return true;
            }

            if (args[0].equalsIgnoreCase("playerPrefixClear")) {
                executeClearPlayerTitle(player);
                return true;
            }

            //前往出生点指令
            if (args[0].equalsIgnoreCase("tpSpawn")) {
                executeTpRandomSpawnCommandSelf(player);
                return true;
            }

            //前往出生点但无提示指令
            if (args[0].equalsIgnoreCase("tpSpawnWithoutInfo")) {
                executeTpRandomSpawnCommandWithOutInfoSelf(player);
                return true;
            }

            //前往团队模式但是无提示指令
            if (args[0].equalsIgnoreCase("tpTeamSpawnWithoutInfo")) {
                executeTpTeamedSpawnCommandWithOutInfoSelf(player);
                return true;
            }

            //========================================================================
            //                       后面的指令需要玩家拥有权限
            //========================================================================

            if(!configs.getConfig().getStringList("Admins").contains(player.getName())) return false;

            //插件信息提示系统
            if (args[0].equalsIgnoreCase(Function.addTab("information"))) {
                player.sendMessage("[QinKitPVPS] 你好呀，我是钦灵制作的职业战争插件(*OωO*)");
                return true;
            }
            if(args[0].equalsIgnoreCase(Function.addTab("noInv"))) {
                player.setInvulnerable(false);
                return true;
            }
            if(args[0].equalsIgnoreCase(Function.addTab("startPvzLevel"))) {
                executeStartPvzLevel(args, player);
                return true;
            }

            //前往出生点id指令
            if (args[0].equalsIgnoreCase(Function.addTab("tpSpawn")) && args.length == 2) {
                executeTpSpawnCommandSelf(player, args);
                return true;
            }

            if (args[0].equalsIgnoreCase(Function.addTab("health"))) {
                for(Entity e : player.getNearbyEntities(8,8,8)) {
                    if(e instanceof LivingEntity lv) {
                        if(lv.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null)
                            lv.setHealth(Objects.requireNonNull(lv.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
                    }
                }
                return true;
            }

            if (args[0].equalsIgnoreCase(Function.addTab("refreshDefault"))) {
                try{
                    plugin.getMySQLDataBase().refreshDefaultKits();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            }

            //检测所有职业指令
            if(args[0].equalsIgnoreCase(Function.addTab("getAllKits"))) {
                try {
                    player.sendMessage(plugin.getMySQLDataBase().getAllKits().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            }

            if(args[0].equalsIgnoreCase(Function.addTab("startAW"))){
                AWRound.initRound();
                return true;
            }
            if(args[0].equalsIgnoreCase(Function.addTab("endAW"))){
                AWRound.endRound("AWBlue");
                return true;
            }

            //前往空岛模式原本世界指令
            if(args[0].equalsIgnoreCase(Function.addTab("toRog"))){
                player.teleport(new Location(Bukkit.getWorld("skyblock"),0, 100 ,0));
            }

            if(args[0].equalsIgnoreCase(Function.addTab("loadSB"))) {
                Bukkit.createWorld(new WorldCreator("skyblock"));
                Function.sendPlayerSystemMessage(player,"空岛世界加载完毕");
            } else if(args[0].equalsIgnoreCase(Function.addTab("loadSBC"))) {
                Bukkit.createWorld(new WorldCreator("skyblock_copy"));
                Function.sendPlayerSystemMessage(player,"复制空岛世界加载完毕");
            }else if(args[0].equalsIgnoreCase(Function.addTab("unloadSB"))) {
                Bukkit.unloadWorld("skyblock",false);
                Function.sendPlayerSystemMessage(player,"空岛世界取消加载完毕");
            } else if(args[0].equalsIgnoreCase(Function.addTab("unloadSBC"))) {
                Bukkit.unloadWorld("skyblock_copy",false);
                Function.sendPlayerSystemMessage(player,"复制空岛世界取消加载完毕");
            }

            //让空岛地图复制指令
            if(args[0].equalsIgnoreCase(Function.addTab("copyWorld"))){
                Function.copyWorld("skyblock","skyblock_copy");
                return true;
            }

            //删除复制地图指令
            if(args[0].equalsIgnoreCase(Function.addTab("deleteWorld"))){
                Function.deleteWorld("skyblock_copy");
                return true;
            }

            //获取玩家小钱钱指令
            if (args[0].equalsIgnoreCase(Function.addTab("getMoney"))) {
                try {
                    player.sendMessage("你拥有 " + plugin.getSQLiteDatabase().getPlayerMoney(player) + " 个小钱钱");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            }

            if (args[0].equalsIgnoreCase(Function.addTab("getKey"))) {
                try {
                    player.sendMessage("你拥有 " + plugin.getMySQLDataBase().getPlayerKey(player) + " 个小钥匙");
                    plugin.getMySQLDataBase().setPlayerKey(player, 3);
                    player.sendMessage("送你三个~ 你拥有 " + plugin.getMySQLDataBase().getPlayerKey(player) + " 个小钥匙");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            //获取玩家经验指令
            if (args[0].equalsIgnoreCase(Function.addTab("getExp"))){
                try {
                    player.sendMessage("你拥有 " + plugin.getSQLiteDatabase().getPlayerRankExp(player) + " 经验");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            }

            //aw金币
            if (args[0].equalsIgnoreCase(Function.addTab(Function.addTab("setAWMoney")))){
                AWPlayer.setPlayerAWMoney(player, 20000);
                return true;
            }

            //插件重置指令
            if (args[0].equalsIgnoreCase(Function.addTab(Function.addTab("reload")))){
                configs.reload();
                QinKitsDataSave.clearQinKitsDataSave();
                QinSkillsDataSava.clearQinSkillsDataSave();
                QinMapsDataSave.clearQinMapsDataSave();
                QinMenusDataSave.clearQinMenusDataSave();
                QinBossBarDataSave.clearQinBossBarDataSave();
                game.getQinBossBars().reloadBossBars();
                PlayerDataSave.clearPlayerDataSave();
                player.sendMessage("[QinKitPVPS] 插件已经重新载入了哦(*^▽^*)");
                return true;
            }

            //停止天气指令
            if (args[0].equalsIgnoreCase(Function.addTab("weatherSwitch"))) {
                if(WeatherDataSave.getWeatherSwitch()){
                    WeatherDataSave.setWeatherSwitch(false);
                    player.sendMessage("§7> > §b§lQinKitPVPS §7--> §3天气已经停止更改！§b〃=▽=〃");
                }else{
                    WeatherDataSave.setWeatherSwitch(true);
                    player.sendMessage("§7> > §b§lQinKitPVPS §7--> §3天气已经恢复更改！§b〃=▽=〃");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase(Function.addTab("displayText"))) {
                executeSummonTextDisplay(args, player);
                return true;
            }

            if (args[0].equalsIgnoreCase(Function.addTab("saveTower"))) {
                executeSaveTowerData(args, player);
                return true;
            }

            if (args[0].equalsIgnoreCase(Function.addTab("savePlant"))) {
                executeSavePlantData(args, player);
                return true;
            }

            if (args[0].equalsIgnoreCase(Function.addTab("placeTower"))) {
                executePlaceTower(args, player);
                return true;
            }

            //设定玩家钱钱数指令
            if(args[0].equalsIgnoreCase(Function.addTab("setMoney"))){
                int money = Integer.parseInt(args[1]);
                try {
                    plugin.getSQLiteDatabase().updatePlayerMoney(player,money);
                    PlayerDataSave.updatePlayerMoneyRecord(player,money);
                    player.sendMessage("更新完毕！你现在的小钱钱是: " + plugin.getSQLiteDatabase().getPlayerMoney(player));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            }

            //设定玩家经验值指令
            if(args[0].equalsIgnoreCase(Function.addTab("setExp"))){
                int exp = Integer.parseInt(args[1]);
                try{
                    plugin.getSQLiteDatabase().updatePlayerRankExp(player,exp);
                    PlayerDataSave.updatePlayerExpRecord(player,exp);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (args[0].equalsIgnoreCase(Function.addTab("freeKit"))) {
                executeGiveFreeKitToPlayer(player, args);
                return true;
            }

            //保存坦克实体指令
            if (args[0].equalsIgnoreCase(Function.addTab("saveTank"))) {
                executeSaveTankData(player, args);
            }

            if (args[0].equalsIgnoreCase(Function.addTab("placeTank"))) {
                executePlaceTankData(player, args);
            }

            //更改职业战争地图天气指令
            if (args[0].equalsIgnoreCase(Function.addTab("weather"))) {
                executeChangeWeatherCommandSelf(player, args);
                return true;
            }

            //更改职业战争地图指令
            if (args[0].equalsIgnoreCase(Function.addTab("map"))) {
                executeChangeMapCommandSelf(player, args);
                return true;
            }

            //更改月灵战争地图指令
            if (args[0].equalsIgnoreCase(Function.addTab("allayMap"))) {
                try {
                    int x = Integer.parseInt(args[1]);
                    QinMap map = QinKitPVPS.getPlugin().getGame().getMaps().getAllayQinMapByMapID(x);
                    if (map != null) {
                        AWRound.setChooseMapID(x);
                        Function.sendPlayerSystemMessage(player, "已选择地图: " + map.getMapName());
                    } else Function.sendPlayerSystemMessage(player, "你输入的地图并不存在！");
                } catch(NumberFormatException e) {
                    Function.sendPlayerSystemMessage(player,"请输入数字地图编号！");
                }
                return true;
            }

            //增加玩家特效
            if (args[0].equalsIgnoreCase(Function.addTab("killEffectAdd"))) {
                executeAddPlayerKillEffects(player,args);
                return true;
            }

            if (args[0].equalsIgnoreCase(Function.addTab("playerPrefixAdd"))) {
                executeAddPlayerPrefix(player, args);
                return true;
            }

            if (args[0].equalsIgnoreCase(Function.addTab("saveTower"))) {
                executeSaveTowerData(args, player);
                return true;
            }

            if (args[0].equalsIgnoreCase(Function.addTab("execCommand"))) {
                executeExecuteCommandPlayer(player, args);
                return true;
            }

            player.sendMessage("你输入错指令了吧？ 我们插件没有这个指令");
        }

        return false;
    }

    private void executeStartPvzLevel(String[] args, Player p) {
        PvzMap pvzMap = QinKitPVPS.getPlugin().getGame().getMaps().getPvzMapByMapID(Integer.parseInt(args[1]));
        if(pvzMap == null) {
            Function.sendPlayerSystemMessage(p,"你指定的PVZ地图不存在啊啊啊啊啊");
            return;
        }
        if(pvzMap.getVillagerArea().getWorld() == null) {
            Function.sendPlayerSystemMessage(p,"你指定的PVZ地图的世界没加载啊啊啊啊啊");
            return;
        }
        for(Player players : pvzMap.getVillagerArea().getWorld().getPlayers()) {
            players.sendTitle("§a准备 放置 植物！","§b保护你的脑子叭！",10,70,20);
        }
        pvzMap.startLevel(args[2]);
    }

    private void executeSavePlantData(String[] args, Player player) {
        List<Entity> entities = player.getNearbyEntities(4,4,4);
        int mostPassengers = 0;
        Entity root = null;
        for(Entity e : entities) {
            if(!(e instanceof Display) && e.getPassengers().isEmpty()) continue;
            if(e.getPassengers().size() > mostPassengers) {
                root = e; mostPassengers = e.getPassengers().size();
            }
        }

        if (root == null) {
            player.sendMessage("附近10m内找不到连续的植物");
            return;
        }

        Configuration data = configs.getPvzData();
        data.set("PlantData." + args[1], null);

        int index = 0;
        for(Entity e : root.getPassengers()) {
            if(e instanceof ItemDisplay display) {
                data.set("PlantData." + args[1] + "." + index + ".Item",display.getItemStack());
                data.set("PlantData." + args[1] + "." + index + ".ItemDisplay",display.getItemDisplayTransform().toString());
                // 将 Vector3f 转换为 List 进行保存
                data.set("PlantData." + args[1] + "." + index + ".Transform.Translation", Arrays.asList(display.getTransformation().getTranslation().x, display.getTransformation().getTranslation().y, display.getTransformation().getTranslation().z));

                // 将 Quaternion 转换为 List 进行保存
                data.set("PlantData." + args[1] + "." + index + ".Transform.LeftRotation", Arrays.asList(display.getTransformation().getLeftRotation().x, display.getTransformation().getLeftRotation().y, display.getTransformation().getLeftRotation().z, display.getTransformation().getLeftRotation().w));
                data.set("PlantData." + args[1] + "." + index + ".Transform.RightRotation", Arrays.asList(display.getTransformation().getRightRotation().x, display.getTransformation().getRightRotation().y, display.getTransformation().getRightRotation().z, display.getTransformation().getRightRotation().w));

                // 将 Scale(Vector3f) 转换为 List 进行保存
                data.set("PlantData." + args[1] + "." + index + ".Transform.Scale", Arrays.asList(display.getTransformation().getScale().x, display.getTransformation().getScale().y, display.getTransformation().getScale().z));

                index++;
            }
        }

        data.save();

        Function.sendPlayerSystemMessage(player,"植物" + args[1] + "已成功保存！");
    }

    private void executePlaceTankData(Player player, String[] args) {
        Configuration data = configs.getData();
        if(!data.contains("TankData." + args[1])) {
            Function.sendPlayerSystemMessage(player,"坦克" + args[1] + "不存在");
            return;
        }

        ItemDisplay lastItemDisplay = null;

        for(int i = 0; i < 100; i++) {
            if(data.contains("TankData." + args[1] + "." + i + ".Item")) {
                ItemStack item = data.getItemStack("TankData." + args[1] + "." + i + ".Item");
                ItemDisplay.ItemDisplayTransform transform = ItemDisplay.ItemDisplayTransform.valueOf(data.getString("TankData." + args[1] + "." + i + ".ItemDisplay"));
                ItemDisplay display = player.getWorld().spawn(player.getLocation(), ItemDisplay.class);
                display.setItemStack(item);
                display.setItemDisplayTransform(transform);
                /*填空***/
                // 从 List 加载 Translation (Vector)
                List<Double> translationList = data.getDoubleList("TankData." + args[1] + "." + i + ".Transform.Translation");
                Vector3f translation = new Vector3f(translationList.get(0).floatValue(), translationList.get(1).floatValue(), translationList.get(2).floatValue());

                // 从 List 加载 LeftRotation 和 RightRotation (Quaternion)
                List<Double> leftRotationList = data.getDoubleList("TankData." + args[1] + "." + i + ".Transform.LeftRotation");
                Quaternionf leftRotation = new Quaternionf(leftRotationList.get(0).floatValue(), leftRotationList.get(1).floatValue(), leftRotationList.get(2).floatValue(), leftRotationList.get(3).floatValue());

                List<Double> rightRotationList = data.getDoubleList("TankData." + args[1] + "." + i + ".Transform.RightRotation");
                Quaternionf rightRotation = new Quaternionf(rightRotationList.get(0).floatValue(), rightRotationList.get(1).floatValue(), rightRotationList.get(2).floatValue(), rightRotationList.get(3).floatValue());

                // 从 List 加载 Scale (Vector)
                List<Double> scaleList = data.getDoubleList("TankData." + args[1] + "." + i + ".Transform.Scale");
                Vector3f scale = new Vector3f(scaleList.get(0).floatValue(), scaleList.get(1).floatValue(), scaleList.get(2).floatValue());

                // 创建 Transformation 对象并应用
                Transformation transformation = new Transformation(translation, leftRotation, scale, rightRotation);
                display.setTransformation(transformation);

                /*填空***/
                if(lastItemDisplay != null)
                    lastItemDisplay.addPassenger(display);
                lastItemDisplay = display;
            } else break;
        }

        Function.sendPlayerSystemMessage(player, "坦克" + args[1] + "的数据加载完毕");
    }

    private void executeSaveTankData(Player player, String[] args) {
        List<Entity> entities = player.getNearbyEntities(10,10,10);
        int mostPassengers = 0;
        Entity root = null;
        for(Entity e : entities) {
            if(!(e instanceof Display) && e.getPassengers().isEmpty()) continue;
            if(e.getPassengers().size() > mostPassengers) {
                root = e; mostPassengers = e.getPassengers().size();
            }
        }

        if (root == null) {
            player.sendMessage("附近10m内找不到连续的坦克");
            return;
        }

        Configuration data = configs.getData();
        data.set("TankData." + args[1], null);

        int index = 0;
        for(Entity e : root.getPassengers()) {
            if(e instanceof ItemDisplay display) {
                data.set("TankData." + args[1] + "." + index + ".Item",display.getItemStack());
                data.set("TankData." + args[1] + "." + index + ".ItemDisplay",display.getItemDisplayTransform().toString());
                // 将 Vector3f 转换为 List 进行保存
                data.set("TankData." + args[1] + "." + index + ".Transform.Translation", Arrays.asList(display.getTransformation().getTranslation().x, display.getTransformation().getTranslation().y, display.getTransformation().getTranslation().z));

                // 将 Quaternion 转换为 List 进行保存
                data.set("TankData." + args[1] + "." + index + ".Transform.LeftRotation", Arrays.asList(display.getTransformation().getLeftRotation().x, display.getTransformation().getLeftRotation().y, display.getTransformation().getLeftRotation().z, display.getTransformation().getLeftRotation().w));
                data.set("TankData." + args[1] + "." + index + ".Transform.RightRotation", Arrays.asList(display.getTransformation().getRightRotation().x, display.getTransformation().getRightRotation().y, display.getTransformation().getRightRotation().z, display.getTransformation().getRightRotation().w));

                // 将 Scale(Vector3f) 转换为 List 进行保存
                data.set("TankData." + args[1] + "." + index + ".Transform.Scale", Arrays.asList(display.getTransformation().getScale().x, display.getTransformation().getScale().y, display.getTransformation().getScale().z));

                index++;
            }
        }

        data.save();

        Function.sendPlayerSystemMessage(player,"坦克" + args[1] + "已成功保存！");
    }

    private void executePlaceTower(String[] args, Player player) {
        String towerName = args[1];

        if (Function.summonTower(player, towerName)) return;

        Function.sendPlayerSystemMessage(player, "塔 " + towerName + " 已成功放置！");
    }

    private void executeSaveTowerData(String[] args, Player player) {

        Block midBlock = player.getTargetBlockExact(8);
        Configuration data = configs.getData();

        if(midBlock == null) {
            Function.sendPlayerSystemMessage(player, "你没有指向一个有效的方块");
            return;
        }

        data.set("TowerData." + args[1],null);
        int length = args.length; int buildingSize = 3;
        if (length > 2) buildingSize = Integer.parseInt(args[2]);
        if(buildingSize != 3 && buildingSize != 5) buildingSize = 3;

        data.set("TowerData." + args[1] + ".size", buildingSize);
        int height = buildingSize == 3 ? 6 : 8;

        for(int y = -1; y < height; y++) {
            for(int x = buildingSize == 3 ? -1 : -2; x < buildingSize / 2 + 1; x++) {
                for(int z = buildingSize == 3 ? -1 : -2; z < buildingSize / 2 + 1; z++) {
                    Block storedBlock = midBlock.getWorld().getBlockAt(midBlock.getLocation().add(x,y,z));
                    BlockState blockState = storedBlock.getState();
                    String path = "TowerData." + args[1] + "." + x + "_" + y + "_" + z;

                    if(storedBlock.getType().equals(Material.AIR)) continue;

                    data.set(path + ".type", storedBlock.getType().toString());

                    if (blockState instanceof Directional directional)
                        data.set(path + ".facing", directional.getFacing().toString());
                    if (blockState.getBlockData() instanceof Rotatable rotation)
                        data.set(path + ".rotation", rotation.getRotation().toString());
                    if (blockState.getBlockData() instanceof Lantern lantern)
                        data.set(path + ".hanging", lantern.isHanging());
                    if (blockState.getBlockData() instanceof Slab slab)
                        data.set(path + ".slabType", slab.getType().toString());
                    if (blockState.getBlockData() instanceof Fence fence) {
                        Set<BlockFace> faces = fence.getFaces();
                        List<String> connectedFaces = faces.stream().map(Enum::name).collect(Collectors.toList());
                        data.set(path + ".connectedFaces", connectedFaces);
                    }
                    if (blockState.getBlockData() instanceof Wall wall) {
                        data.set(path + ".wallNorth", wall.getHeight(BlockFace.NORTH).name());
                        data.set(path + ".wallSouth", wall.getHeight(BlockFace.SOUTH).name());
                        data.set(path + ".wallEast", wall.getHeight(BlockFace.EAST).name());
                        data.set(path + ".wallWest", wall.getHeight(BlockFace.WEST).name());
                        data.set(path + ".wallIsUp", wall.isUp());
                    }
                }
            }
        }

        data.save();

        Function.sendPlayerSystemMessage(player, "名为 " + args[1] + " 的塔的信息已经全部存储完毕");
    }


    private void executeSummonTextDisplay(String[] args, Player player) {
        TextDisplay text = player.getWorld().spawn(player.getLocation(),TextDisplay.class);
        text.setText(Function.changeColorCharacters(args[1]));
        text.setAlignment(TextDisplay.TextAlignment.CENTER);
        text.setLineWidth(2000);
        text.addScoreboardTag("QinText");
    }

    private void executeExecuteCommandPlayer(Player player, String[] args) {
        Player targetPlayer = Bukkit.getServer().getPlayer(args[1]);
        if(targetPlayer != null) {
            StringBuilder command = new StringBuilder();
            for(int i = 2; i < args.length; i++)
                command.append(args[i]).append(" ");
            command = new StringBuilder(command.substring(0, command.length() - 1));
            targetPlayer.performCommand(command.toString());
        }else {
            player.sendMessage("玩家" + args[1] + "不存在");
        }
    }

    private void executePlayerBuyKillEffect(Player player, String[] args) {
        try {
            String effectName = args[1];
            String name = plugin.getMySQLDataBase().getKillEffectNameFromIDName(effectName);
            if(plugin.getMySQLDataBase().ifPlayerHasKillEffect(player,effectName)) {
                Function.sendPlayerSystemMessage(player, "&3你已经拥有 " + name + " 特效了！=w=");
                return;
            }
            int effectPrice;
            effectPrice = plugin.getMySQLDataBase().getKillEffectPrice(effectName);
            if(effectPrice == -1) Function.sendPlayerSystemMessage(player, "&3该特效不存在或无法购买！ &b>A<");
            else {
                int money = PlayerDataSave.getPlayerMoneyRecord(player);
                if(money >= effectPrice) {
                    PlayerDataSave.updatePlayerMoneyRecord(player, money - effectPrice);
                    plugin.getMySQLDataBase().givePlayerKillEffect(player,effectName);
                    Function.sendPlayerSystemMessage(player, "&3购买成功 &bOwO " + name);
                }else {
                    Function.sendPlayerSystemMessage(player, "&3你没有足够的小钱钱钱购买该特效！ &bO^O");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void executePlayerBuyKillSound(Player player, String[] args) {
        String soundID = args[1];
        if(DataBaseCache.getPlayerOwnKillSounds(player).contains(soundID)) {
            Function.sendPlayerSystemMessage(player, "&3你已经拥有 " + DataBaseCache.getKillSoundColumns().get(soundID).get("name") + " 特效了！=w=");
            return;
        }
        try{
            DataBaseCache.addPlayerOwnKillSound(player, soundID);
            Function.sendPlayerSystemMessage(player, "&3购买成功 &bOwO " + DataBaseCache.getKillSoundColumns().get(soundID).get("name"));
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage("系统数据库出错了xwx，之后再来吧");
        }

    }

    private void executeClearPlayerKillEffects(Player player) {
        try {
            plugin.getMySQLDataBase().removePlayerEquipKillEffect(player);
            Function.sendPlayerSystemMessage(player, "&3取消装备了你的击杀特效捏");
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeClearPlayerTitle(Player player) {
        try {
            plugin.getMySQLDataBase().removePlayerEquipTitle(player);
            Function.sendPlayerSystemMessage(player, "&3取消装备了你的头衔捏");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeGiveFreeKitToPlayer(Player player, String[] args) {
        String kitName = args[1];
        try {
            if(plugin.getMySQLDataBase().ifPlayerHasKit(player,kitName))
                Function.sendPlayerSystemMessage(player, "&3你已经拥有这个职业了啊喂 &b=^=");
            else {
                plugin.getMySQLDataBase().givePlayerKit(player,kitName);
                Function.sendPlayerSystemMessage(player, "&3你获得了职业 &b&l" + plugin.getMySQLDataBase().getKitNameFromFileName(kitName) + " &3=w=");
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeOpenMenuCommandSelf(Player player, String[] args) {
        String menuID = args[1];

        QinMenu qinMenu = game.getQinMenus().getQinMenuFromQinMenuID(menuID);
        if(qinMenu == null) return;

        player.openInventory(QinMenusDataSave.getMenuInventory(qinMenu, player));
    }

    private void executeAddPlayerPrefix(Player player, String[] args) {
        try {
            plugin.getMySQLDataBase().givePlayerTitle(player, args[1]);
            Function.sendPlayerSystemMessage(player,"&3你获得了新的称号！" + plugin.getMySQLDataBase().getContentFromTitleID(args[1]) + " (〃'▽'〃)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void executeApplyPlayerPrefix(Player player, String[] args) {
        try {
            if(plugin.getMySQLDataBase().ifPlayerHasTitle(player, args[1])) {
                PlayerDataSave.setPlayerTextDataRecord(player, "playerPrefix", plugin.getMySQLDataBase().getContentFromTitleID(args[1]));
                plugin.getMySQLDataBase().setPlayerEquipTitle(player,args[1]);
            }else Function.sendPlayerSystemMessage(player, "&3你并没有这个称号！");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //玩家随机选择职业指令
    private void kitRandomKit(Player player) {
        ArrayList<QinKit> passedKits = new ArrayList<>();
        for(String str : configs.getPluginDirectoryFiles("Kits",false)){
            QinKit kit = game.getKits().getKitByName(str);
            if(!kit.getAvailable()) continue;
            if(!player.hasPermission(game.getKits().getKitByName(str).getPermission())) continue;
            passedKits.add(kit);
        }
        Random random = new Random();
        int chance = random.nextInt(passedKits.size());
        passedKits.get(chance).apply(player);
    }

    //玩家随机抽奖职业指令
    private void rollRandomKit(Player player) {
        try {
            if(DataBaseCache.getPlayerKeys(player) <= 0) return;
            if(DataBaseCache.getPlayerNotOwnKits(player).isEmpty()) {
                Function.sendPlayerSystemMessage(player,"&3你已经拥有所有职业啦&b qwq");
                return;
            }
            DataBaseCache.setPlayerOwnKeys(player,DataBaseCache.getPlayerKeys(player)-1);
            int max = DataBaseCache.getPlayerNotOwnKits(player).size();
            Random random = new Random();
            int chance = random.nextInt(max);
            String kitName = DataBaseCache.getPlayerNotOwnKits(player).get(chance);

            Function.sendPlayerSystemMessage(player, "&3你获得了职业 &b&l" + DataBaseCache.getKitColumns().get(kitName).get("name") + " &3=w=");

            DataBaseCache.addPlayerOwnKit(player,kitName);

            String rare = plugin.getMySQLDataBase().getKitRareFromFileName(kitName);
            switch (rare) {
                case "普通" ->
                {
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getEyeLocation(), 50, 0.2, 0.2, 0.2, 0.5);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT,1,1.0F);
                }
                case "稀有" ->
                {
                    player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, player.getEyeLocation(), 50, 0.2, 0.2, 0.2, 0.5);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT,1,1.2F);
                }
                case "高级" ->
                {
                    player.getWorld().spawnParticle(Particle.FLAME, player.getEyeLocation(), 50, 0.2, 0.2, 0.2, 0.5);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT,1,1.4F);
                }
                case "史诗" -> {
                    player.getWorld().spawnParticle(Particle.SOUL, player.getEyeLocation(), 50, 0.2, 0.2, 0.2, 0.5);
                    player.getWorld().spawnParticle(Particle.WITCH, player.getEyeLocation(), 50, 0.2, 0.2, 0.2, 0.5);
                    player.getWorld().spawnParticle(Particle.ENCHANT, player.getEyeLocation(), 50, 0.2, 0.2, 0.2, 0.5);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT,1,1.6F);
                }
                case "传奇" ->
                {
                    player.getWorld().spawnParticle(Particle.LAVA, player.getEyeLocation(), 50, 0.2, 0.2, 0.2, 0.5);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT,1,1.8F);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //更换地图指令
    private void executeChangeMapCommandSelf(Player player, String[] args) {
        int mapID = Integer.parseInt(args[1]);
        MapRunnable.changeMap(mapID);
        player.sendMessage("[QinKitPVPS] 地图已经被变更！(Ov<)");
    }

    //玩家增加特效指令
    private void executeAddPlayerKillEffects(Player player, String[] args) {
        try {
            plugin.getMySQLDataBase().givePlayerKillEffect(player,args[1]);
            Function.sendPlayerSystemMessage(player,"&3你获得了新的特效 &b" + args[1]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //玩家击杀特效生效指令
    private void executeApplyPlayerKillEffects(Player player, String[] args) {
        try {
            if(plugin.getMySQLDataBase().ifPlayerHasKillEffect(player,args[1])) {
                PlayerDataSave.setPlayerTextDataRecord(player,"killEffects",args[1]);
                plugin.getMySQLDataBase().setPlayerEquipKillEffect(player, args[1]);
                Function.sendPlayerSystemMessage(player, "&3你装备了特效 &b" + plugin.getMySQLDataBase().getKillEffectNameFromIDName(args[1]));
            }else Function.sendPlayerSystemMessage(player,"你并没有该特效哦qwq");
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void executeApplyPlayerKillSound(Player player, String[] args) {
        if(!DataBaseCache.getPlayerOwnKillSounds(player).contains(args[1])) {
            Function.sendPlayerSystemMessage(player,"你并没有该特效哦qwq"); return;
        }
        DataBaseCache.setPlayerEquipKillSound(player,args[1]);
        Function.sendPlayerSystemMessage(player,"你装备了特效 &b" + DataBaseCache.getKillSoundColumns().get(args[1]).get("name"));
    }

    //更改地图天气指令
    private void executeChangeWeatherCommandSelf(Player player, String[] args) {
        try{
            int weatherChance = Integer.parseInt(args[1]);
            WeatherRunnable.changeWeather(weatherChance);
            player.sendMessage("[QinKitPVPS] 天气已经被变更！(Ov<)");
        } catch (NumberFormatException e) {
            Function.sendPlayerSystemMessage(player,"§3都特么说了输入数字！欠打是吧 §b>^< §3!!");
        }
    }

    //队伍出生点传送指令
    private void executeTpTeamedSpawnCommandWithOutInfoSelf(Player player) {
        randomTeamMapID = plugin.getRandomTeamMapID();
        QinMap qinMap = game.getMaps().getTeamedQinMapByMapID(randomTeamMapID);
        if(qinMap != null) {
            Random random = new Random();
            int randomNumber = random.nextInt(game.getMaps().getTeamedQinMapByMapID(randomTeamMapID).getLocation().size());
            String str = String.valueOf(randomNumber);
            if (game.getMaps().getTeamedQinMapByMapID(randomTeamMapID).getLocation().get(str) != null) {
                player.teleport(game.getMaps().getTeamedQinMapByMapID(randomTeamMapID).getLocation().get(str));
                player.addScoreboardTag("InTeamedGaming");
            }else{
                player.sendMessage("[QinKitPVPS] 出错了(xwx)");
            }
            tpMixedGameFalseInformation(player, randomTeamMapID);
        }else{
            player.sendMessage("[QinKitPVPS] 没有这个地图啊(━△━)");
        }
    }

    //玩家传送到模式出生点指令
    private void executeTpModeSpawnCommandSelf(Player player, String[] args) {
        int modeID = Integer.parseInt(args[1]);
        if(modeID == 0){
            executeTpRandomSpawnCommandSelf(player);
        }else if(modeID == 1){
            executeTpTeamedModeSpawnCommand(player);
        }
    }

    private void executeTpTeamedModeSpawnCommand(Player player) {
        QinMap qinMap = game.getMaps().getTeamedQinMapByMapID(0);
        if(qinMap != null){
            Random random = new Random();
            int randomNumber = random.nextInt(game.getMaps().getTeamedQinMapByMapID(0).getLocation().size());
            String str = String.valueOf(randomNumber);
            if(game.getMaps().getTeamedQinMapByMapID(0).getLocation().get(str) != null){
                player.teleport(game.getMaps().getTeamedQinMapByMapID(0).getLocation().get(str));
                Function.playerReset(player);
                player.addScoreboardTag("InTeamedGame");
                List<String> commands = new ArrayList<>();
                commands.add("console: tellraw @a [\"\\u00a77> > > \\u00a73\\u00a7l玩家 \\u00a7b\\u00a7l"+player.getName()+" \\u00a73\\u00a7l进入了团队游戏模式战场 \\u00a77< < <\"]");
                commands.add("console: execute at %player% run tp %player% ~ ~ ~ 270 0");
                Function.executeCommands(player,commands,"none","none");
                player.setInvulnerable(true);
            }else{
                player.sendMessage("[QinKitPVPS] 出错了(xwx)");
            }
            if(game.getMaps() == null){player.sendMessage("地图都没获取到");}
            if(game.getMaps().getTeamedQinMapByMapID(0) == null){player.sendMessage("TeamedQinMap没有获取到");}
            if(game.getMaps().getTeamedQinMapByMapID(0).getLocation().isEmpty()){player.sendMessage("位置没有获取到");}
        }else{
            player.sendMessage("[QinKitPVPS] 没有这个地图啊(━△━)");
        }
    }

    private void executeTpRandomSpawnCommandWithOutInfoSelf(Player player) {
        randomMapID = plugin.getRandomMapID();
        executeTpSpawnCommandWithoutInfo(player, randomMapID);
    }

    private void executeTpRandomSpawnCommandSelf(Player player) {
        randomMapID = plugin.getRandomMapID();
        executeTpSpawnCommand(player, randomMapID);
    }

    private void executeTpSpawnCommandSelf(Player player, String[] args) {
        int mapID = Integer.parseInt(args[1]);
        executeTpSpawnCommand(player, mapID);
    }
    private void executeTpSpawnCommand(Player player, int randomMapID) {
        QinMap qinMap = game.getMaps().getQinMapByMapID(randomMapID);
        if(qinMap != null){
            Random random = new Random();
            int randomNumber = random.nextInt(game.getMaps().getQinMapByMapID(randomMapID).getLocation().size());
            String str = String.valueOf(randomNumber);
            if(game.getMaps().getQinMapByMapID(randomMapID).getLocation().get(str) != null){
                player.teleport(game.getMaps().getQinMapByMapID(randomMapID).getLocation().get(str));
                player.addScoreboardTag("InMixedGame");
                player.setInvulnerable(false);
                player.setSaturation(5);
                player.setFoodLevel(20);
                player.removeScoreboardTag("QinKitLobby");
                List<String> commands = new ArrayList<>();
                commands.add("console: tellraw @a [\"\\u00a77> > > \\u00a73\\u00a7l玩家 \\u00a7b\\u00a7l"+player.getName()+" \\u00a73\\u00a7l进入了战场 \\u00a77< < <\"]");
                Function.executeCommands(player,commands,"none","none");
            }else{
                player.sendMessage("[QinKitPVPS] 出错了(xwx)");
            }
            tpMixedGameFalseInformation(player, randomMapID);
        }else{
            player.sendMessage("[QinKitPVPS] 没有这个地图啊(━△━)");
        }
    }

    private void tpMixedGameFalseInformation(Player player, int randomMapID) {
        if(game.getMaps() == null){player.sendMessage("地图都没获取到");}
        if(game.getMaps().getQinMapByMapID(randomMapID) == null){player.sendMessage("QinMap没有获取到");}
        if(game.getMaps().getQinMapByMapID(randomMapID).getLocation().isEmpty()){player.sendMessage("位置没有获取到");}
    }

    private void executeTpSpawnCommandWithoutInfo(Player player, int randomMapID) {
        QinMap qinMap = game.getMaps().getQinMapByMapID(randomMapID);
        if(qinMap != null) {
            Random random = new Random();
            int randomNumber = random.nextInt(game.getMaps().getQinMapByMapID(randomMapID).getLocation().size());
            String str = String.valueOf(randomNumber);
            if (game.getMaps().getQinMapByMapID(randomMapID).getLocation().get(str) != null) {
                player.teleport(game.getMaps().getQinMapByMapID(randomMapID).getLocation().get(str));
                player.addScoreboardTag("InMixedGame");
            }else{
                player.sendMessage("[QinKitPVPS] 出错了(xwx)");
            }
            tpMixedGameFalseInformation(player, randomMapID);
        }else{
            player.sendMessage("[QinKitPVPS] 没有这个地图啊(━△━)");
        }
    }

    private void executeKitCommandSelf(Player player, String[] args) {
        String kitName = args[1];
        QinKit kitToGive = game.getKits().getKitByName(kitName);
        if(kitToGive != null){
            game.getKits().giveQinKitToPlayer(player, kitToGive);
        }else{
            player.sendMessage("[QinKitPVPS] 咱文件里没有这个职业哟(O^O;)");
        }
    }
    private void executeShowKitIntroductionCommandSelf(Player player, String[] args){
        String kitName = args[1];
        QinKit kitToShow = game.getKits().getKitByName(kitName);
        if(kitToShow != null){
            kitToShow.showIntroduction(player);
        }else{
            player.sendMessage("[QinKitPVPS] 咱文件里没有这个职业哟(O^O;)");
        }
    }
    private void executeBuyKitCommandSelf(Player player, String[] args) {
        String kitName = args[1];
        QinKit qinKit = game.getKits().getKitByName(kitName);
        String permission = qinKit.getPermission();
        if(player.hasPermission(permission)){
            player.sendMessage("§7> > §b§lQinKitPVPS §7--> §3你已经有这个职业了哦 §b(*╹▽╹*)");
        }else{
            if(PlayerDataSave.getPlayerMoneyRecord(player) < qinKit.getPrice()){
                player.sendMessage("§7> > §b§lQinKitPVPS §7--> §3你的小钱钱不够惹 §b(=^=)");
            }else{
                try {
                    Function.executeCommand(player,"console: lp user " + player.getName() + " permission set " + qinKit.getPermission() + " true");
                    int money = PlayerDataSave.getPlayerMoneyRecord(player);
                    money = money - qinKit.getPrice();
                    plugin.getSQLiteDatabase().updatePlayerMoney(player,money);
                    PlayerDataSave.updatePlayerMoneyRecord(player,money);
                    player.sendMessage("§7> > §b§lQinKitPVPS §7--> §3获得了新的职业！ §b§l" + qinKit.getName() + " §b(〃'▽'〃)");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
