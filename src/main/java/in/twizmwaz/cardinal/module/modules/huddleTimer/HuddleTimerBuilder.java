package in.twizmwaz.cardinal.module.modules.huddleTimer;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.ModuleBuilder;
import in.twizmwaz.cardinal.module.ModuleCollection;

public class HuddleTimerBuilder implements ModuleBuilder {

    @Override
    public ModuleCollection<HuddleTimer> load(Match match) {
        return new ModuleCollection<>(new HuddleTimer(match));
    }

}
