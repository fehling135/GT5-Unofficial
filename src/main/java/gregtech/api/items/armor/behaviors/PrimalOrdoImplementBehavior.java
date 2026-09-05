package gregtech.api.items.armor.behaviors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.jetbrains.annotations.NotNull;

import gregtech.api.items.armor.ArmorContext;

public class PrimalOrdoImplementBehavior implements IArmorBehavior {

    public static final PrimalOrdoImplementBehavior INSTANCE = new PrimalOrdoImplementBehavior();

    protected PrimalOrdoImplementBehavior() {}

    @Override
    public BehaviorName getName() {
        return BehaviorName.PrimalOrdoImplement;
    }

    @Override
    public void onArmorTick(@NotNull ArmorContext context) {
        if (context.isRemote()) return;

        EntityPlayer player = context.getPlayer();

        player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 202, 0, true));
    }
}
