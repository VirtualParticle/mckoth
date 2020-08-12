package game;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class GamePlayer {

    private Team team;
    private final Player player;

    public GamePlayer(Player player) {
        this(player, null);
    }

    public GamePlayer(Player player, Team team) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public Player getPlayer() {
        return player;
    }

    public void regenHealth() {
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        int maxHealth = 20;
        if (maxHealthAttribute != null) {
            maxHealth = (int) maxHealthAttribute.getValue();
        }
        player.setHealth(maxHealth);
    }


}
