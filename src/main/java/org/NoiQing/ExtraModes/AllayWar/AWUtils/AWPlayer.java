package org.NoiQing.ExtraModes.AllayWar.AWUtils;

import org.NoiQing.QinKitPVPS;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AWPlayer {
    private static final Map<Player, Integer> playerAWMoney = new HashMap<>();
    private static final Map<Entity, Player> playerRaider = new HashMap<>();
    private static final Map<Player, Location> playerSelectLocation = new HashMap<>();
    private static final Map<Player, Set<Mob>> playerSelectedEntities = new HashMap<>();
    private static final Map<Player, ItemStack[]> playerTempInventory = new HashMap<>();

    public static void setPlayerAWMoney(Player p, int money) {
        QinKitPVPS.getPlugin().getGames().getAllayGame().setPlayerTeamMoney(p, money);
        // playerAWMoney.put(p, money);
    }
    public static int getPlayerAWMoney(Player p) {
        return QinKitPVPS.getPlugin().getGames().getAllayGame().getPlayerTeamMoney(p);
    }

    public static void setPlayerRaider(Entity e, Player p){
        playerRaider.put(e, p);
    }
    public static Player getPlayerRaider(Entity e){
        return playerRaider.getOrDefault(e, null);
    }
    public static void removePlayerRaider(Entity e){
        playerRaider.remove(e);
    }

    public static void setPlayerSelectLocation(Player p, Location l){
        playerSelectLocation.put(p, l);
    }
    public static Location getPlayerSelectLocation(Player p){
        return playerSelectLocation.getOrDefault(p, null);
    }
    public static void removePlayerSelectLocation(Player p){
        playerSelectLocation.remove(p);
    }

    public static void setPlayerSelectedEntities(Player p, Set<Mob> entities){
        playerSelectedEntities.put(p, new HashSet<>());
        playerSelectedEntities.get(p).addAll(entities);
    }
    public static Set<Mob> getPlayerSelectedEntities(Player p){
        return playerSelectedEntities.getOrDefault(p, null);
    }
    public static void removePlayerSelectedEntities(Player p){
        playerSelectedEntities.remove(p);
    }

    public static void setPlayerTempInventory(Player p, ItemStack[] inv){
        ItemStack[] items = new ItemStack[inv.length];
        System.arraycopy(inv, 0, items, 0, inv.length);
        playerTempInventory.put(p, items);
    }
    public static ItemStack[] getPlayerTempInventory(Player p){
        return playerTempInventory.getOrDefault(p, null);
    }
}
