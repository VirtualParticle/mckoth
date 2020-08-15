package commands;

import I18n.I18n;
import com.virtualparticle.mc.mckoth.McKoth;
import commands.exceptions.PluginCommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class PluginCommand implements CommandExecutor {

    protected final String name;
    protected final McKoth plugin;
    protected final I18n i18n = I18n.getInstance();

    protected PluginCommand(String name) {
        this.name = name;
        plugin = McKoth.getPlugin();
        plugin.getCommand(name).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        try {
            onPluginCommand(sender, command, label, args);
        } catch (PluginCommandException e) {
            sender.sendMessage(e.getMessage().replace(i18n.getString("commandPlaceholder"), name));
        }

        return true;

    }

    public abstract boolean onPluginCommand(CommandSender sender, Command command, String label, String[] args) throws PluginCommandException;

    public String getName() {
        return name;
    }

    public String getUsage() {
        return i18n.getString("commandLabelUsage") + ": " + i18n.getString("commandGenericUsage");
    }

}
