package gregtech.api.items.armor.behaviors;

public class PrimalPerditioImplementBehavior implements IArmorBehavior {

    public static final PrimalPerditioImplementBehavior INSTANCE = new PrimalPerditioImplementBehavior();

    protected PrimalPerditioImplementBehavior() {}

    @Override
    public BehaviorName getName() {
        return BehaviorName.PrimalPerditioImplement;
    }
}
