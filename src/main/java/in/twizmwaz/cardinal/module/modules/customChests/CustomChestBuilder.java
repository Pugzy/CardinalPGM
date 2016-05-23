package in.twizmwaz.cardinal.module.modules.customChests;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.ModuleBuilder;
import in.twizmwaz.cardinal.module.ModuleCollection;
import in.twizmwaz.cardinal.module.modules.filter.FilterModule;
import in.twizmwaz.cardinal.module.modules.filter.FilterModuleBuilder;
import in.twizmwaz.cardinal.module.modules.killReward.KillReward;
import in.twizmwaz.cardinal.module.modules.kit.KitBuilder;
import in.twizmwaz.cardinal.module.modules.kit.KitNode;
import in.twizmwaz.cardinal.module.modules.kit.kitTypes.KitItem;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.module.modules.regions.parsers.PointParser;
import in.twizmwaz.cardinal.module.modules.regions.type.PointRegion;
import in.twizmwaz.cardinal.util.Parser;
import org.bukkit.Location;
import org.jdom2.Element;

public class CustomChestBuilder implements ModuleBuilder {

    @Override
    public ModuleCollection<CustomChest> load(Match match) {
        ModuleCollection<CustomChest> results = new ModuleCollection<>();
        for (Element element : match.getDocument().getRootElement().getChildren("custom-chests")) {
            KitNode kit = null;
            if (element.getAttributeValue("chest") != null) kit = KitNode.getKitByName(element.getAttributeValue("chest"));
            if (element.getChild("chest") != null) kit = KitBuilder.getKit(element.getChild("kit"));

            KitItem item = null;
            if (element.getChild("item") != null) {
                item = Parser.getKitItem(element.getChild("item"));
            }

            FilterModule filter = FilterModuleBuilder.getFilter("always");
            if (element.getAttributeValue("filter") != null) filter = FilterModuleBuilder.getFilter(element.getAttributeValue("filter"));
            if (element.getChild("filter") != null) filter = FilterModuleBuilder.getFilter(element.getChild("filter"));

            RegionModule region = null;
            if (element.getChild("region") != null) {
                region = new PointRegion(new PointParser(element.getChild("region")));
            }

            results.add(new CustomChest(item, kit, filter, region));
        }
        return results;
    }

}
