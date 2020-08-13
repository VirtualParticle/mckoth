package commands.exceptions;

import commands.PluginCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IllegalSenderException extends PluginCommandException{

    public IllegalSenderException(CommandSender sender) {
        super(i18n.getString("cantRunFrom" + (sender instanceof Player ? "Player" : "Console")));
    }

}
