package commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import com.virtualparticle.mc.mckoth.McKoth;
import commands.exceptions.IllegalSenderException;
import commands.exceptions.IncorrectUsageException;
import commands.exceptions.PluginCommandException;
import commands.exceptions.MalformedCommandException;
import map.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import utils.WorldEditUtils;

public class CommandKothMap extends PluginCommand {


    protected CommandKothMap(String name) {
        super(name);
    }

    @Override
    public boolean onPluginCommand(CommandSender sender, Command command, String label, String[] args) throws PluginCommandException {

        if (args.length == 0) {
            throw new IncorrectUsageException(this);
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

        }

        if (args[0].equalsIgnoreCase("create")) {

            if (!(sender instanceof Player)) {
                throw new IllegalSenderException(sender);
            }

            Player p = (Player) sender;

            if (args.length < 2) {
                throw new MalformedCommandException(i18n.getString("commandKothMapCreateUsage"));
            }

            String name = args[1];

            WorldEditPlugin worldEdit = plugin.getWorldEdit();
            Region region = WorldEditUtils.getSelectionRegion(p);
            if (region == null) {
                throw new PluginCommandException(i18n.getString("invalidWorldEditSelection"));
            }


        }

        return true;

    }

    @Override
    public String getUsage() {
        return i18n.getString("commandLabelUsage") + ": " + i18n.getString("command" + name + "Usage");
    }

}
