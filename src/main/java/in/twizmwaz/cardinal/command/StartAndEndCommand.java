package in.twizmwaz.cardinal.command;

import com.google.common.base.Optional;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.chat.ChatConstant;
import in.twizmwaz.cardinal.chat.LocalizedChatMessage;
import in.twizmwaz.cardinal.match.MatchState;
import in.twizmwaz.cardinal.module.modules.huddleTimer.HuddleTimer;
import in.twizmwaz.cardinal.module.modules.startTimer.StartTimer;
import in.twizmwaz.cardinal.module.modules.team.TeamModule;
import in.twizmwaz.cardinal.module.modules.timeLimit.TimeLimit;
import in.twizmwaz.cardinal.util.ChatUtil;
import in.twizmwaz.cardinal.util.Teams;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class StartAndEndCommand {
    private static int timer;
    private static int huddle;
    private static boolean waiting = false;

    @Command(aliases = {"start", "begin"}, desc = "Starts the match.", usage = "[countdown time] [huddle time]", flags = "f")
    @CommandPermissions("cardinal.match.start")
    public static void start(CommandContext cmd, CommandSender sender) throws CommandException {

        StartTimer startTimer = GameHandler.getGameHandler().getMatch().getModules().getModule(StartTimer.class);
        HuddleTimer huddleTimer = GameHandler.getGameHandler().getMatch().getModules().getModule(HuddleTimer.class);

        if (GameHandler.getGameHandler().getMatch().getState().equals(MatchState.WAITING)) {
            int time = 600;
            int huddle = 0;
            if (cmd.argsLength() > 0) time = cmd.getInteger(0) * 20;
            if (cmd.argsLength() > 1) huddle = cmd.getInteger(1) * 20;

            huddleTimer.setTime(huddle);
            startTimer.setHuddle(huddle != 0);
            GameHandler.getGameHandler().getMatch().start(time, cmd.hasFlag('f'));

            Bukkit.broadcastMessage("Command> Huddle Time: " + huddleTimer.getTime());

        } else if (GameHandler.getGameHandler().getMatch().getState().equals(MatchState.STARTING)) {
            huddleTimer.setTime(cmd.argsLength() > 1 ? cmd.getInteger(1) * 20 : 0);
            Bukkit.broadcastMessage(huddleTimer.getTime() + " huddle time.. this is " + (huddleTimer.getTime() != 0));
            startTimer.setHuddle(huddleTimer.getTime() != 0);
            startTimer.setTime(cmd.argsLength() > 0 ? cmd.getInteger(0) * 20 : 30 * 20);
            startTimer.setForced(cmd.hasFlag('f'));
        } else if (GameHandler.getGameHandler().getMatch().getState().equals(MatchState.ENDED)) {
            throw new CommandException(new LocalizedChatMessage(ChatConstant.ERROR_NO_RESUME).getMessage(ChatUtil.getLocale(sender)));
        } else {
            throw new CommandException(new LocalizedChatMessage(ChatConstant.ERROR_NO_START).getMessage(ChatUtil.getLocale(sender)));
        }

    }

    @Command(aliases = {"end", "finish"}, desc = "Ends the match.", usage = "[team]", flags = "n")
    @CommandPermissions("cardinal.match.end")
    public static void end(CommandContext cmd, CommandSender sender) throws CommandException {
        if (!GameHandler.getGameHandler().getMatch().isRunning()) {
            throw new CommandException(ChatConstant.ERROR_NO_END.getMessage(ChatUtil.getLocale(sender)));
        }
        if (cmd.argsLength() > 0) {
            Optional<TeamModule> team = Teams.getTeamByName(cmd.getString(0));
            GameHandler.getGameHandler().getMatch().end(team.orNull());
        } else {
            if (cmd.hasFlag('n')) {
                GameHandler.getGameHandler().getMatch().end();
            } else {
                GameHandler.getGameHandler().getMatch().end(TimeLimit.getMatchWinner());
            }
        }
    }

}
