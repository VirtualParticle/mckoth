package commands;

import com.virtualparticle.mc.mckoth.McKoth;
import commands.exceptions.IllegalSenderException;
import commands.exceptions.IncorrectUsageException;
import commands.exceptions.PluginCommandException;
import game.Game;
import game.GamePlayer;
import map.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKoth extends PluginCommand {

    private static final String NAME = "Koth";

    private final McKoth plugin;

    public CommandKoth() {
        super(NAME);
        plugin = McKoth.getPlugin();
    }

    @Override
    public boolean onPluginCommand(CommandSender sender, Command command, String label, String[] args) throws PluginCommandException {

        if (!(sender instanceof Player)) {
            throw new IllegalSenderException(sender);
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            throw new IncorrectUsageException(this);
        }

        if (args[0].equalsIgnoreCase("join")) {

            if (args.length < 2) {
                throw new IncorrectUsageException(this);
            }

            if (plugin.getGameManager().getGameByPlayer(player) != null) {
                throw new PluginCommandException(i18n.getString("alreadyInGame"));
            }

            Map map = Map.getMapByName(args[1]);
            if (map == null) {
                throw new PluginCommandException(i18n.getString("mapNotFound", args[1]));
            }

            Game game = plugin.getGameManager().getGameByMap(map);
            if (game == null) {
                game = plugin.getGameManager().createGame(map);
            }

            GamePlayer gamePlayer = game.addPlayer(player);
            player.sendMessage(i18n.getString("joinedTeam", gamePlayer.getTeam().getName()));

            return true;

        }

        if (args[0].equalsIgnoreCase("leave")) {

            Game game = plugin.getGameManager().getGameByPlayer(player);
            if (game == null || game.hasPlayer(player)) {
                throw new PluginCommandException(i18n.getString("notInGame"));
            }
            game.removePlayer(player);
            player.sendMessage(i18n.getString("leftGame"));
            
            // TODO: teleport the player out of the map or something

            return true;

        }

        if (args[0].equalsIgnoreCase("start")) {

            Game game = plugin.getGameManager().getGameByPlayer(player);
            if (game == null) {
                throw new PluginCommandException(i18n.getString("notInGame"));
            }

            if (game.isActive()) {
                throw new PluginCommandException(i18n.getString("gameAlreadyStarted"));
            }

            game.start();
            // TODO: maybe don't send a message and just start the game
            player.sendMessage(i18n.getString("startedGame"));

            return true;

        }

        throw new IncorrectUsageException(this);

    }

}
