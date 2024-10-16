package org.NoiQing.EventListener.System;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.util.WeatherDataSave;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class WeatherEventListener implements Listener {
    private final QinKitPVPS plugin;
    public WeatherEventListener(QinKitPVPS plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void playerHurtEvent(EntityDamageByEntityEvent event){
        String currentWeather = WeatherDataSave.getWeatherStorage().get(Bukkit.getWorld("world"));
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if(currentWeather.equals("§7台风")){
            if(entity instanceof LivingEntity livingEntity){
                livingEntity.setVelocity(entity.getVelocity().add(damager.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize()).multiply(-2.0));
            }
        }

        //提升TNT的爆炸击退能力
        if(entity instanceof LivingEntity livingEntity){
            if(damager instanceof TNTPrimed tntPrimed && !tntPrimed.getScoreboardTags().contains("allay_damage") && !tntPrimed.getScoreboardTags().contains("cannon_tnt")) {
                livingEntity.setVelocity(entity.getVelocity().add(damager.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize()).multiply(-1.5));
            } else if (damager instanceof LargeFireball) {
                livingEntity.setVelocity(entity.getVelocity().add(damager.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize()).multiply(-1.1));
            }
        }
    }
}
