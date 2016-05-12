package in.twizmwaz.cardinal.module.modules.kit.kitTypes;

import in.twizmwaz.cardinal.module.modules.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Skin;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class HeadKit implements Kit {

    String name;
    UUID uuid;
    Skin skin;

    public HeadKit(String name, UUID uuid, String skin) {
        this.name = name;
        this.uuid = uuid;
        //this.skin = new Skin(skin);
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void apply(Player player, Boolean force) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(name, uuid, skin);
        skullMeta.setDisplayName(name);
        skull.setItemMeta(skullMeta);
        player.getInventory().setHelmet(skull);
        Bukkit.broadcastMessage("Skull Created");
    }

}
