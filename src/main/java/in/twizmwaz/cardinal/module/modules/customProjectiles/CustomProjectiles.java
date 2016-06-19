package in.twizmwaz.cardinal.module.modules.customProjectiles;

import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.module.Module;
import in.twizmwaz.cardinal.module.modules.filter.FilterModule;
import in.twizmwaz.cardinal.module.modules.filter.FilterState;
import in.twizmwaz.cardinal.module.modules.observers.ObserverModule;
import in.twizmwaz.cardinal.util.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CustomProjectiles implements Module {

    private String id, name;
    private boolean throwable;
    private EntityType projectile;
    private double damage, velocity;
    private int cooldown;
    private String click;
    private List<PotionEffect> effects;
    private FilterModule destroyFilter;

    protected CustomProjectiles(final String id, final String name, final boolean throwable,
                                final EntityType projectile, final double damage, final double velocity,
                                final int cooldown, final String click, final List<PotionEffect> effects,
                                final FilterModule destroyFilter) {
        this.id = id;
        this.name = name;
        this.throwable = throwable;
        this.projectile = projectile;
        this.damage = damage;
        this.velocity = velocity;
        this.cooldown = cooldown;
        this.click = click;
        this.effects = effects;
        this.destroyFilter = destroyFilter;
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!ObserverModule.testObserverOrDead(event.getPlayer())) {
            if (event.getPlayer().getItemInHand().getType() != Material.AIR) {
                if (NMS.getItemNBT(event.getPlayer().getItemInHand()).getString("projectile") != null) {
                    Bukkit.broadcastMessage("NBT Info: " + NMS.getItemNBT(event.getPlayer().getItemInHand()).getString("projectile") + " | Projectile ID: " + id);
                    Action action = event.getAction();
                    if (event.getPlayer().getRemainingItemCooldown(event.getPlayer().getItemInHand().getType()) == 0) {
                        if ((click.equalsIgnoreCase("left") && !(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) // TODO LEFT_CLICK("left") .equalsIgnoreCase()
                                || (click.equalsIgnoreCase("right") && !(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK))) return;
                        if (this.throwable) {
                            int amount = event.getPlayer().getItemInHand().getAmount();
                            if (amount == 0) {
                                return;
                            } else if (amount > 1) {
                                event.getPlayer().getItemInHand().setAmount(amount - 1);
                            } else {
                                event.getPlayer().setItemInHand(null);
                            }
                        }
                        event.getPlayer().startItemCooldown(event.getPlayer().getItemInHand().getType(), (cooldown * 20));
                        Entity entity = GameHandler.getGameHandler().getMatchWorld().spawnEntity(event.getPlayer().getEyeLocation(), this.projectile);
                        entity.setVelocity((event.getPlayer().getEyeLocation().getDirection()).multiply(this.velocity));
                        entity.setMetadata(id, new FixedMetadataValue(GameHandler.getGameHandler().getPlugin(), true));
                        if (entity instanceof Projectile) {
                            ((Projectile) entity).setShooter(event.getPlayer());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            ProjectileSource source = ((Projectile) event.getDamager()).getShooter();
            if (source instanceof Player) {
                ((Player) source).playSound(((Player) source).getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.2F, 0.5F);
            }
            if (event.getDamager().getType().equals(projectile) && event.getDamager().hasMetadata(id)) {
                event.setDamage(this.damage);
                if (event.getEntity() instanceof LivingEntity) {
                    for (PotionEffect effect : this.effects) {
                        ((LivingEntity) event.getEntity()).addPotionEffect(effect);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().getType().equals(projectile) && event.getEntity().hasMetadata(id)) {
            Set<Block> blocksToRemove = new HashSet<>();
            for (Block block : event.blockList()) {
                if (this.destroyFilter.evaluate(block, event.getEntity(), event).equals(FilterState.DENY)) {
                    blocksToRemove.add(block);
                }
            }
            for (Block block : blocksToRemove) {
                event.blockList().remove(block);
            }
        }
    }

}
