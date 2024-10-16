package org.NoiQing.EventListener.System;

import org.NoiQing.AllayWar.AWAPI.AWRound;
import org.NoiQing.AllayWar.AWUtils.AWFunction;
import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinTeam;
import org.NoiQing.mainGaming.QinTeams;
import org.NoiQing.util.DataBaseCache;
import org.NoiQing.util.Function;
import org.NoiQing.util.PlayerDataSave;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class BornAndDeathListener implements Listener {
    private final QinKitPVPS plugin;

    public BornAndDeathListener(QinKitPVPS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        if(player == null) return;
        if(!player.getScoreboardTags().contains("InAWGaming")) Function.clearPlayerTeam(player);
        if(player.getScoreboardTags().contains("InMixedGame")) player.removeScoreboardTag("InMixedGame");
        if(player.getScoreboardTags().contains("InTeamedGaming")) player.addScoreboardTag("InTeamedGamingSPC");

        /*死亡音效*/
        playDeathSound(PlayerDataSave.getLastAttackPlayerRecord(player), event.getEntity().getLocation());
        String message = showMessage(event);
        Bukkit.broadcastMessage(message);

        PlayerDataSave.clearPlayerKitRecord(player);
    }

    private void playDeathSound(Player killer, Location loc) {
        if(killer == null) return;
        String killSound = DataBaseCache.getPlayerEquipKillSound(killer);

        assert loc.getWorld() != null;      //断言语句
        if(killSound == null) {
            loc.getWorld().playSound(loc,Sound.ENTITY_LIGHTNING_BOLT_IMPACT,1.2f,1.2f);
            return;
        }

        switch(killSound) {
            case "pig" -> loc.getWorld().playSound(loc,Sound.ENTITY_PIG_DEATH,1.2f,1.2f);
            case "wolf" -> loc.getWorld().playSound(loc,Sound.ENTITY_WOLF_DEATH,1.0f,1.0f);
            case "bell" -> loc.getWorld().playSound(loc,Sound.BLOCK_BELL_RESONATE,1.2f,1.2f);
            case "cat" -> loc.getWorld().playSound(loc,Sound.ENTITY_CAT_AMBIENT,1.2f,1.2f);
            default -> loc.getWorld().playSound(loc,Sound.ENTITY_LIGHTNING_BOLT_IMPACT,1.2f,1.2f);
        }
    }

    private String showMessage(PlayerDeathEvent event) {
        String message = "§e☠ §b>> §7";
        Player player = event.getEntity();
        Player killer = PlayerDataSave.getLastAttackPlayerRecord(player);
        if(killer == null){
            if(player.getLastDamageCause() == null)
                message = "§7莫名其妙的， §c"+ player.getName() + " §7洗掉惹";
            else{
                EntityDamageEvent.DamageCause cause = player.getLastDamageCause().getCause();
                switch (cause){
                    case FALL -> {
                        switch(getRandomChance(5)){
                            case 0 -> message += "哇哦，"+ showVictim(player) + "的大屁股碎裂了";
                            case 1 -> message += showVictim(player) + "的菊花被剧烈摔碎了OAO";
                            case 2 -> message += showVictim(player) + "认为自己有双隐形的翅膀";
                            case 3 -> message += "妈妈没有告诉你" + showVictim(player) + "是不会飞的吗？";
                            case 4 -> message += showVictim(player) + "似乎下落前忘了带鞘翅了";
                        }
                    }
                    case FIRE -> message = message + "好烧哦，"+ showVictim(player) + "因为太烧被烧死了";
                    case LAVA -> message = message + "大家都知道你很烧，"+ showVictim(player) + "但是你不用这么证明自己";
                    case LIGHTNING -> message = message + "下辈子别做亏心事遭雷劈了，"+showVictim(player);
                    case ENTITY_ATTACK -> message = message + "悲，" + showVictim(player) + "被强行异种奸了";
                    default -> message += showVictim(player) + "死了";
                }
            }
        }else{
            if(player.getLastDamageCause() == null)
                message = "§7虽然不知道为毛，但是 §c"+ player.getName() + " §7被§a " + killer.getName() +" §7杀掉惹";
            else{
                EntityDamageEvent.DamageCause cause = player.getLastDamageCause().getCause();
                switch (cause){
                    case ENTITY_ATTACK -> {
                        switch (getRandomChance(21)){
                            case 0 -> message += showVictim(player) + "被" + showKiller(killer) + "强碱到喵喵叫";
                            case 1 -> message += showVictim(player) + "被" + showKiller(killer) + "打出小珍珠";
                            case 2 -> message += showVictim(player) + "被" + showKiller(killer) + "全身按摩到高潮";
                            case 3 -> message += showVictim(player) + "被" + showKiller(killer) + "超市了";
                            case 4 -> message += showVictim(player) + "显然不知道" + showKiller(killer) + "是ta的主人";
                            case 5 -> message += showVictim(player) + "成为了" + showKiller(killer) + "的小星奴";
                            case 6 -> message += showVictim(player) + "被" + showKiller(killer) + "打的汗流浃背";
                            case 7 -> message += showVictim(player) + "被" + showKiller(killer) + "撅烂了";
                            case 8 -> message += showVictim(player) + "被" + showKiller(killer) + "打到哭哭qwq";
                            case 9 -> message += showVictim(player) + "被" + showKiller(killer) + "拍的嘤嘤叫";
                            case 10 -> message += showVictim(player) + "求" + showKiller(killer) + "放过他但是达咩捏";
                            case 11 -> message += showVictim(player) + "成为了" + showKiller(killer) + "的小受";
                            case 12 -> message += showVictim(player) + "暗恋" + showKiller(killer) + "被拒绝了";
                            case 13 -> message += showVictim(player) + "没有发现" + showKiller(killer) + "比他的还大";
                            case 14 -> message += showVictim(player) + "被" + showKiller(killer) + "嘎掉惹";
                            case 15 -> message += showVictim(player) + "请" + showKiller(killer) + "吃了国宴";
                            case 16 -> message += showVictim(player) + "被杰哥" + showKiller(killer) + "登dua郎了";
                            case 17 -> message += showVictim(player) + "被" + showKiller(killer) + "当小孩吃了";
                            case 18 -> message += showVictim(player) + "吞下了" + showKiller(killer) + "的大保健";
                            case 19 -> message += showVictim(player) + "被" + showKiller(killer) + "骗去吃香蕉";
                            case 20 -> message += showVictim(player) + "因为玩原神被" + showKiller(killer) + "杀了";
                            default -> message += showVictim(player) + "被" + showKiller(killer) + "强行灌成了小泡芙";
                        }
                    }
                    case LIGHTNING -> {
                        switch (getRandomChance(4)) {
                            case 0 -> message += "雷神之子"+ showKiller(killer) + "活活把" + showVictim(player) + "给劈死了";
                            case 1 -> message += "宙斯召唤"+ showKiller(killer) + "把" + showVictim(player) + "劈成沙贝";
                            case 2 -> message += "召唤光的力量！"+ showKiller(killer) + "用闪电把" + showVictim(player) + "高压制裁";
                            default -> message += "啪！"+ showKiller(killer) + "召唤闪电把" + showVictim(player) + "劈裂了";
                        }
                    }
                    case FIRE -> {
                        switch (getRandomChance(3)) {
                            case 0 -> message += showKiller(killer) + "看不下去小烧货" + showVictim(player) + "于是把他给超市了";
                            case 1 -> message += "小烧货" + showVictim(player) + "因为实在是太烧了被" + showKiller(killer) + "撅了";
                            default -> message += "烧比"+ showVictim(player) + "乱晃屁股被" + showKiller(player) + "按捺不住草饲了";
                        }
                    }
                    case LAVA -> {
                        switch (getRandomChance(3)) {
                            case 0 -> message += showKiller(killer) + "告诉" + showVictim(player) + "岩浆里可以洗澡";
                            case 1 -> message += "烧货" + showVictim(player) + "进岩浆里泡澡被" + showKiller(killer) + "抓奸了";
                            default -> message += showKiller(killer) + "眼睁睁地看着" + showVictim(player) + "变成一个烧鸡";
                        }
                    }
                    case FALL -> {
                        switch (getRandomChance(4)){
                            case 0 -> message += showKiller(killer) + "悄悄告诉" + showVictim(player) + "他有一双隐形的翅膀";
                            case 1 -> message += showVictim(player) + "在逃离" + showKiller(player) + "的时候没注意前面是悬崖";
                            case 2 -> message += showVictim(player) + "蹦蹦跳跳的远离了" + showKiller(killer) + "的世界";
                            default -> message += showVictim(player) + "因为" + showKiller(killer) + "摔碎了皮鼓";
                        }
                    }
                    default -> message += showVictim(player) + "被" + showKiller(killer) + "杀了";
                }
            }
        }
        return message;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if(Function.isPlayerParkour(player)) return;
        if (event.isBedSpawn()) player.setRespawnLocation(new Location(Bukkit.getWorld("world"), 984.5, 7, 998.5));

        if (!player.getScoreboardTags().contains("InTeamedGaming") && !player.getScoreboardTags().contains("InAWGaming")) {
            Function.playerReset(player);
            event.setRespawnLocation(new Location(Bukkit.getWorld("world"), 984.5, 7, 998.5).setDirection(new Vector(1, 0, 0)));
            player.setRotation(-90, 0);
            Function.playerTpLobby(player);
        } else if(player.getScoreboardTags().contains("InAWGaming")) {
            Location teleportLoc = playerRejoinAWWar(player);
            if(!event.getRespawnLocation().equals(teleportLoc)) event.setRespawnLocation(teleportLoc);
        }
    }

    public static Location playerRejoinAWWar(Player player) {
        //塔防战争玩家重生设定
        player.getInventory().clear();
        QinTeam team = QinTeams.getEntityTeam(player);
        if(team != null && AWRound.getTeamLevels(team.getTeamName()) != null && AWRound.getTeamLevels(team.getTeamName()).get("HaveBase") == 0) {
            player.setGameMode(GameMode.SPECTATOR);
            Function.sendPlayerSystemMessage(player,"§c主基地丢失！你已出局");
            Bukkit.broadcastMessage("§c"+team.getTeamName()+"§7 的玩家 §c"+ player.getName()+" §7已出局");
        }
        AWFunction.givePlayerRespawnItem(player);
        Location loc = AWFunction.getTeamRespawnPoint(AWFunction.getAWTeam(player));
        Location teleportLoc = new Location(Bukkit.getWorld("skyblock_copy"), loc.getX() + 0.5,loc.getY() + 0.5,loc.getZ() + 0.5);
        player.teleport(teleportLoc);
        Function.setPlayerMaxHealth(player,40);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,100,20,true));
        player.setInvulnerable(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setInvulnerable(false);
            }
        }.runTaskLater(QinKitPVPS.getPlugin(),5 * 20);
        return teleportLoc;
    }

    private String showVictim(Player player){
        return " §c"+player.getName() + " §7";
    }
    private String showKiller(Player killer){
        return " §a"+killer.getName()+"§7[§b"+PlayerDataSave.getPlayerKitRecord(killer)+"§7] ";
    }
    private int getRandomChance(int bound){
        Random random = new Random();
        return random.nextInt(bound);
    }
}
