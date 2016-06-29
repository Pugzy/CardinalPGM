package in.twizmwaz.cardinal.module.modules.payload;

import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.module.GameObjective;
import in.twizmwaz.cardinal.module.Module;
import in.twizmwaz.cardinal.module.TaskedModule;
import in.twizmwaz.cardinal.module.modules.appliedRegion.type.VelocityRegion;
import in.twizmwaz.cardinal.module.modules.filter.FilterModule;
import in.twizmwaz.cardinal.module.modules.filter.FilterState;
import in.twizmwaz.cardinal.module.modules.payload.checkpoint.Checkpoint;
import in.twizmwaz.cardinal.module.modules.proximity.GameObjectiveProximityHandler;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.module.modules.regions.type.BlockRegion;
import in.twizmwaz.cardinal.module.modules.scoreboard.GameObjectiveScoreboardHandler;
import in.twizmwaz.cardinal.module.modules.team.TeamModule;
import in.twizmwaz.cardinal.module.modules.tntTracker.TntTracker;
import in.twizmwaz.cardinal.util.PacketUtils;
import in.twizmwaz.cardinal.util.Parser;
import net.minecraft.server.EnumParticle;
import net.minecraft.server.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PayloadObjective implements TaskedModule, GameObjective {

    private String id;
    private boolean show;
    private String name;
    private DyeColor color;
    private ChatColor chatColor;
    private TeamModule owner;
    private RegionModule spawn;
    private boolean beam;
    private Set<Checkpoint> checkpoints;

    private Long lastUpdate = 0L;
    private Minecart minecart;
    private ArmorStand armorStand;

    private GameObjectiveScoreboardHandler scoreboardHandler;
    private Map<String, GameObjectiveProximityHandler> payloadProximityHandlers;
    private boolean touched;
    private boolean complete;

    protected PayloadObjective(String id,
                               boolean show,
                               String name,
                               DyeColor color,
                               ChatColor chatColor,
                               TeamModule owner,
                               RegionModule spawn,
                               boolean beam,
                               Set<Checkpoint> checkpoints) {

        this.id = id;
        this.show = show;
        this.name = name;
        this.color = color;
        this.chatColor = chatColor;
        this.owner = owner;
        this.spawn = spawn;
        this.beam = beam;
        this.checkpoints = checkpoints;

        this.scoreboardHandler = new GameObjectiveScoreboardHandler(this);

        spawnCart();
        //updateArmorStand();
        Bukkit.broadcastMessage(this.toString());
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String toString() {
        return "PayloadObjective{" +
                "id='" + id + '\'' +
                ", show=" + show +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", chatColor=" + chatColor +
                ", owner=" + owner +
                ", beam=" + beam +
                '}';
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            this.minecart = (Minecart) event.getVehicle();
            MaterialData displayBlock = new MaterialData(Material.WOOL);
            displayBlock.setData((byte)((int)color.getWoolData()));
            minecart.setDisplayBlock(displayBlock);
            minecart.setInvulnerable(true);
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            Player p = (Player)event.getActor();
            Vector facing = p.getLocation().getDirection();
            event.getVehicle().setVelocity(facing);
        }
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (event.getVehicle().equals(minecart)) { // check correct cart..
            if (event.getFrom().getBlock() != event.getTo()) {
                if (isOnRail(event.getFrom().getBlock().getRelative(BlockFace.SELF).getType())) {
                    Block block = event.getFrom().getBlock().getRelative(BlockFace.DOWN);
                    block.setType(Material.WOOL);
                    block.setData((byte)((int)color.getWoolData()));
                }
            }
        }
    }

    private void sendParticles() {
        if (minecart.getLocation() != null) {
            Location loc = minecart.getLocation();
            if (beam) {
                loc.add(0, 56.0, 0);
                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.ITEM_CRACK, true, (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 0.15f, 24.0f, 0.15f, 0.0f, 40, 35, (int)color.getWoolData());
                PacketUtils.broadcastPacket(packet);
            }
        }
    }

    @Override
    public void run() {
        sendParticles();
        if (GameHandler.getGameHandler().getMatch().isRunning()) {
            if (System.currentTimeMillis() - lastUpdate > 100) {
                tickAndUpdate();
            }
        }
    }

    private void spawnCart() {
        Entity cart  = GameHandler.getGameHandler().getMatchWorld().spawn(this.spawn.getCenterBlock().getLocation(), Minecart.class);
        Bukkit.broadcastMessage("Locaton: " + this.spawn.getCenterBlock().getLocation());
        this.minecart = (Minecart) cart;
        MaterialData displayBlock = new MaterialData(Material.WOOL);
        displayBlock.setData((byte)((int)color.getWoolData()));
        minecart.setDisplayBlock(displayBlock);
        minecart.setInvulnerable(true);
    }

    private void setLastUpdate() {
        lastUpdate = System.currentTimeMillis();
    }

    private void tickAndUpdate() {
        updateCart();
        //updateArmorStand();
        //updateArmorStand();
        updateScoreboard();
    }

    private void updateCart() {
        //minecart.setVelocity((new Vector(minecart.getLocation().getDirection().getX(), 0, minecart.getLocation().getDirection().getZ()).multiply(1)));
        //minecart.setMaxSpeed(0.5);
        setLastUpdate();
    }

    private void updateArmorStand() {
        if (!isMoving() && armorStand == null || armorStand.isDead()) armorStand = createArmorStand();
        //String suffix = isDropped() && GameHandler.getGameHandler().getMatch().isRunning() ? ChatColor.AQUA + " " + getRecoverTime() : "";
        armorStand.setCustomName(getDisplayName()); // + suffix
        if (this.isMoving()) {
            armorStand.remove();
        } else {
            armorStand.teleport(this.minecart.getLocation());
        }
    }

    private void updateScoreboard() {
        //updateArmorStand();
    }

    public String getDisplayName() {
        return getName(); // add chat color
    }

    private ArmorStand createArmorStand() {
        Location loc = this.minecart.getLocation();
        ArmorStand armorStand = GameHandler.getGameHandler().getMatchWorld().spawn(loc, ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setBasePlate(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(getDisplayName());
        return armorStand;
    }

    private boolean isOnRail(Material material) {
        if (material.equals(Material.RAILS)) {
            return true;
        }
        if (material.equals(Material.ACTIVATOR_RAIL)) {
            return true;
        }
        if (material.equals(Material.DETECTOR_RAIL)) {
            return true;
        }
        if (material.equals(Material.POWERED_RAIL)) {
            return true;
        }
        return false;
    }

    public boolean isMoving() {
        if (minecart.getVelocity().getX() != 0 || minecart.getVelocity().getY() != 0 || minecart.getVelocity().getZ() != 0) {
            return true;
        }
        return false;
    }

    public Location getCartLocation() {
        return minecart.getLocation();
    }

    @Override
    public TeamModule getTeam() {
        return owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isTouched() {
        return touched;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public boolean showOnScoreboard() {
        return show;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public GameObjectiveScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }

    @Override
    public GameObjectiveProximityHandler getProximityHandler(TeamModule team) {
        return payloadProximityHandlers.get(team.getId());
    }



}
