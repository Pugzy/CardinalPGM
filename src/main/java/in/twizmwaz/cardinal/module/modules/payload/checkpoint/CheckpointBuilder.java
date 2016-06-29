package in.twizmwaz.cardinal.module.modules.payload.checkpoint;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.Module;
import in.twizmwaz.cardinal.module.ModuleBuilder;
import in.twizmwaz.cardinal.module.ModuleCollection;
import in.twizmwaz.cardinal.module.modules.blockdrops.Blockdrops;
import in.twizmwaz.cardinal.module.modules.filter.FilterModule;
import in.twizmwaz.cardinal.module.modules.filter.FilterModuleBuilder;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.module.modules.regions.RegionModuleBuilder;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.Parser;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckpointBuilder implements ModuleBuilder {

    @Override
    public ModuleCollection<? extends Module> load(Match match) {
        ModuleCollection<Checkpoint> results = new ModuleCollection<>();
        for (Element payload : match.getDocument().getRootElement().getChildren("payload")) {
            for (Element cart : payload.getChildren("cart")) {
                results.add(parseCheckpoint(cart, payload));
            }
            for (Element subCarts : payload.getChildren("cart")) {
                for (Element net : subCarts.getChildren("net")) {
                    results.add(parseCheckpoint(net, subCarts, payload));
                }
            }
        }
        return results;
    }

    public static Checkpoint parseCheckpoint(Element... elements) {
        if (elements[0].getName().equals("checkpoint")) {
            String id = elements[0].getAttributeValue("id") == null ? null : elements[0].getAttributeValue("id");
            RegionModule region = RegionModuleBuilder.getAttributeOrChild("region", elements);
            boolean show = Numbers.parseBoolean(Parser.getOrderedAttribute("show", elements), true);
            int position = Numbers.parseInt(Parser.getOrderedAttribute("position", elements), 0);

            return new Checkpoint(id, region, show, position);
        }
        return null;
    }

}