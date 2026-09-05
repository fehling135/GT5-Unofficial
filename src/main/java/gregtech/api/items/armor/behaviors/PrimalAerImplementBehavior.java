package gregtech.api.items.armor.behaviors;

import static gregtech.api.items.armor.behaviors.BehaviorName.Levitation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import gregtech.api.items.armor.ArmorContext;
import gregtech.api.items.armor.ArmorEventHandlers;

public class PrimalAerImplementBehavior implements IArmorBehavior {

    public static final PrimalAerImplementBehavior INSTANCE = new PrimalAerImplementBehavior();

    protected PrimalAerImplementBehavior() {}

    @Override
    public BehaviorName getName() {
        return BehaviorName.PrimalAerImplement;
    }

    @Override
    public void onArmorTick(@NotNull ArmorContext context) {
        if (context.isRemote()) return;

        EntityPlayer player = context.getPlayer();
        World world = player.worldObj;

        reflectProjectiles(world, player);

        int aerCount = ArmorEventHandlers.countBehaviors(player, BehaviorName.PrimalAerImplement);
        boolean canFly = aerCount >= 2;

        if (canFly) {
            player.fallDistance = 0;
            if (!player.capabilities.allowFlying) {
                player.capabilities.allowFlying = true;
                player.sendPlayerAbilities();
            }

            if (context.isBehaviorActive(Levitation)) {
                player.capabilities.isFlying = true;
            }
        } else if (!player.capabilities.isCreativeMode) {
            player.capabilities.isFlying = false;
            player.capabilities.allowFlying = false;
            player.sendPlayerAbilities();
        }
    }

    @Override
    public void onArmorUnequip(@NotNull ArmorContext context) {
        EntityPlayer player = context.getPlayer();

        if (!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }
    }

    private void reflectProjectiles(World world, EntityPlayer player) {
        // From Witching Gadgets:
        AxisAlignedBB aabb = AxisAlignedBB
            .getBoundingBox(
                player.posX - .5,
                player.posY - .5,
                player.posZ - .5,
                player.posX + .5,
                player.posY + .5,
                player.posZ + .5)
            .expand(4, 4, 4);
        for (Entity projectile : world.getEntitiesWithinAABB(Entity.class, aabb)) {
            if (projectile == null) continue;
            if (!(projectile instanceof IProjectile) || projectile.getClass()
                .getSimpleName()
                .equalsIgnoreCase("IManaBurst")) continue;

            Entity shooter = null;
            if (projectile instanceof EntityArrow) shooter = ((EntityArrow) projectile).shootingEntity;
            else if (projectile instanceof EntityThrowable) shooter = ((EntityThrowable) projectile).getThrower();

            if (shooter != null && shooter.equals(player)) continue;

            double delX = projectile.posX - player.posX;
            double delY = projectile.posY - player.posY;
            double delZ = projectile.posZ - player.posZ;

            double angle = (delX * projectile.motionX + delY * projectile.motionY + delZ * projectile.motionZ)
                / (Math.sqrt(delX * delX + delY * delY + delZ * delZ) * Math.sqrt(
                    projectile.motionX * projectile.motionX + projectile.motionY * projectile.motionY
                        + projectile.motionZ * projectile.motionZ));
            angle = Math.acos(angle);
            if (angle < 3 * (Math.PI / 4)) continue;

            if (shooter != null) {
                delX = -projectile.posX + shooter.posX;
                delY = -projectile.posY + (shooter.posY + shooter.getEyeHeight());
                delZ = -projectile.posZ + shooter.posZ;
            }

            double curVel = Math.sqrt(delX * delX + delY * delY + delZ * delZ);
            delX /= curVel;
            delY /= curVel;
            delZ /= curVel;
            double newVel = Math.sqrt(
                projectile.motionX * projectile.motionX + projectile.motionY * projectile.motionY
                    + projectile.motionZ * projectile.motionZ);
            projectile.motionX = newVel * delX;
            projectile.motionY = newVel * delY;
            projectile.motionZ = newVel * delZ;
        }
    }
}
