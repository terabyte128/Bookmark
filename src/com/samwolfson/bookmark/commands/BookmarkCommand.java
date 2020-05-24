package com.samwolfson.bookmark.commands;

import com.samwolfson.bookmark.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BookmarkCommand implements TabExecutor {
    Bookmark plugin;

    public BookmarkCommand(Bookmark plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You must be a player to use this command.");
            return false;
        }

        Player p = (Player) commandSender;
        ConfigManager playerConfig = new ConfigManager(p, plugin);

        // bad
        if (args.length < 1) {
            p.sendMessage("Usage:");
            p.sendMessage("  /" + label + " set <name>: set a new bookmark");
            p.sendMessage("  /" + label + " [list|ls]: show all bookmarks");
            p.sendMessage("  /" + label + " del <name>: delete bookmark");
            p.sendMessage("  /" + label + " nav <name>: navigate to bookmark");
            p.sendMessage("  /" + label + " [clear|clr]: clear navigation");
            return false;
        }

        if (args.length == 1) {
            if (args[0].equals("list") || args[0].equals("ls")) {

                if (playerConfig.getSavedLocations().isEmpty()) {
                    p.sendMessage("Sadly, you have no bookmarked locations. Add some using /" + label + " set <name>");
                } else {
                    p.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Saved Locations");
                    StringBuilder b = new StringBuilder();

                    Map<String, Location> savedLocations = playerConfig.getSavedLocations();

                    List<NamedLocation> thisWorld = new ArrayList<>();
                    List<NamedLocation> otherWorlds = new ArrayList<>();

                    // separate into two lists, one for this world, one for not this world
                    for (Map.Entry<String, Location> location : savedLocations.entrySet()) {
                        NamedLocation namedLocation = new NamedLocation(location.getKey(), location.getValue());
                        if (Objects.equals(location.getValue().getWorld(), p.getWorld())) {
                            thisWorld.add(namedLocation);
                        } else {
                            otherWorlds.add(namedLocation);
                        }
                    }

                    // sort locations in this world by distance to the player
                    thisWorld.sort((o1, o2) -> (int) (o1.getLocation().distanceSquared(p.getLocation()) - o2.getLocation().distanceSquared(p.getLocation())));

                    // sort locations in other worlds by name
                    otherWorlds.sort(Comparator.comparing(NamedLocation::getName));

                    for (NamedLocation l : thisWorld) {
                        b.append("    ").append(l.getName()).append("    ")
                                .append(Bookmark.prettyLocation(l.getLocation()))
                                .append("\n");
                    }

                    for (NamedLocation l : otherWorlds) {
                        String worldName = "???";
                        if (l.getLocation().getWorld() != null) {
                            worldName = l.getLocation().getWorld().getName();
                        }

                        b.append("    ").append(l.getName()).append("    ")
                                .append(Bookmark.prettyLocation(l.getLocation()))
                                .append(" (").append(ChatColor.RED)
                                .append(worldName)
                                .append(ChatColor.RESET).append(")")
                                .append("\n");
                    }

                    p.sendMessage(b.toString());
                }
            }

            else if (args[0].equals("clear") || args[0].equals("clr")) {
                PlayerNavTask.removePlayer(p);
                p.sendTitle("", "", 0, 0, 0);
            }

            else {
                p.sendMessage(ChatColor.BOLD + args[0] + ChatColor.RESET + " appears to be invalid or incomplete.");
            }

        } else if (args.length == 2) {
            if (args[0].equals("set")) {
                if (playerConfig.hasLocation(args[1])) {
                    p.sendMessage(ChatColor.GREEN + "You've already used that as a location name. Delete it first using " + ChatColor.BOLD + "/bm del " + args[1]);
                } else if (args[1].equals(PlayerListener.LOCATION_NAME)) {
                    p.sendMessage(ChatColor.GREEN + "You can't use that as a location, since it is reserved for your death location.");
                } else {
                    Location pl = p.getLocation();
                    p.sendMessage(ChatColor.GREEN + "Added bookmark " + ChatColor.BOLD + args[1] + ChatColor.RESET + " for " + ChatColor.BOLD + Bookmark.prettyLocation(pl) + ChatColor.RESET + ChatColor.GREEN + ".");
                    playerConfig.addLocation(args[1], pl);
                    playerConfig.saveConfig();
                }
            }

            else if (args[0].equals("del")) {
                if (playerConfig.hasLocation(args[1])) {
                    playerConfig.removeLocation(args[1]);
                    p.sendMessage(ChatColor.GREEN + "Removed bookmark " + ChatColor.BOLD + args[1] + ChatColor.RESET + ChatColor.GREEN + ".");
                } else {
                    p.sendMessage(ChatColor.GREEN + "Unfortunately, " + ChatColor.BOLD + args[1] + ChatColor.RESET + ChatColor.GREEN + " does not exist.");
                }
                playerConfig.saveConfig();
            }

            else if (args[0].equals("nav")) {
                if (playerConfig.hasLocation(args[1])) {
                    Location desired = playerConfig.getLocation(args[1]);
                    if (Objects.equals(p.getLocation().getWorld(), desired.getWorld())) {
                        PlayerNavTask.addPlayer(p, desired);
                    } else {
                        p.sendMessage(ChatColor.GREEN + "You can't navigate to there since you aren't in the same world.");
                    }
                } else {
                    p.sendMessage(ChatColor.GREEN + "Unfortunately, " + ChatColor.BOLD + args[1] + ChatColor.RESET + ChatColor.GREEN + " does not exist.");
                }
            }

            else {
                p.sendMessage(ChatColor.BOLD + args[0] + " " + args[1] + ChatColor.RESET + " appears to be invalid or incomplete.");
            }

        } else {
            p.sendMessage("your command is invalid, but i don't know how. try reading the instructions");
        }
        return true;
    }

    /*
        Possible autocompletions:
        - /bookmark [list|ls}
        - /bookmark del <name>
        - /bookmark set <name>
        - /bookmark nav <name>
        - /bookmark [clear|clr]

        Note: args does not include the command alias
     */

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (!(commandSender instanceof Player)) {
            return null;
        }

        if (args.length == 0 || (args.length == 1 && args[0].isEmpty())) {
            return Arrays.asList("list", "del", "set", "nav", "clear");
        }

        else if (args.length == 1) {
            // ls or list
            if (args[0].startsWith("li")) {
                return Collections.singletonList("list");
            } else if (args[0].startsWith("l")) {
                return Arrays.asList("list", "ls");
            }

            // clr or clear
            else if (args[0].startsWith("c")) {
                return Arrays.asList("clear", "clr");
            } else if (args[0].startsWith("cle")) {
                return Collections.singletonList("clear");
            }

            else {
                return Arrays.asList("del", "set", "nav");
            }
        } else if (args.length == 2) {
            if (args[0].equals("del") || args[0].equals("nav")) {
                ConfigManager playerConfig = new ConfigManager((Player) commandSender, plugin);
                return new ArrayList<>(playerConfig.getSavedLocations().keySet());
            }
        }

        return null;
    }
}
