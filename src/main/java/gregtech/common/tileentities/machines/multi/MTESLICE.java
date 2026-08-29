package gregtech.common.tileentities.machines.multi;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.casing.Casings;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.ICasingTextureProvider;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEExtendedPowerMultiBlockBase;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;

public class MTESLICE extends MTEExtendedPowerMultiBlockBase<MTESLICE>
    implements ISurvivalConstructable, ICasingTextureProvider {

    private static IStructureDefinition<MTESLICE> STRUCTURE_DEFINITION = null;

    private static final String tier1 = "tier1";
    private static final String tier2 = "tier2";
    private static final String tier3 = "tier3";

    private byte mTier = 1;

    private static final int OFFSET_X1 = 6;
    private static final int OFFSET_X2 = 1;
    private static final int OFFSET_X3 = 1;
    private static final int OFFSET_Y1 = 7;
    private static final int OFFSET_Y2 = 2;
    private static final int OFFSET_Y3 = 2;
    private static final int OFFSET_Z1 = 0;
    private static final int OFFSET_Z2 = 0;
    private static final int OFFSET_Z3 = 0;

    private int casingAmount;

    private static final int BASE_PARALLEL_PER_TIER = 4;
    private static final float BASE_SPEED = 3f;
    private static final float BASE_EU_EFFICIENCY = 0.85f;

    private MTEHatchDynamoTunnel laserSource = null;
    private int laserAmps = 1;
    private int laserTier = 0;

    private final int MACHINEMODE_CUTTER = 0;
    private final int MACHINEMODE_LASER_ENGRAVER = 1;

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
                        "             ",
                        "    AAAAA    ",
                        "   C     C   ",
                        "   C     C   ",
                        "   C     C   ",
                        "   C     C   ",
                        "   C     C   ",
                        "    AA~AA    ",
                        "             "
                    },{
                        "    AAAAA    ",
                        "  AA     AA  ",
                        "             ",
                        "             ",
                        "             ",
                        "             ",
                        "             ",
                        "  AA     AA  ",
                        "    AAAAA    "
                    },{
                        "  AAAAAAAAA  ",
                        " A  DGGGD  A ",
                        "    D   D    ",
                        "             ",
                        "             ",
                        "             ",
                        "    D   D    ",
                        " A  DGGGD  A ",
                        "  AAAAAAAAA  "
                    },{
                        "  AAAAAAAAA  ",
                        " A AAAAAAA A ",
                        "C  BDAAADB  C",
                        "C  B FFF B  C",
                        "C  B FFF B  C",
                        "C  B FFF B  C",
                        "C  BDAAADB  C",
                        " A BGGGBBB A ",
                        "  AAAAAAAAA  "
                    },{
                        " AAAAAAAAAAA ",
                        "A DAAAAAAAD A",
                        "  DDAGBGADD  ",
                        "    F   F    ",
                        "    F   F    ",
                        "    F   F    ",
                        "  DDAHHHADD  ",
                        "A DBGGGBGGD A",
                        " AAAAAAAAAAA "
                    },{
                        " AAAAAAAAAAA ",
                        "A GAAAAAAAG A",
                        "   AGGBGGA   ",
                        "   F     F   ",
                        "   F     F   ",
                        "   F     F   ",
                        "   AHHHHHA   ",
                        "A GBGGBBGGG A",
                        " AAAAAAAAAAA "
                    },{
                        " AAAAAAAAAAA ",
                        "A GAAAAAAAG A",
                        "   ABBIBBA   ",
                        "   FE   EF   ",
                        "   F E E F   ",
                        "   F     F   ",
                        "   AHHHHHA   ",
                        "A GBBBBGGGG A",
                        " AAAAAAAAAAA "
                    },{
                        " AAAAAAAAAAA ",
                        "A GAAAAAAAG A",
                        "   AGGBGGA   ",
                        "   F     F   ",
                        "   F     F   ",
                        "   F     F   ",
                        "   AHHHHHA   ",
                        "A GGBGBBBBG A",
                        " AAAAAAAAAAA "
                    },{
                        " AAAAAAAAAAA ",
                        "A DAAAAAAAD A",
                        "  DDAGBGADD  ",
                        "    F   F    ",
                        "    F   F    ",
                        "    F   F    ",
                        "  DDAHHHADD  ",
                        "A DGBGGGGBD A",
                        " AAAAAAAAAAA "
                    },{
                        "  AAAAAAAAA  ",
                        " A AAAAAAA A ",
                        "C  BDAAADB  C",
                        "C  B FFF B  C",
                        "C  B FFF B  C",
                        "C  B FFF B  C",
                        "C  BDAAADB  C",
                        " A BBGGGGB A ",
                        "  AAAAAAAAA  "
                    },{
                        "  AAAAAAAAA  ",
                        " A  DGGGD  A ",
                        "    D   D    ",
                        "             ",
                        "             ",
                        "             ",
                        "    D   D    ",
                        " A  DGGGD  A ",
                        "  AAAAAAAAA  "
                    },{
                        "    AAAAA    ",
                        "  AA     AA  ",
                        "             ",
                        "             ",
                        "             ",
                        "             ",
                        "             ",
                        "  AA     AA  ",
                        "    AAAAA    "
                    },{
                        "             ",
                        "    AAAAA    ",
                        "   C     C   ",
                        "   C     C   ",
                        "   C     C   ",
                        "   C     C   ",
                        "   C     C   ",
                        "    AAAAA    ",
                        "             "
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
                    'A',
                    buildHatchAdder(MTESLICE.class)
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Maintenance, Energy, MultiAmpEnergy)
                        .casingIndex(Casings.RadiantNaquadahAlloyCasing.textureId)
                        .hint(1)
                        .buildAndChain(
                            onElementPass(MTESLICE::onCasingAdded, Casings.RadiantNaquadahAlloyCasing.asElement())))
                .addElement('B', Casings.AdvancedComputerCasing.asElement())
                .addElement('C', ofFrame(Materials.BlackPlutonium))
                .addElement('D', ofFrame(Materials.InfinityCatalyst))
                .addElement('E', ofFrame(Materials.CosmicNeutronium))
                .addElement('F', Casings.NanochipComplexGlass.asElement())
                .addElement('G', Casings.HeatResistantTriniumPlatedCasing.asElement())
                .addElement('H', ofBlock(GregTechAPI.sLaserRender, 0))
                .addElement(
                    'I',
                    buildHatchAdder(MTESLICE.class).anyOf(LaserSource)
                        .adder(MTESLICE::addLaserSource)
                        .casingIndex(Casings.RadiantNaquadahAlloyCasing.textureId)
                        .hint(2)
                        .build())
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // copied from HILE
    private boolean addLaserSource(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity != null) {
            final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof MTEHatchDynamoTunnel) {
                laserSource = (MTEHatchDynamoTunnel) aMetaTileEntity;
                laserSource.updateTexture(aBaseCasingIndex);
                // Snap the laser source toward the plate. Player can rotate it if they want after but this will look
                // nicer
                switch (getRotation()) {
                    case NORMAL -> laserSource.getBaseMetaTileEntity()
                        .setFrontFacing(ForgeDirection.DOWN);
                    case UPSIDE_DOWN -> laserSource.getBaseMetaTileEntity()
                        .setFrontFacing(ForgeDirection.UP);
                    case CLOCKWISE -> laserSource.getBaseMetaTileEntity()
                        .setFrontFacing(getDirection().getRotation(ForgeDirection.UP));
                    default -> laserSource.getBaseMetaTileEntity()
                        .setFrontFacing(getDirection().getRotation(ForgeDirection.DOWN));
                }
                laserAmps = (int) laserSource.maxAmperesOut();
                laserTier = (int) laserSource.getOutputTier();
                return true;
            }
        }
        return false;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        return Textures.BlockIcons.createTextureWithCasing(
            this,
            side,
            aFacing,
            aActive,
            OVERLAY_FRONT_MULTI_AUTOCLAVE,
            OVERLAY_FRONT_MULTI_AUTOCLAVE_GLOW,
            OVERLAY_FRONT_MULTI_AUTOCLAVE_ACTIVE,
            OVERLAY_FRONT_MULTI_AUTOCLAVE_ACTIVE_GLOW);
    }

    @Override
    public ITexture getCasingTexture() {
        return Casings.RadiantNaquadahAlloyCasing.getCasingTexture();
    }

    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Laser Engraver, Cutting Machine, S.L.I.C.E")
            .addBulkMachineInfo(4, 3F, 0.85F)
            .beginStructureBlock(3, 5, 3, true)
            .addController("Front center, 3rd layer")
            .addCasing("14-22", "Reinforced Wooden Casing", false)
            .addCasing("6", "Any Tiered Glass", false)
            .addCasing("4", "Steel Frame Box", false)
            .addEnergyHatch("1", "Any casing", 1)
            .addInputBus("1+", "Any casing", 1)
            .addInputHatch("1+", "Any casing", 1)
            .addMaintenanceHatch("1", "Any casing", 1)
            .addOutputHatch("1+", "Any casing", 1)
            .addStructureInfo("")
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            private int lastMode = -1;

            @NotNull
            @Override
            protected Stream<GTRecipe> findRecipeMatches(@Nullable RecipeMap<?> map) {
                int mode = getModeFromCircuit(inputItems);

                if (mode == -1) {
                    lastMode = -1;
                    return Stream.empty();
                }
                if (!(mode == lastMode)) {
                    lastRecipe = null;
                    lastMode = mode;
                }
                switch (mode) {
                    case MACHINEMODE_CUTTER -> {
                        return super.findRecipeMatches(RecipeMaps.cutterRecipes);
                    }
                    case MACHINEMODE_LASER_ENGRAVER -> {
                        return super.findRecipeMatches(RecipeMaps.laserEngraverRecipes);
                    }
                    default -> {
                        return super.findRecipeMatches(null);
                    }
                }
            }
        }.setEuModifier(getCurrentEUEfficiency())
            .setSpeedBonus(1F / getCurrentSpeed())
            .setMaxParallelSupplier(this::getTrueParallel);
    }

    private int getCurrentParallelPerTier() {
        int CURRENT_PARALLEL_PER_TIER = BASE_PARALLEL_PER_TIER;
        if (mTier == 1) {
            // add log4(Amp) to parallel
            CURRENT_PARALLEL_PER_TIER += (int) (Math.log(laserAmps) / Math.log(4));
        }
        return CURRENT_PARALLEL_PER_TIER;
    }

    private float getCurrentSpeed() {
        float CURRENT_SPEED = BASE_SPEED;
        if (mTier == 1) {
            // add tier/4 to speed
            CURRENT_SPEED += (laserTier / 4);
        }
        return CURRENT_SPEED;
    }

    private float getCurrentEUEfficiency() {
        float CURRENT_EU_EFFICIENCY = BASE_EU_EFFICIENCY;
        if (mTier == 1) {
            // minus tier/100 to eu efficiency
            CURRENT_EU_EFFICIENCY -= (laserTier / 100);
        }
        return CURRENT_EU_EFFICIENCY;
    }

    @Override
    public int getMaxParallelRecipes() {
        return (getCurrentParallelPerTier() * GTUtility.getTier(this.getMaxInputVoltage()));
    }

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
        if (mMachine) return -1;
        if (stackSize.stackSize == 1) {
            return survivalBuildPiece(
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
            return survivalBuildPiece(
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
            return survivalBuildPiece(
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
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        casingAmount = 0;
        if (aStack == null) {
            mTier = 1;
        } else {
            if (GTUtility.areStacksEqual(aStack, ItemList.EnergisedTesseract.get(1))) {
                mTier = 2;
            } else if (GTUtility.areStacksEqual(aStack, ItemList.Transdimensional_Alignment_Matrix.get(1))) {
                mTier = 3;
            } else {
                errors.add(StructureErrors.of("GT5U.gui.text.structure_error.wrong_SLICE_itemslot"));
                return;
            }
        }

        if (mTier == 1) {
            if (!checkPiece("tier1", OFFSET_X1, OFFSET_Y1, OFFSET_Z1, errors)) return;
        } else if (mTier == 2) {
            if (!checkPiece("tier2", OFFSET_X2, OFFSET_Y2, OFFSET_Z2, errors)) return;
        } else {
            if (!checkPiece("tier3", OFFSET_X3, OFFSET_Y3, OFFSET_Z3, errors)) return;
        }

        checkCasingMin(errors, casingAmount, 6);
        checkHasEnergyHatch(errors);
        checkHasMaintenanceHatch(errors);
        checkHasInputBus(errors);
        checkHasInputHatch(errors);
        checkHasOutputBus(errors);
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.laserEngraverRecipes, RecipeMaps.cutterRecipes);
    }

    private int getModeFromCircuit(ItemStack[] t) {
        for (ItemStack j : t) {
            if (j.getItem() == GTUtility.getIntegratedCircuit(0)
                .getItem()) {
                if (j.getItemDamage() == 15) {
                    return MACHINEMODE_CUTTER;
                } else if (j.getItemDamage() <= 16) {
                    return MACHINEMODE_LASER_ENGRAVER;
                }
            }
        }
        return -1;
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
