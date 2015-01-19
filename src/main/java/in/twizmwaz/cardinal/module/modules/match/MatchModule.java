package in.twizmwaz.cardinal.module.modules.match;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.event.CycleCompleteEvent;
import in.twizmwaz.cardinal.event.MatchEndEvent;
import in.twizmwaz.cardinal.event.MatchStartEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchState;
import in.twizmwaz.cardinal.module.Module;
import in.twizmwaz.cardinal.util.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerListPingEvent;

public class MatchModule implements Module {

    private final Match match;

    protected MatchModule(Match match) {
        this.match = match;
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "# # # # # # # # # # # # # # # #");
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "# # " + ChatColor.GOLD + "The match has started!" + ChatColor.DARK_PURPLE + " # #");
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "# # # # # # # # # # # # # # # #");
    }

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "# # # # # # # # # # # #");
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "# #    " + ChatColor.GOLD + "Game over!" + ChatColor.DARK_PURPLE + "    # #");
        try {
            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "# # " + event.getTeam().getColor() + event.getTeam().getName() + " wins!" + ChatColor.DARK_PURPLE + " # #");
        } catch (NullPointerException ex) {

        }
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "# # # # # # # # # # # #");
    }

    @EventHandler
    public void onCycleComplete(CycleCompleteEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            TeamUtils.getTeamById("observers").add(player, true);
        }
    }
}