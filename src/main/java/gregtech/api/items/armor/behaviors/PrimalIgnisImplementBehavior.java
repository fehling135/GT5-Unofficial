package gregtech.api.items.armor.behaviors;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.jetbrains.annotations.NotNull;

import gregtech.api.items.armor.ArmorContext;
import gregtech.api.items.armor.ArmorEventHandlers;

public class PrimalIgnisImplementBehavior implements IArmorBehavior {

    public static final PrimalIgnisImplementBehavior INSTANCE = new PrimalIgnisImplementBehavior();

    protected PrimalIgnisImplementBehavior() {}

    @Override
    public BehaviorName getName() {
        return BehaviorName.PrimalIgnisImplement;
    }

    @Override
    public void onArmorTick(@NotNull ArmorContext context) {
        if (context.isRemote()) return;

        EntityPlayer player = context.getPlayer();

        if (ArmorEventHandlers.countBehaviors(player, BehaviorName.PrimalIgnisImplement) >= 2
            && player.isInsideOfMaterial(Material.lava)) {
            player.setAir(300);
            player.addPotionEffect(new PotionEffect(Potion.blindness.id, 202, 0, true));
        }
    }
}
