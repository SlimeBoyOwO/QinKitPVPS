package org.NoiQing.util;

import org.NoiQing.QinKitPVPS;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class CreateFileConfig {
    private final QinKitPVPS plugin;
    private final Configuration config;
    private final Configuration data;
    private final Configuration pvzData;
    private final Configuration shop;
    private final Configuration saveItems;
    private final Map<String, Configuration> kitFiles;
    private final Map<String, Configuration> skillFiles;
    private final Map<String, Configuration> mixedMapFiles;
    private final Map<String, Configuration> teamedMapFiles;
    private final Map<String, Configuration> allayMapFiles;
    private final Map<String, Configuration> pvzMapFiles;
    private final Map<String, Configuration> menuFiles;
    public CreateFileConfig(QinKitPVPS plugin){
        this.plugin = plugin;
        this.config = new Configuration(plugin,"customConfig.yml");
        this.data = new Configuration(plugin,"data.yml");
        this.pvzData = new Configuration(plugin,"pvzData.yml");
        this.shop = new Configuration(plugin,"shop.yml");
        this.saveItems = new Configuration(plugin,"saveItems.yml");
        this.kitFiles= new HashMap<>();
        this.skillFiles = new HashMap<>();
        this.mixedMapFiles = new HashMap<>();
        this.teamedMapFiles = new HashMap<>();
        this.allayMapFiles = new HashMap<>();
        this.pvzMapFiles = new HashMap<>();
        this.menuFiles = new HashMap<>();

        if (!plugin.getDataFolder().exists()) {
            kitFiles.put("Fighter.yml", new Configuration(plugin, "Kits/Fighter.yml"));

            skillFiles.put("SpeedI.yml", new Configuration(plugin, "Skills/SpeedI.yml"));
        }
        load();
    }

    public void load(){
        config.reloadCustomConfig();
        config.copyDefaults();
        data.reloadCustomConfig();
        data.copyDefaults();
        pvzData.reloadCustomConfig();
        pvzData.copyDefaults();
        saveItems.reloadCustomConfig();
        saveItems.copyDefaults();
        shop.reloadCustomConfig();
        shop.copyDefaults();

        for (String fileName : getPluginDirectoryFiles("kits", true)) {
            if (!kitFiles.containsKey(fileName) && !fileName.startsWith(".")) {
                kitFiles.put(fileName, new Configuration(plugin, "kits/" + fileName));
            }
        }

        for (String fileName : getPluginDirectoryFiles("Skills", true)) {
            if (!skillFiles.containsKey(fileName) && !fileName.startsWith(".")) {
                skillFiles.put(Function.getNameRemovedNotes(fileName), new Configuration(plugin, "Skills/" + fileName));
            }
        }

        for (String fileName : getPluginDirectoryFiles("Menus", true)) {
            if (!menuFiles.containsKey(fileName) && !fileName.startsWith(".")) {
                menuFiles.put(Function.getNameRemovedNotes(fileName), new Configuration(plugin, "Menus/" + fileName));
            }
        }

        for (String fileName : getPluginDirectoryFilesLevelTwo("Maps","MixedMaps", true)) {
            if (!mixedMapFiles.containsKey(fileName) && !fileName.startsWith(".")) {
                mixedMapFiles.put(Function.getNameRemovedNotes(fileName), new Configuration(plugin, "Maps/MixedMaps/" + fileName));
            }
        }

        for (String fileName : getPluginDirectoryFilesLevelTwo("Maps","TeamedMaps", true)) {
            if (!teamedMapFiles.containsKey(fileName) && !fileName.startsWith(".")) {
                teamedMapFiles.put(Function.getNameRemovedNotes(fileName), new Configuration(plugin, "Maps/TeamedMaps/" + fileName));
            }
        }

        for (String fileName : getPluginDirectoryFilesLevelTwo("Maps","AllayMaps", true)) {
            if (!allayMapFiles.containsKey(fileName) && !fileName.startsWith(".")) {
                allayMapFiles.put(Function.getNameRemovedNotes(fileName), new Configuration(plugin, "Maps/AllayMaps/" + fileName));
            }
        }

        for (String fileName : getPluginDirectoryFilesLevelTwo("Maps","PvzMaps", true)) {
            if (!pvzMapFiles.containsKey(fileName) && !fileName.startsWith(".")) {
                pvzMapFiles.put(Function.getNameRemovedNotes(fileName), new Configuration(plugin, "Maps/PvzMaps/" + fileName));
            }
        }

        reloadFiles(kitFiles);
        reloadFiles(skillFiles);
        reloadFiles(mixedMapFiles);
        reloadFiles(teamedMapFiles);
        reloadFiles(allayMapFiles);
        reloadFiles(pvzMapFiles);
        reloadFiles(menuFiles);
    }
    public void reload(){load();}
    private void reloadFiles(Map<String, Configuration> files){
        files.values().forEach(Configuration::reloadCustomConfig);
        files.values().forEach(Configuration::copyDefaults);
    }

    public Configuration getConfig() { return config; }
    public Configuration getData() { return data; }
    public Configuration getPvzData() { return pvzData; }
    public Configuration getShop() { return shop; }
    public Configuration getSaveItems() { return saveItems; }
    public Configuration getMenu(String menuName) {
        if (menuFiles.containsKey(menuName + ".yml")) {
            return menuFiles.get(menuName + ".yml");
        }
        return null;
    }
    public Configuration getKit(String kitName) {
        if (kitFiles.containsKey(kitName + ".yml")) {
            return kitFiles.get(kitName + ".yml");
        }
        return null;
    }

    public Configuration getSkill(String skillName){
        if (skillFiles.containsKey(skillName + ".yml")) {
            return skillFiles.get(skillName + ".yml");
        }
        return null;
    }

    public Configuration getMixedMap(String stringMapID){
        if(mixedMapFiles.containsKey(stringMapID + ".yml")) {
            return mixedMapFiles.get(stringMapID + ".yml");
        }
        return null;
    }

    public Configuration getTeamedMap(String stringMapID){
        if(teamedMapFiles.containsKey(stringMapID + ".yml")) {
            return teamedMapFiles.get(stringMapID + ".yml");
        }
        return null;
    }

    public Configuration getAllayMap(String stringMapID){
        if(allayMapFiles.containsKey(stringMapID + ".yml")) {
            return allayMapFiles.get(stringMapID + ".yml");
        }
        return null;
    }
    public Configuration getPvzMap(String stringMapID){
        if(pvzMapFiles.containsKey(stringMapID + ".yml")) {
            return pvzMapFiles.get(stringMapID + ".yml");
        }
        return null;
    }
    public List<String> getPluginDirectoryFiles(String directoryName, boolean withFileEndings) {
        File folder = new File(plugin.getDataFolder().getAbsolutePath() + "/" + directoryName);
        return getDirectoryFilesFromPlugin(withFileEndings, folder);
    }
    public List<String> getPluginDirectoryFilesLevelTwo(String directoryName, String directoryName2, boolean withFileEndings) {
        File folder = new File(plugin.getDataFolder().getAbsolutePath() + "/" + directoryName + "/" + directoryName2);
        return getDirectoryFilesFromPlugin(withFileEndings, folder);
    }

    @NotNull
    private List<String> getDirectoryFilesFromPlugin(boolean withFileEndings, File folder) {
        List<String> fileList = new ArrayList<>();

        if (folder.exists() && folder.list() != null) {
            for (String fileName : Objects.requireNonNull(folder.list())) {
                fileList.add(withFileEndings ? fileName : fileName.split(".yml")[0]);
            }
        }
        return fileList;
    }

    public Map<String, Configuration> getMapFiles() {return mixedMapFiles;}
    public Map<String, Configuration> getTeamedMapFiles() {return teamedMapFiles;}


}
