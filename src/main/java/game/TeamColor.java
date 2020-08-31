package game;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public enum TeamColor {

    BLUE(ChatColor.BLUE, ChatColor.DARK_BLUE, Material.BLUE_WOOL, Color.BLUE),
    GREEN(ChatColor.GREEN, ChatColor.DARK_GREEN, Material.GREEN_WOOL, Color.GREEN),
    AQUA(ChatColor.AQUA, ChatColor.DARK_AQUA, Material.CYAN_WOOL, Color.AQUA),
    RED(ChatColor.RED, ChatColor.DARK_RED, Material.RED_WOOL, Color.RED),
    PURPLE(ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE, Material.PURPLE_WOOL, Color.PURPLE),
    GOLD(ChatColor.YELLOW, ChatColor.GOLD, Material.YELLOW_WOOL, Color.YELLOW);


    private final ChatColor color1;
    private final ChatColor color2;
    private final Material material;
    private final Color armorColor;

    TeamColor(ChatColor color1, ChatColor color2, Material material, Color armorColor) {
        this.color1 = color1;
        this.color2 = color2;
        this.material = material;
        this.armorColor = armorColor;
    }

    public ChatColor getColor1() {
        return color1;
    }

    public ChatColor getColor2() {
        return color2;
    }

    public Material getMaterial() {
        return material;
    }

    public Color getArmorColor() {
        return armorColor;
    }

    @Override
    public String toString() {
        return color1.toString();
    }
}
