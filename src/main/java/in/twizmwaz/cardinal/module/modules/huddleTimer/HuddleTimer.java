package in.twizmwaz.cardinal.module.modules.huddleTimer;

import in.twizmwaz.cardinal.Cardinal;
import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.chat.ChatConstant;
import in.twizmwaz.cardinal.chat.ChatMessage;
import in.twizmwaz.cardinal.chat.LocalizedChatMessage;
import in.twizmwaz.cardinal.chat.UnlocalizedChatMessage;
import in.twizmwaz.cardinal.event.MatchStartEvent;
import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.match.MatchState;
import in.twizmwaz.cardinal.module.TaskedModule;
import in.twizmwaz.cardinal.util.ChatUtil;
import in.twizmwaz.cardinal.util.Players;
import in.twizmwaz.cardinal.util.Teams;
import in.twizmwaz.cardinal.util.bossBar.BossBars;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class HuddleTimer implements TaskedModule, Cancellable {

    private boolean cancelled = true;
    private int time, originalTime;
    private String bossBar;

    private Match match;

    public HuddleTimer(Match match) {
        this.match = match;
        this.bossBar = BossBars.addBroadcastedBossBar(new UnlocalizedChatMessage(""), BarColor.YELLOW, BarStyle.SOLID, false);
    }

    @Override
    public void unload() {
        BossBars.removeBroadcastedBossBar(bossBar);
        HandlerList.unregisterAll(this);
    }

    @Override
    public void run() {
        if (!isCancelled()) {
            BossBars.setProgress(bossBar, ((double)time / originalTime));
            if (time % 20 == 0) {
                int intTime = (time / 20);
                if (time != 0) {
                    BossBars.setTitle(bossBar, getHuddleTimerMessage(intTime));
                    if (intTime <= 3) {
                        Players.broadcastSoundEffect(Sound.BLOCK_NOTE_PLING, 1, 1);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!Teams.getTeamByPlayer(player).get().isObserver()) {
                                player.showTitle(new TextComponent(ChatColor.YELLOW + "" + intTime), new TextComponent(""), 0, 5, 15);
                            }
                        }
                    }
                } else {
                    if (match.getState() == MatchState.HUDDLE) {
                        cancelled = true;
                        BossBars.removeBroadcastedBossBar(bossBar);
                        match.setState(MatchState.PLAYING);
                        ChatUtil.getGlobalChannel().sendLocalizedMessage(new UnlocalizedChatMessage(ChatColor.GREEN + "{0}", new LocalizedChatMessage(ChatConstant.UI_MATCH_STARTED)));
                        Bukkit.getServer().getPluginManager().callEvent(new MatchStartEvent());
                    }
                    Players.broadcastSoundEffect(Sound.BLOCK_NOTE_PLING, 1, 2);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!Teams.getTeamByPlayer(player).get().isObserver()) {
                            String title = new LocalizedChatMessage(ChatConstant.UI_MATCH_START_TITLE).getMessage(player.getLocale());
                            player.showTitle(new TextComponent(ChatColor.GREEN + title), new TextComponent(""), 0, 5, 15);
                        }
                    }
                }
            }
            if (time < 0) {
                setCancelled(true);
            }
            time--;
        }
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        setOriginalTime(time);
    }

    public void setOriginalTime(int time) {
        this.originalTime = time;
    }

    private ChatMessage getHuddleTimerMessage(int time) {
        return new UnlocalizedChatMessage(ChatColor.YELLOW + "{0}", new LocalizedChatMessage(ChatConstant.UI_MATCH_HUDDLE_END_IN, new LocalizedChatMessage(time == 1 ? ChatConstant.UI_SECOND : ChatConstant.UI_SECONDS, ChatColor.DARK_RED + "" + time + ChatColor.YELLOW)));
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.cancelled = isCancelled;
        if (this.cancelled && GameHandler.getGameHandler().getMatch().getState().equals(MatchState.HUDDLE)) {
            GameHandler.getGameHandler().getMatch().setState(MatchState.WAITING);
            BossBars.setVisible(bossBar, false);
        } else {
            BossBars.setVisible(bossBar, true);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!Teams.getTeamByPlayer(player).get().isObserver()) {
                    if (time >= 20) ChatUtil.getGlobalChannel().sendLocalizedMessage(new UnlocalizedChatMessage(ChatColor.YELLOW + "{0}", new LocalizedChatMessage(ChatConstant.UI_MATCH_HUDDLE_STRATIGIZE, new LocalizedChatMessage(time == 1 ? ChatConstant.UI_SECOND : ChatConstant.UI_SECONDS, ChatColor.YELLOW + "" + (time / 20) + ChatColor.YELLOW))));
                }
            }
        }
    }

}
