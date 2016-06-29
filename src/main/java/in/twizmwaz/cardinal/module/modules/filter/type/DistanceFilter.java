package in.twizmwaz.cardinal.module.modules.filter.type;

import in.twizmwaz.cardinal.module.GameObjective;
import in.twizmwaz.cardinal.module.modules.cores.CoreObjective;
import in.twizmwaz.cardinal.module.modules.ctf.FlagObjective;
import in.twizmwaz.cardinal.module.modules.filter.FilterModule;
import in.twizmwaz.cardinal.module.modules.filter.FilterState;
import in.twizmwaz.cardinal.module.modules.filter.parsers.DistanceFilterParser;
import in.twizmwaz.cardinal.module.modules.filter.parsers.ObjectiveFilterParser;
import in.twizmwaz.cardinal.module.modules.hill.HillObjective;
import in.twizmwaz.cardinal.module.modules.payload.PayloadObjective;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.module.modules.wools.WoolObjective;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static in.twizmwaz.cardinal.module.modules.filter.FilterState.ALLOW;
import static in.twizmwaz.cardinal.module.modules.filter.FilterState.DENY;

public class DistanceFilter extends FilterModule {

    private DistanceFilterParser parser;
    private GameObjective objective = null;
    private RegionModule region;
    private double radius;

    public DistanceFilter(DistanceFilterParser parser) {
        super(parser.getName(), parser.getParent());
        this.parser = parser;
        this.radius = parser.getRadius();
        Bukkit.broadcastMessage(this.toString());
    }

    public void load() {
        parser.load();
        this.objective = parser.getObjective();
        this.region = parser.getRegion();
    }

    @Override
    public FilterState evaluate(final Object... objects) {
        if (objective == null) load();
        for (Object object : objects) {
            if (object instanceof Player) {
                Player player = (Player) object;
                if (objective instanceof PayloadObjective) {
                    PayloadObjective payloadObjective = (PayloadObjective) this.objective;
                    if (player.getLocation().distance(payloadObjective.getCartLocation()) <= radius) {
                        return ALLOW;
                    }
                }
                if (objective instanceof CoreObjective) {
                    CoreObjective coreObjective = (CoreObjective) this.objective;
                    if (player.getLocation().distance(coreObjective.getRegion().getCenterBlock().getLocation()) <= radius) {
                        return ALLOW;
                    }
                }
                if (objective instanceof WoolObjective) {
                    WoolObjective woolObjective = (WoolObjective) this.objective;
//                    if (player.getLocation().distance(woolObjective.getLocation()) <= radius) {
//                        return ALLOW;
//                    }
                }
                if (objective instanceof HillObjective) {
                    HillObjective hillObjective = (HillObjective) this.objective;
//                    if (player.getLocation().distance(hillObjective.getLocation()) <= radius) {
//                        return ALLOW;
//                    }
                }
                if (objective instanceof FlagObjective) {
                    FlagObjective flagObjective = (FlagObjective) this.objective;
                    if (player.getLocation().distance(flagObjective.getCurrentFlagLocation()) <= radius) {
                        return ALLOW;
                    }
                }
            }
        }
        return DENY;
    }

    @Override
    public String toString() {
        return "DistanceFilter{" +
                "objective=" + objective +
                ", radius=" + radius +
                '}';
    }
}