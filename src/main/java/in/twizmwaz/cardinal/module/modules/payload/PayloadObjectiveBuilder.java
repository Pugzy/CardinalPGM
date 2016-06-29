package in.twizmwaz.cardinal.module.modules.payload;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.Module;
import in.twizmwaz.cardinal.module.ModuleBuilder;
import in.twizmwaz.cardinal.module.ModuleCollection;
import in.twizmwaz.cardinal.module.modules.payload.checkpoint.Checkpoint;
import in.twizmwaz.cardinal.module.modules.payload.checkpoint.CheckpointBuilder;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.module.modules.regions.RegionModuleBuilder;
import in.twizmwaz.cardinal.module.modules.team.TeamModule;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.Parser;
import in.twizmwaz.cardinal.util.Teams;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.jdom2.Element;

import java.util.HashSet;
import java.util.Set;

public class PayloadObjectiveBuilder implements ModuleBuilder {

    @Override
    public ModuleCollection<? extends Module> load(Match match) {
        ModuleCollection<PayloadObjective> results = new ModuleCollection<>();
        for (Element payload : match.getDocument().getRootElement().getChildren("payload")) {
            for (Element cart : payload.getChildren("cart")) {
                results.add(parseCart(cart, payload));
            }
            for (Element carts : payload.getChildren("carts")) {
                for (Element cart : carts.getChildren("cart")) {
                    results.add(parseCart(cart, carts, payload));
                }
            }
        }
        return results;
    }

    private PayloadObjective parseCart(Element... elements) {
        ModuleCollection<Module> result =  new ModuleCollection<>();
        String id = elements[0].getAttributeValue("id");
        boolean show = Numbers.parseBoolean(Parser.getOrderedAttribute("show", elements), true);
        String name = elements[0].getAttributeValue("name");
        DyeColor color = Parser.parseDyeColor(elements[0].getAttributeValue("color"));
        ChatColor chatColor = Parser.parseChatColor((elements[0].getAttributeValue("chatColor")));
        TeamModule owner = Parser.getOrderedAttribute("owner", elements) == null ? null : Teams.getTeamById(Parser.getOrderedAttribute("owner", elements)).orNull();
        RegionModule region = RegionModuleBuilder.getAttributeOrChild("region", "always", elements);
        boolean beam = Numbers.parseBoolean(Parser.getOrderedAttribute("beam", elements), true);

        Set<Checkpoint> checkpoints = new HashSet<>();
        if (elements[0].getChildren("checkpoints").size() > 0) {
            for (Element checkpoint : elements[0].getChildren("checkpoints")) {
                Checkpoint point = CheckpointBuilder.parseCheckpoint(Parser.addElement(checkpoint, elements));
                checkpoints.add(point);
                result.add(point); // this seems wrong, should be checkpoints not point.
            }
        }

        return new PayloadObjective(id, show, name, color, chatColor, owner, region, beam, checkpoints);
    }

}