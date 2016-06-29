package in.twizmwaz.cardinal.module.modules.payload.checkpoint;

import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.module.Module;
import in.twizmwaz.cardinal.module.modules.filter.FilterModule;
import in.twizmwaz.cardinal.module.modules.filter.FilterState;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.module.modules.tntTracker.TntTracker;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.swing.plaf.synth.Region;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Checkpoint implements Module {

    private String id;
    private RegionModule region;
    private boolean show;
    private int position;

    protected Checkpoint(String id, RegionModule region, boolean show, int position) {
        this.id = id;
        this.region = region;
        this.show = show;
        this.position = position;

        Bukkit.broadcastMessage(this.toString());
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String toString() {
        return "Checkpoint{" +
                "id='" + id + '\'' +
                ", region=" + region +
                ", show=" + show +
                ", position=" + position +
                '}';
    }
}
