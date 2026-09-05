package gregtech.api.items.armor.behaviors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import thaumcraft.api.IWarpingGear;

public class WarpBehavior implements IArmorBehavior, IWarpingGear {

    public final int warp;

    public WarpBehavior(int warp) {
        this.warp = warp;
    }

    @Override
    public BehaviorName getName() {
        return BehaviorName.Warp;
    }

    @Override
    public int getWarp(ItemStack s, EntityPlayer p) {
        return warp;
    }
}
