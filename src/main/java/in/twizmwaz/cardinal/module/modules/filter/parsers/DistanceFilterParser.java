package in.twizmwaz.cardinal.module.modules.filter.parsers;

import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.module.GameObjective;
import in.twizmwaz.cardinal.module.modules.filter.FilterParser;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.module.modules.regions.RegionModuleBuilder;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.Parser;
import in.twizmwaz.cardinal.util.Strings;
import in.twizmwaz.cardinal.util.Teams;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.jdom2.Element;

public class DistanceFilterParser extends FilterParser {

    private Element element;
    private GameObjective objective;
    private RegionModule region;
    private double radius;


    public DistanceFilterParser(final Element element) {
        super(element);
        this.element = element;
        Bukkit.broadcastMessage(element.getAttributeValue("objective") + element.getAttributeValue("region") + element.getAttributeValue("radius"));
        this.radius = element.getAttributeValue("radius") != null ?
                Numbers.parseDouble(element.getAttributeValue("radius")) : -1;
    }

    public void load() {
        String name = element.getText();
        for (GameObjective objective : GameHandler.getGameHandler().getMatch().getModules().getModules(GameObjective.class)) {
            if (objective.getId().replaceAll(" ", "").equalsIgnoreCase(name.replaceAll(" ", ""))) {
                this.objective = objective;
            }
        }
        for (GameObjective objective : GameHandler.getGameHandler().getMatch().getModules().getModules(GameObjective.class)) {
            if (objective.getId().replaceAll(" ", "").toLowerCase().startsWith(name.replaceAll(" ", "").toLowerCase())) {
                this.objective = objective;
            }
        }
        for (GameObjective objective : GameHandler.getGameHandler().getMatch().getModules().getModules(GameObjective.class)) {
            if (objective.getName().replaceAll(" ", "").equalsIgnoreCase(name.replaceAll(" ", ""))) {
                this.objective = objective;
            }
        }
        for (GameObjective objective : GameHandler.getGameHandler().getMatch().getModules().getModules(GameObjective.class)) {
            if (objective.getName().replaceAll(" ", "").toLowerCase().startsWith(name.replaceAll(" ", "").toLowerCase())) {
                this.objective = objective;
            }
        }
    }

    public GameObjective getObjective() {
        return this.objective;
    }

    public RegionModule getRegion() { return this.region; }

    public double getRadius() { return this.radius; }

}