package commands;

import commands.exceptions.IncorrectUsageException;
import commands.exceptions.PluginCommandException;
import commands.exceptions.MalformedCommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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

        }

        return true;

    }

    @Override
    public String getUsage() {
        return i18n.getString("commandLabelUsage") + ": " + i18n.getString("command" + name + "Usage");
    }

}
