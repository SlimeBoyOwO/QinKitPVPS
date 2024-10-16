package org.NoiQing.api;

import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class QinSkill {
    private final String name;
    private String activatorName;
    private int coolDown;
    private Sound sound;
    private int soundPitch;
    private int soundVolume;
    private final List<PotionEffect> effects;
    private List<String> commands;
    public QinSkill(String name){
        this.name = name;
        this.effects = new ArrayList<>();
    }

    public void setActivatorName(String s) {this.activatorName = s;}
    public void setCoolDown(int i){this.coolDown = i;}

    public void setCommands(List<String> commands){this.commands = commands;}
    public void setSound(Sound sound, int pitch, int volume) {
        this.sound = sound;
        this.soundPitch = pitch;
        this.soundVolume = volume;
    }

    public void setEffect(PotionEffectType type, int amplifier, int durationSeconds) {
        PotionEffect effect = new PotionEffect(type, durationSeconds * 20, amplifier - 1);
        effects.add(effect);
    }

    public String getName() {return name;}
    public int getCoolDown() {return coolDown;}
    public List<String> getCommands() {return commands;}
    public List<PotionEffect> getEffects() {return effects;}
    public String getActivatorName() {return activatorName;}
    public Sound getSoundType() {return sound;}
    public int getSoundPitch() {return soundPitch;}
    public int getSoundVolume() {return soundVolume;}
}
