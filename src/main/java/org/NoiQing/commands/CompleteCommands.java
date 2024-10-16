package org.NoiQing.commands;

import org.NoiQing.QinKitPVPS;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompleteCommands implements TabCompleter {

    private static final List<String> tabs = new ArrayList<>();
    private static final List<String> noAdminTabs = new ArrayList<>(Arrays.asList("kill", "lobby"));
    public static List<String> getTabArryList() {
        return tabs;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("qinkit")){
            List<String> completions = new ArrayList<>();
            switch(args.length) {
                case 1 -> {
                    if(commandSender.isOp())
                        completions.addAll(tabs);
                    else completions.addAll(noAdminTabs);
                }
                case 2 -> {
                    if(args[0].equalsIgnoreCase("weather")) {
                        completions.add("1这里只能输入数字吼");
                        completions.add("2某只猪告诉你的");
                    }
                }
            }
            return completions;
        }
        return null;
    }
}
