package gregtech.api.items.armor.behaviors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.jetbrains.annotations.NotNull;

import gregtech.api.items.armor.ArmorContext;

public class PrimalAquaImplementBehavior implements IArmorBehavior {

    public static final PrimalAquaImplementBehavior INSTANCE = new PrimalAquaImplementBehavior();

    protected PrimalAquaImplementBehavior() {}

    @Override
    public BehaviorName getName() {
        return BehaviorName.PrimalAquaImplement;
    }

    @Override
    public void onArmorTick(@NotNull ArmorContext context) {
        if (context.isRemote()) return;

        EntityPlayer player = context.getPlayer();

        if (player.isInWater()) {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 202, 0, true));
        }
    }
}
