package in.twizmwaz.cardinal.module.modules.customChests;

import in.twizmwaz.cardinal.event.CardinalDeathEvent;
import in.twizmwaz.cardinal.module.Module;
import in.twizmwaz.cardinal.module.modules.filter.FilterModule;
import in.twizmwaz.cardinal.module.modules.filter.FilterState;
import in.twizmwaz.cardinal.module.modules.kit.KitNode;
import in.twizmwaz.cardinal.module.modules.kit.kitTypes.KitItem;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

public class CustomChest implements Module {

    private final KitItem item;
    private final KitNode kit;
    private final FilterModule filter;
    private final RegionModule region;

    protected CustomChest(KitItem item, KitNode kit, final FilterModule filter, RegionModule region) {
        this.item = item;
        this.kit = kit;
        this.filter = filter;
        this.region = region;
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        for (Block block : region.getBlocks()){
            if (block.getType() == Material.CHEST) {
                Chest chest = (Chest) block;
                for (int i = chest.getInventory().getSize(); i > 0; i--){
                    ItemStack item = new ItemStack(Material.CARROT_ITEM, 1);
                    chest.getInventory().setItem(i, item);
                }
            }
        }
    }

}
