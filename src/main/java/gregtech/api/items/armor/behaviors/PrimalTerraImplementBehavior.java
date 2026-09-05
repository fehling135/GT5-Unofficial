package gregtech.api.items.armor.behaviors;

public class PrimalTerraImplementBehavior implements IArmorBehavior {

    public static final PrimalTerraImplementBehavior INSTANCE = new PrimalTerraImplementBehavior();

    protected PrimalTerraImplementBehavior() {}

    @Override
    public BehaviorName getName() {
        return BehaviorName.PrimalTerraImplement;
    }
}
