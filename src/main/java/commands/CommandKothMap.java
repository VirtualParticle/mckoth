package commands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import commands.exceptions.IllegalSenderException;
import commands.exceptions.IncorrectUsageException;
import commands.exceptions.PluginCommandException;
import commands.exceptions.MalformedCommandException;
import map.Map;
import map.capturePoint.CapturePoint;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import utils.WorldEditUtils;

import java.util.List;

public class CommandKothMap extends PluginCommand {


    protected CommandKothMap(String name) {
        super(name);
    }

    @Override
    public boolean onPluginCommand(CommandSender sender, Command command, String label, String[] args) throws PluginCommandException {

        if (args.length == 0) {
            throw new IncorrectUsageException(this);
        }

        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage(ChatColor.GOLD + i18n.getString("mapListTitle"));
            Map.getMaps().forEach(map -> sender.sendMessage("•" + map.getName()));
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {

            if (args.length < 2) {
                throw new MalformedCommandException(i18n.getString("noMapSpecified"));
            }

            Map map = Map.getMapByName(args[1]);
            if (map == null) {
                throw new MalformedCommandException(i18n.getString("mapNotFound", args[1]));
            }

            sender.sendMessage(ChatColor.GOLD + i18n.getString("mapInfoTitle", map.getName()));
            sender.sendMessage("Info placeholder"); // TODO: implement

            return true;

        }

        WorldEditPlugin worldEdit = plugin.getWorldEdit();

        if (args[0].equalsIgnoreCase("create")) {

            if (p == null) {
                throw new IllegalSenderException(sender);
            }

            if (args.length < 2) {
                throw new MalformedCommandException(i18n.getString("commandKothMapCreateUsage"));
            }

            String name = args[1];

            int teamCount = 2;
            if (args.length >= 3) {
                try {
                    teamCount = Integer.parseInt(args[2]);
                } catch (NumberFormatException ignored) {

                }
            }

            Region region = WorldEditUtils.getSelectionRegion(p);
            if (region == null) {
                throw new PluginCommandException(i18n.getString("invalidWorldEditSelection"));
            }

            Map map = new Map(region, name, teamCount);
            Map.startModifyingMap(map, p);
            p.sendMessage(i18n.getString("startCreatingMap", map.getName()));

            return true;

        }

        if (args[0].equalsIgnoreCase("modify")) {

            if (p == null) {
                throw new IllegalSenderException(sender);
            }

            if (args.length < 2) {
                throw new MalformedCommandException(i18n.getString("commandKothMapModifyUsage"));
            }

            Map map = Map.getMapByName(args[1]);
            if (map == null) {
                throw new PluginCommandException(i18n.getString("mapNotFound", args[1]));
            }

            Map.startModifyingMap(map, p);

            p.sendMessage(i18n.getString("startModifyingMap", map.getName()));

        }

        if (args[0].equalsIgnoreCase("add")) {

            if (p == null) {
                throw new IllegalSenderException(sender);
            }

            Map map = Map.getMapInModification(p);
            if (map == null) {
                throw new PluginCommandException(i18n.getString("notModifyingMap"));
            }

            if (args.length < 2) {
                throw new MalformedCommandException(i18n.getString("commandKothMapAddUsage"));
            }

            if (args[1].equalsIgnoreCase("spawn")) {

                int team;
                if (args.length < 3) {
                    throw new MalformedCommandException("commandKothMapAddSpawnUsage");
                }

                try {
                    team = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    throw new MalformedCommandException("commandKothMapAddSpawnUsage");
                }

                Region region = WorldEditUtils.getSelectionRegion(p);
                if (region == null) {
                    throw new PluginCommandException(i18n.getString("invalidWorldEditSelection"));
                }

                if (region.equals(map.getRegion()) && (args.length < 4 || !args[3].equalsIgnoreCase("override"))) {
                    throw new PluginCommandException(i18n.getString("regionSameAsMapRegion"));
                }

                boolean regionInUse = false;
                for (List<Region> spawnRegions : map.getSpawnRegions()) {
                    for (Region testRegion : spawnRegions) {
                        regionInUse = testRegion.equals(region);
                    }
                }

                if (regionInUse) {
                    throw new PluginCommandException(i18n.getString("spawnRegionInUse"));
                }

                map.addSpawnRegion(region, team);

                return true;

            }

            if (args[1].equalsIgnoreCase("point")) {

                Region region = WorldEditUtils.getSelectionRegion(p);
                if (region == null) {
                    throw new PluginCommandException(i18n.getString("invalidWorldEditSelection"));
                }

                if (region.equals(map.getRegion()) && (args.length < 4 || !args[3].equalsIgnoreCase("override"))) {
                    throw new PluginCommandException(i18n.getString("regionSameAsMapRegion"));
                }

                boolean regionInUse = false;
                for (List<Region> spawnRegions : map.getSpawnRegions()) {
                    for (Region testRegion : spawnRegions) {
                        regionInUse = testRegion.equals(region);
                    }
                }

                if (regionInUse) {
                    throw new PluginCommandException(i18n.getString("spawnRegionInUse"));
                }

                // TODO: allow for captime customization
                CapturePoint capturePoint = new CapturePoint(region);
                map.addCapturePoint(capturePoint);

                return true;

            }

            throw new IncorrectUsageException(this);

        }

        throw new IncorrectUsageException(this);

    }

    @Override
    public String getUsage() {
        return i18n.getString("commandLabelUsage") + ": " + i18n.getString("command" + name + "Usage");
    }

}
