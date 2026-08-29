package gregtech.common.tileentities.machines.multi;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.casing.Casings;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class MTESLICE extends MTEExtendedPowerMultiBlockBase<MTESLICE> implements ISurvivalConstructable {

    private static IStructureDefinition<MTESLICE> STRUCTURE_DEFINITION = null;

    private static final String tier1 = "tier1";
    private static final String tier2 = "tier2";
    private static final String tier3 = "tier3";

    private static final int OFFSET_X1 = 1;
    private static final int OFFSET_X2 = 1;
    private static final int OFFSET_X3 = 1;
    private static final int OFFSET_Y1 = 2;
    private static final int OFFSET_Y2 = 2;
    private static final int OFFSET_Y3 = 2;
    private static final int OFFSET_Z1 = 0;
    private static final int OFFSET_Z2 = 0;
    private static final int OFFSET_Z3 = 0;

    private static final int PARALLEL_PER_TIER = 1;
    private static final float SPEED = 1f;
    private static final float EU_EFFICIENCY = 1f;

    public MTESLICE(final int aID, final String aName, final String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public MTESLICE(final String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(final IGregTechTileEntity aTileEntity) {
        return new MTESLICE(this.mName);
    }

    @Override
    public IStructureDefinition<MTESLICE> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<MTESLICE>builder()
                .addShape(
                    tier1,
                    // spotless:off
                    new String[][]{{
                        "BBB",
                        "BBB",
                        "B~B",
                        "BBB",
                        "C C"
                    },{
                        "BBB",
                        "A A",
                        "A A",
                        "BBB",
                        "   "
                    },{
                        "BBB",
                        "BAB",
                        "BAB",
                        "BBB",
                        "C C"
                    }})
                    // spotless:on
                .addShape(
                    tier2,
                    // spotless:off
                    new String[][]{{
                        "BBB",
                        "BBB",
                        "B~B",
                        "BBB",
                        "D D"
                    },{
                        "BBB",
                        "A A",
                        "A A",
                        "BBB",
                        "   "
                    },{
                        "BBB",
                        "BAB",
                        "BAB",
                        "BBB",
                        "D D"
                    }})
                    // spotless:on
                .addShape(
                    tier3,
                    // spotless:off
                    new String[][]{{
                        "BBB",
                        "BBB",
                        "B~B",
                        "BBB",
                        "E E"
                    },{
                        "BBB",
                        "A A",
                        "A A",
                        "BBB",
                        "   "
                    },{
                        "BBB",
                        "BAB",
                        "BAB",
                        "BBB",
                        "E E"
                    }})
                    // spotless:on
                .addElement(
                    'B',
                    buildHatchAdder(MTESLICE.class)
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Maintenance, Energy.or(ExoticEnergy))
                        .casingIndex(Casings.RadiantNaquadahAlloyCasing.textureId)
                        .hint(1)
                        .buildAndChain(
                            onElementPass(MTESLICE::onCasingAdded, Casings.RadiantNaquadahAlloyCasing.asElement())))
                .addElement('A', chainAllGlasses())
                .addElement('C', ofFrame(Materials.Steel))
                .addElement('D', Casings.InfinityCooledCasing.asElement())
                .addElement('E', Casings.MiningNeutroniumCasing.asElement())
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Casings.RadiantNaquadahAlloyCasing.getCasingTexture(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_MULTI_BREWERY_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Casings.RadiantNaquadahAlloyCasing.getCasingTexture(), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_MULTI_BREWERY)
                .extFacing()
                .build() };
        }
        return new ITexture[] { Casings.RadiantNaquadahAlloyCasing.getCasingTexture() };
    }

    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("SLICE")
            .addBulkMachineInfo(1, 1F, 1F)
            .beginStructureBlock(3, 5, 3, true)
            .addController("Front center, 3rd layer")
            .addCasing("14-22", "Reinforced Wooden Casing", false)
            .addCasing("6", "Any Tiered Glass", false)
            .addCasing("4", "Steel Frame Box", false)
            .addEnergyHatch("1+", "Any casing", 1)
            .addMaintenanceHatch("1", "Any casing", 1)
            .addInputBus("1+", "Any casing", 1)
            .addInputHatch("1+", "Any casing", 1)
            .addOutputHatch("1+", "Any casing", 1)
            .addStructureInfo("")
            .addSubChannel(GTStructureChannels.BOROGLASS)
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic().setEuModifier(EU_EFFICIENCY)
            .setSpeedBonus(1F / SPEED)
            .setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public int getMaxParallelRecipes() {
        return (PARALLEL_PER_TIER * GTUtility.getTier(this.getMaxInputVoltage()));
    }

    private int casingAmount;

    private void onCasingAdded() {
        casingAmount++;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        if (stackSize.stackSize == 1) {
            buildPiece(tier1, stackSize, hintsOnly, OFFSET_X1, OFFSET_Y1, OFFSET_Z1);
        } else if (stackSize.stackSize == 2) {
            buildPiece(tier2, stackSize, hintsOnly, OFFSET_X2, OFFSET_Y2, OFFSET_Z2);
        } else {
            buildPiece(tier3, stackSize, hintsOnly, OFFSET_X3, OFFSET_Y3, OFFSET_Z3);
        }
        return;
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        int built = 0;
        if (mMachine) return -1;
        if (stackSize.stackSize == 1) {
            built += survivalBuildPiece(
                tier1,
                stackSize,
                OFFSET_X1,
                OFFSET_Y1,
                OFFSET_Z1,
                elementBudget,
                env,
                false,
                true);
        } else if (stackSize.stackSize == 2) {
            built += survivalBuildPiece(
                tier2,
                stackSize,
                OFFSET_X2,
                OFFSET_Y2,
                OFFSET_Z2,
                elementBudget,
                env,
                false,
                true);
        } else {
            built += survivalBuildPiece(
                tier3,
                stackSize,
                OFFSET_X3,
                OFFSET_Y3,
                OFFSET_Z3,
                elementBudget,
                env,
                false,
                true);
        }
        return built;
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        casingAmount = 0;
        String CurrentTier = "tier1";
        if (aStack != null) {
            if (aStack.isItemEqual(ItemList.EnergisedTesseract.get(1))) {
                CurrentTier = "tier2";
            } else if (aStack.isItemEqual(ItemList.Transdimensional_Alignment_Matrix.get(1))) {
                CurrentTier = "tier3";
            } else {
                errors.add(StructureErrors.of("GT5U.gui.text.structure_error.wrong_SLICE_itemslot"));
                return;
            }
        }

        if ("tier1".equals(CurrentTier)) {
            if (!checkPiece(CurrentTier, OFFSET_X1, OFFSET_Y1, OFFSET_Z1, errors)) return;
        } else if ("tier2".equals(CurrentTier)) {
            if (!checkPiece(CurrentTier, OFFSET_X2, OFFSET_Y2, OFFSET_Z2, errors)) return;
        } else {
            if (!checkPiece(CurrentTier, OFFSET_X3, OFFSET_Y3, OFFSET_Z3, errors)) return;
        }

        checkCasingMin(errors, casingAmount, 6);
        checkHasEnergyHatch(errors);
        checkHasMaintenanceHatch(errors);
        checkHasInputBus(errors);
        checkHasInputHatch(errors);
        checkHasOutputBus(errors);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.cutterRecipes;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }
}
