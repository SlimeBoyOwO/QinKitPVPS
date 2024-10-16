package org.NoiQing.mainGaming;

import org.NoiQing.QinKitPVPS;
import org.NoiQing.api.QinSkill;
import org.NoiQing.util.Configuration;
import org.NoiQing.util.CreateFileConfig;
import org.NoiQing.util.Function;
import org.NoiQing.util.QinSkillsDataSava;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class QinSkills {
    private final QinKitPVPS plugin;
    private final CreateFileConfig skillConfigs;
    private final Map<String, String> skills;
    public QinSkills(QinKitPVPS plugin, Game game){
        this.plugin = plugin;
        this.skillConfigs = plugin.getResource();

        this.skills = new HashMap<>();
    }

    public QinSkill getSkillByName(String skillName){
        return loadSkillFromCacheOrCreate(skillName);
    }

    private QinSkill loadSkillFromCacheOrCreate(String skillName) {
        if(!QinSkillsDataSava.getSkillsCache().containsKey(skillName)){
            List<String> skillList = skillConfigs.getPluginDirectoryFiles("Skills",false);
            List<String> skillListNoRemark =  new ArrayList<>();
            for(String s : skillList){
                skillListNoRemark.add(Function.getNameRemovedNotes(s));
            }
            if (skillListNoRemark.contains(skillName)) {
                Configuration skill = skillConfigs.getSkill(skillName);
                try {
                    QinSkillsDataSava.getSkillsCache().put(skillName,createSkillFromResource(skill));
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return QinSkillsDataSava.getSkillsCache().get(skillName);
    }

    private QinSkill createSkillFromResource(Configuration resource) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        QinSkill skill = new QinSkill(trimName(resource.getName()));
        skill.setActivatorName(resource.contains("Activator.Name") ? resource.getString("Activator.Name") : "Error错误");
        skill.setCoolDown(resource.contains("CoolDown.CoolDown") ?
                Integer.parseInt(Objects.requireNonNull(resource.getString("CoolDown.CoolDown")).replaceAll("S","")) : 1);
        skill.setCommands(resource.contains("Commands") ? resource.getStringList("Commands") : null);
        if (resource.contains("Sound")) {
            Sound skillSound = Function.safeSound(resource.getString("Sound.Sound"));
            int skillSoundPitch = resource.getInt("Sound.Pitch");
            int skillSoundVolume = resource.getInt("Sound.Volume");

            skill.setSound(skillSound, skillSoundPitch, skillSoundVolume);
        }
        if(resource.contains("Effects")){
            ConfigurationSection effectSection = resource.getConfigurationSection("Effects");

            if (effectSection != null) {
                for (String effectName : effectSection.getKeys(false)) {
                    PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.minecraft(effectName.toLowerCase()));
                    if(effectType == null){
                        effectType = PotionEffectType.HUNGER;
                    }
                    int amplifier = resource.getInt("Effects." + effectName + ".Amplifier");
                    int duration = resource.getInt("Effects." + effectName + ".Duration");

                    skill.setEffect(effectType, amplifier, duration);
                }
            }
        }
        return skill;
    }
    private String trimName(String kitNameWithFileEnding) {
        String[] splitName = kitNameWithFileEnding.split(".yml");
        return splitName[0];
    }
}
