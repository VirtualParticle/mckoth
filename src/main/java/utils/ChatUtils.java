package utils;

import com.virtualparticle.mc.mckoth.McKoth;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ChatUtils {

    public static void sendActionBar(Player player, String message, long duration) {

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        if (duration >= 0) {
            Plugin plugin = McKoth.getPlugin();
            if (plugin != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    sendActionBar(player, "");
                }, duration);
            }
        }

    }

    public static void sendActionBar(Player player, String message) {
        sendActionBar(player, message, -1);
    }

    public static String generateProgressBar(ChatColor leftColor, ChatColor rightColor, int length, int value, int total) {
        return generateProgressBar(leftColor, rightColor, length, (float) value / total);
    }

    public static String generateProgressBar(ChatColor leftColor, ChatColor rightColor, int length, float progress) {

        StringBuilder sb = new StringBuilder();
        for (int i = length; i >= 0; i--) {
            float point = (float) i / length;
            sb.append(point > progress ? leftColor : rightColor);
            sb.append("█");
        }

        return sb.toString();

    }

}
