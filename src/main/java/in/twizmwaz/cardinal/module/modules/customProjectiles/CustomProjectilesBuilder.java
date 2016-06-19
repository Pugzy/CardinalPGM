package in.twizmwaz.cardinal.module.modules.customProjectiles;

import in.twizmwaz.cardinal.match.Match;
import in.twizmwaz.cardinal.module.ModuleBuilder;
import in.twizmwaz.cardinal.module.ModuleCollection;
import in.twizmwaz.cardinal.module.modules.filter.FilterModule;
import in.twizmwaz.cardinal.module.modules.filter.FilterModuleBuilder;
import in.twizmwaz.cardinal.util.Numbers;
import in.twizmwaz.cardinal.util.Parser;
import in.twizmwaz.cardinal.util.Strings;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

public class CustomProjectilesBuilder implements ModuleBuilder {

    @Override
    public ModuleCollection<CustomProjectiles> load(Match match) {
        ModuleCollection<CustomProjectiles> results = new ModuleCollection<>();
        for (Element projectiles : match.getDocument().getRootElement().getChildren("projectiles")) {
            for (Element projectile : projectiles.getChildren("projectile")) {
                results.add(parseCustomProjectiles(projectile, projectiles));
            }
            for (Element projectile2 : projectiles.getChildren()) {
                for (Element projectile : projectile2.getChildren("projectile")) {
                    results.add(parseCustomProjectiles(projectile, projectile2, projectiles));
                }
            }
        }
        return results;
    }

    private CustomProjectiles parseCustomProjectiles(Element... elements) {
        List<PotionEffect> effects = new ArrayList<>();
        String id = Parser.getOrderedAttribute("id", elements);
        String name = Parser.getOrderedAttribute("name", elements);
        boolean throwable = Numbers.parseBoolean(Parser.getOrderedAttribute("throwable", elements), true);
        EntityType projectile = EntityType.valueOf(Strings.getTechnicalName(Parser.getOrderedAttribute("projectile", elements)));
        double damage = Numbers.parseDouble(Parser.getOrderedAttribute("damage", elements), 0);
        double velocity = Numbers.parseDouble(Parser.getOrderedAttribute("velocity", elements), 1.0);
        int cooldown = Strings.timeStringToSeconds((Parser.getOrderedAttribute("cooldown", elements)));
        String click = Parser.getOrderedAttribute("click", elements) == null ? "both" : Parser.getOrderedAttribute("click", elements);
        for (Element potion : elements[0].getChildren("potion")) {
            effects.add(Parser.getPotion(potion));
        }
        FilterModule destroyFilter = FilterModuleBuilder.getAttributeOrChild("destroy-filter", "always", elements);

        return new CustomProjectiles(id, name, throwable, projectile, damage, velocity, cooldown, click, effects, destroyFilter);
    }
}