package tectech.thing.metaTileEntity.multi;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static tectech.thing.metaTileEntity.multi.base.TTMultiblockBase.HatchElement.InputData;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
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
import gregtech.api.metatileentity.GregTechTileClientEvents;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.shutdown.ShutDownReason;
import gregtech.api.util.shutdown.SimpleShutDownReason;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public class MTESLICE extends TTMultiblockBase implements ISurvivalConstructable, ICasingTextureProvider {

    private static IStructureDefinition<MTESLICE> STRUCTURE_DEFINITION = null;

    private static final String tier1 = "tier1";
    private static final String tier2 = "tier2";
    private static final String tier3 = "tier3";

    private byte mTier = 0;

    private static final int OFFSET_X1 = 6;
    private static final int OFFSET_X2 = 4;
    private static final int OFFSET_X3 = 21;
    private static final int OFFSET_Y1 = 7;
    private static final int OFFSET_Y2 = 10;
    private static final int OFFSET_Y3 = 16;
    private static final int OFFSET_Z1 = 0;
    private static final int OFFSET_Z2 = 1;
    private static final int OFFSET_Z3 = 13;

    private int casingAmount;

    private static final int BASE_PARALLEL_PER_TIER = 8;
    private static final float BASE_SPEED = 3f;
    private static final float BASE_EU_EFFICIENCY = 0.85f;

    private MTEHatchDynamoTunnel laserSource = null;
    private int laserAmps = 0;
    private int laserTier = 0;

    private final byte MACHINEMODE_CUTTER = 0;
    private final byte MACHINEMODE_LASER_ENGRAVER = 1;
    private byte currentmode = -1;

    private static final long REQUIRED_COMPUTATION_PER_TICK = 1000;

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
    public IStructureDefinition<MTESLICE> getStructure_EM() {
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
                        "                                                               ",
                        "                                                               ",
                        "                                                               ",
                        "                                                               ",
                        "       K   K  K   K  K   K  K   K  K   K  K   K  K   K  K   K  ",
                        "                                                               ",
                        " K                                                             ",
                        " K                                                             ",
                        " KEEEEEKDDDK  KDDDK  KDDDK  KDDDK  KDDDK  KDDDK  KDDDK  KDDDK  ",
                        " K     K   K  K   K  K   K  K   K  K   K  K   K  K   K  K   K  ",
                        " K     K   K  K   K  K   K  K   K  K   K  K   K  K   K  K   K  ",
                        " K     K   K  K   K  K   K  K   K  K   K  K   K  K   K  K   K  ",
                        " K     KKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKK  "
                    },{
                        "                                                               ",
                        "                                                               ",
                        "                                                               ",
                        "       K   K  K   K  K   K  K   K  K   K  K   K  K   K  K   K  ",
                        "       KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  ",
                        "      EBFFFBEEBFFFBEEBFFFBEEBFFFBEEBFFFBEEBFFFBEEBFFFBEEBFFFBE ",
                        " K    EBFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB E",
                        " KKKKKEBFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB E",
                        " KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKE",
                        " KJJJJJKJJJC  CJJJC  CJJJC  CJJJC  CJJJC  CJJJC  CJJJC  CJJJCKK",
                        " KJJ~JJKJJJC  CJJJC  CJJJC  CJJJC  CJJJC  CJJJC  CJJJC  CJJJCKK",
                        " KJJJJJKJJJC  CJJJC  CJJJC  CJJJC  CJJJC  CJJJC  CJJJC  CJJJCKK",
                        " KJJJJJKMMMKMMKMMMKMMKMMMKMMKMMMKMMKMMMKMMKMMMKMMKMMMKMMKMMMKKK"
                    },{
                        "                                                               ",
                        "                                                               ",
                        "                                             KKKKKKKKKKKKKKK   ",
                        "        JJJ    JJJ    JJJ    JJJ    JJJ    JJJ    JJJ    JJJ   ",
                        "      EM   MEEM   MEEM   MEEM   MEEM   MEEM   MEEM   MEEM   MKK",
                        "     MN     NN     NN     NN     NN     NN     NN     NN     NK",
                        " K JJMN     NN     NN     NN     NN     NN     NN     NN     NK",
                        " KK  MN     NN     NN     NN     NN     NN     NN     NN     NK",
                        " EK   NKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNK",
                        "  J        KBBK   KBBK   KBBK   KBBK   KBBK   KBBK   KBBK     K",
                        "  J        KBBK   KBBK   KBBK   KBBK   KBBK   KBBK   KBBK     K",
                        "  J        KBBK   KBBK   KBBK   KBBK   KBBK   KBBK   KBBK     K",
                        "  JJJJJJJJJKKKKJJJKKKKJJJKKKKJJJKKKKJJJKKKKJJJKKKKJJJKKKKJJJJJK"
                    },{
                        "                                              M  M  M  M  M    ",
                        "                                       M     EMFFMFFMFFMFFME   ",
                        "                                       M   KKKMMMMMMMMMMMMMK   ",
                        "        JJJ    JJJ    JJJ    JJJ    JJJM   JJJ    JJJ    JJJ   ",
                        "     MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM  ",
                        "     MNCCCCCNNCCCCCNNCCCCCNNCCCCCNNCCCCCNNCCCCCNNCCCCCNNCCCCCN ",
                        " K JJ N     NN     NN     NN     NN     NN     NN     NN     N ",
                        " KK L NCCCCCNNCCCCCNNCCCCCNNCCCCCNNCCCCCNNCCCCCNNCCCCCNNCCCCCN ",
                        " EK L NKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKN ",
                        "  J L                                                         K",
                        "  J L                                                         K",
                        "  J L                                                         K",
                        "  JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJK"
                    },{
                        "                                             KMKKMKKMKKMKKMK   ",
                        "                                       M   KKKM  M  M  M  MK   ",
                        "                                           KKKMMMMMMMMMMMMMK   ",
                        "        JJJ    JJJ    JJJ    JJJ    JJJ    JJJ    JJJ    JJJ   ",
                        "      EM   MEEM   MEEM   MEEM   MEEM   MEEM   MEEM   MEEM   MKK",
                        "     MN     NN     NN     NN     NN     NN     NN     NN     NK",
                        " K JJ N     NN     NN     NN     NN     NN     NN     NN     NK",
                        " KK L N     NN     NN     NN     NN     NN     NN     NN     NK",
                        " EK   NKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNNKKKKKNK",
                        "  J                                                           K",
                        "  J                                                           K",
                        "  J                                                           K",
                        "  JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJK"
                    },{
                        "                                              MMMMMMMMMMMMM    ",
                        "                                       MMMMMMMMMMMMMMMMMMMM    ",
                        "                                       C   KKKMMMMMMMMMMMMMK   ",
                        "       K   K  K   K  K   K  K   K  K   K  KCCCK  KCCCK  KCCCK  ",
                        "       KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  ",
                        "MMMMMMEBFFFBEEBFFFBEEBFFFBEEBFFFBEEBFFFBEEBFFFBEEBFFFBEEBFFFBE ",
                        "MK JJKKBFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB E",
                        "MKK LKKBFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB  BFFFB E",
                        "MEK   KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKE",
                        "M J                                                          KK",
                        "M J                                                          KK",
                        "M J                                                          KK",
                        "M JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJKK"
                    },{
                        "                                             KMKKMKKMKKMKKMK   ",
                        "                                           KKKM  M  M  M  MK   ",
                        "                                           KKKMMMMMMMMMMMMMK   ",
                        "                                                               ",
                        "       KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  ",
                        "     M  FMF    FMF    FMF    FMF    FMF    FMF    FMF    FMF   ",
                        " K JJKE F F    F F    F F    F F    F F    F F    F F    F F   ",
                        " KK LK  FLF    FLF    FLF    FLF    FLF    FLF    FLF    FLF   ",
                        " EK   KKKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  ",
                        "  J         JJ     JJ     JJ     JJ     JJ     JJ     JJ     K ",
                        "  J                                                          K ",
                        "  J                                                          K ",
                        "  JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJK "
                    },{
                        "                                              M  M  M  M  M    ",
                        "                                             EMFFMFFMFFMFFME   ",
                        "                                           KKKMMMMMMMMMMMMMK   ",
                        "                                                               ",
                        "        D D    D D    D D    D D    D D    D D    D D    D D   ",
                        "MMMMMM  FMF    FMF    FMF    FMF    FMF    FMF    FMF    FMF   ",
                        "MK JJKE F F    F F    F F    F F    F F    F F    F F    F F   ",
                        "MKK LK  FLF    FLF    FLF    FLF    FLF    FLF    FLF    FLF   ",
                        "MEK   KKKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  ",
                        "M J         JJ     JJ     JJ     JJ     JJ     JJ     JJ     K ",
                        "M J                                                          K ",
                        "M J                                                          K ",
                        "M JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJK "
                    },{
                        "                                                               ",
                        "                                                               ",
                        "                                             KKKKKKKKKKKKKKK   ",
                        "                                              C  C C C  C C    ",
                        "       KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KDCDK  KDCDK  ",
                        "     M  FMF    FMF    FMF    FMF    FMF    FMF    FMF    FMF   ",
                        " K JJKE F F    F F    F F    F F    F F    F F    F F    F F   ",
                        " KK LK  FLF    FLF    FLF    FLF    FLF    FLF    FLF    FLF   ",
                        " EK   KKKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  ",
                        "  J         JJ     JJ     JJ     JJ     JJ     JJ     JJ     K ",
                        "  J                                                          K ",
                        "  J                                                          K ",
                        "  JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJK "
                    },{
                        "                                                               ",
                        "                                                               ",
                        "                                                               ",
                        "       K   K  K   K  K   K  K   K  K   K  K   K  K   K  K   K  ",
                        "       KD DKEEKD DKEEKD DKEEKD DKEEKD DKEEKD DKEEKD DKEEKD DKK ",
                        "MMMMMMEBFMFBKKBFMFBKKBFMFBKKBFMFBKKBFMFBKKBFMFBKKBFMFBKKBFMFBK ",
                        "MK JJKKBF FBKKBF FBKKBF FBKKBF FBKKBF FBKKBF FBKKBF FBKKBF FBK ",
                        "MKK LKKBFLFBKKBFLFBKKBFLFBKKBFLFBKKBFLFBKKBFLFBKKBFLFBKKBFLFBK ",
                        "MEK   KKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKK ",
                        "M J    MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMK ",
                        "M J                                                          K ",
                        "M J                                                          K ",
                        "M JJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJJK "
                    },{
                        "                                                               ",
                        "                                                               ",
                        "                                                               ",
                        "       KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  KKKKK  ",
                        "    KKKKJJJKKKKJJJKKKKJJJKKKKJJJKKKKJJJKKKKJJJKKKKJJJKKKKJJJK  ",
                        "    KMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMKE ",
                        " K JJ                                                       KE ",
                        " KK LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLKE ",
                        " EK  KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKE ",
                        "  JJJJJKKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKKE ",
                        "  JJJJJKKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKKE ",
                        "  JJJJJKKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKJJJJKKKKE ",
                        "  JJJJJKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKE "
                    },{
                        "                                                               ",
                        "                                                               ",
                        "                                                               ",
                        "       K   K  K   K  K   K  K   K  K   K  K   K  K   K  K   K  ",
                        "     EEKD DKEEKD DKEEKD DKEEKD DKEEKD DKEEKD DKEEKD DKEEKD DKK ",
                        "     KKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBK ",
                        " K JJKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBK ",
                        " KKJJKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBKKBKKKBK ",
                        " KKJJEEKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKEEKKKKKK ",
                        " KKJJ  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DKK ",
                        " KKJJ  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DKK ",
                        " KKJJ  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DKK ",
                        " KKJJ  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DKK "
                    },{
                        "                                                               ",
                        "                                                               ",
                        "                                                               ",
                        "                                                               ",
                        "       KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  ",
                        "        D D    D D    D D    D D    D D    D D    D D    D D   ",
                        " K      D D    D D    D D    D D    D D    D D    D D    D D   ",
                        " K      D D    D D    D D    D D    D D    D D    D D    D D   ",
                        " K     KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  KD DK  ",
                        " K                                                             ",
                        " K                                                             ",
                        " K                                                             ",
                        " K                                                             "
                    }})
                    // spotless:on
                .addShape(
                    tier3,
                    // spotless:off
                    new String[][]{{
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                   M   M                   ",
                        "                   MM MM                   ",
                        "                   MMTMM                   ",
                        "                   MM MM                   ",
                        "                   M   M                   ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                   M   M                   ",
                        "                   MTTTM                   ",
                        "                   M   M                   ",
                        "                   MTTTM                   ",
                        "                  UM   MU                  ",
                        "                 KUKKKKKUK                 ",
                        "                 KUKKOKKUK                 ",
                        "                 KUKKKKKUK                 ",
                        "                  UM   MU                  ",
                        "                   MTTTM                   ",
                        "                   M   M                   ",
                        "                   MTTTM                   ",
                        "                   M   M                   ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                   M   M                   ",
                        "                   MTTTM                   ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                  KMPPPMK                  ",
                        "              KKK KMPPPMK KKK              ",
                        "              KKK KMPSPMK KKK              ",
                        "              KKK KMPPPMK KKK              ",
                        "                  KMPPPMK                  ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                   MTTTM                   ",
                        "                   M   M                   ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                   MTTTM                   ",
                        "                  U     U                  ",
                        "                  U KKK U                  ",
                        "                    FFF                    ",
                        "                    FFF                    ",
                        "                    FFF                    ",
                        "                 KK KKK KK                 ",
                        "                 KUPQQQPUK                 ",
                        "            KK   KUP T PUK   KK            ",
                        "            KK   KUP T PUK   KK            ",
                        "            KK   KUP T PUK   KK            ",
                        "                 KUPQQQPUK                 ",
                        "                 KK KKK KK                 ",
                        "                    FFF                    ",
                        "                    FFF                    ",
                        "                    FFF                    ",
                        "                  U KKK U                  ",
                        "                  U     U                  ",
                        "                   MTTTM                   ",
                        "                                           "
                    },{
                        "                   M   M                   ",
                        "                  U     U                  ",
                        "                                           ",
                        "                   KKKKK                   ",
                        "                   K   K                   ",
                        "                 FFK   KFF                 ",
                        "                 FFK   KFF                 ",
                        "       KKK       FFKKKKKFF       KKK       ",
                        "                  KPQQQPK                  ",
                        "          KK      RP T PR      KK          ",
                        "          KK      RP   PR      KK          ",
                        "          KK      RP T PR      KK          ",
                        "                  KPQQQPK                  ",
                        "       KKK       FFKKKKKFF       KKK       ",
                        "                 FFK   KFF                 ",
                        "                 FFK   KFF                 ",
                        "                   K   K                   ",
                        "                   KKKKK                   ",
                        "                                           ",
                        "                  U     U                  ",
                        "                   M   M                   "
                    },{
                        "                  U     U                  ",
                        "                                           ",
                        "                                           ",
                        "                   MKKKM                   ",
                        "                 KKM   MKK                 ",
                        "              FFF  M   M  FFF              ",
                        "       KK     FFF  M   M  FFF     KK       ",
                        "     KK       FFF  MKKKM  FFF       KK     ",
                        "        U         KPQQQPK         U        ",
                        "        UK         F   F         KU        ",
                        "       UUK         F   F         KUU       ",
                        "        UK         F   F         KU        ",
                        "        U         KPQQQPK         U        ",
                        "     KK       FFF  MKKKM  FFF       KK     ",
                        "       KK     FFF  M   M  FFF     KK       ",
                        "              FFF  M   M  FFF              ",
                        "                 KKM   MKK                 ",
                        "                   MKKKM                   ",
                        "                                           ",
                        "                                           ",
                        "                  U     U                  "
                    },{
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                 KKKMMMKKK                 ",
                        "              KKK   MMM   KKK              ",
                        "       K    FF      MMM      FF    K       ",
                        "      KUUU  FF      MMM      FF  UUUK      ",
                        "     K PP   FF      MMM      FF   PP K     ",
                        "       PP  U       KPPPK       U  PP       ",
                        "       UK  U        FFF        U  KU       ",
                        "      T K  U        FFF        U  K T      ",
                        "       UK  U        FFF        U  KU       ",
                        "       PP  U       KPPPK       U  PP       ",
                        "     K PP   FF      MMM      FF   PP K     ",
                        "      KUUU  FF      MMM      FF  UUUK      ",
                        "       K    FF      MMM      FF    K       ",
                        "              KKK   MMM   KKK              ",
                        "                 KKKMMMKKK                 ",
                        "                                           ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "              KKK         KKK              ",
                        "        K   KK               KK   K        ",
                        "      KKUU K                   K UUKK      ",
                        "     KUUPP K                   K PPUUK     ",
                        "    K PPPP K                   K PPPP K    ",
                        "      PPPPPK        KKK        KPPPPP      ",
                        "      UKPPP                     PPPKU      ",
                        "     U OSPP                     PPSO U     ",
                        "      UKPPP                     PPPKU      ",
                        "      PPPPPK        KKK        KPPPPP      ",
                        "    K PPPP K                   K PPPP K    ",
                        "     KUUPP K                   K PPUUK     ",
                        "      KKUU K        T T        K UUKK      ",
                        "        K   KK      T T      KK   K        ",
                        "              KKK         KKK              ",
                        "                                           ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "         K   K    UUUUUUU    K   K         ",
                        "       KKUU K     PPPPPPP     K UUKK       ",
                        "       UUP KT                 TK PUU       ",
                        "     KUPPPKM                   MKPPPUK     ",
                        "    K PPKKKM                   MKKKPP K    ",
                        "     UPPQQQPK                 KPQQQPPU     ",
                        "     UKPT  P                   P  TPKU     ",
                        "     UKST  P                   P  TSKU     ",
                        "     UKPT  P                   P  TPKU     ",
                        "     UPPQQQPK                 KPQQQPPU     ",
                        "    K PPKKKM                   MKKKPP K    ",
                        "     KUPPPKM                   MKPPPUK     ",
                        "       UUP KT       T T       TK PUU       ",
                        "       KKUU K     PPPPPPP     K UUKK       ",
                        "         K   K    UUUUUUU    K   K         ",
                        "                                           ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                  KKKKKKK                  ",
                        "        KKU  K  UUQQQQQQQUU  K  UKK        ",
                        "        UU  KT  PP       PP  TK  UU        ",
                        "       UPPKKM                 MKKPPU       ",
                        "      UPPPPM                   MPPPPU      ",
                        "    K  PKKKM                   MKKKP  K    ",
                        "       PQQQPK                 KPQQQP       ",
                        "     K P T F                   F T P K     ",
                        "     K P   F                   F   P K     ",
                        "     K P T F                   F T P K     ",
                        "       PQQQPK                 KPQQQP       ",
                        "    K  PKKKM                   MKKKP  K    ",
                        "      UPPPPM                   MPPPPU      ",
                        "       UPPKKM       T T       MKKPPU       ",
                        "        UU  KT  PP PPPPP PP  TK  UU        ",
                        "        KKU  K  UUQQQQQQQUU  K  UKK        ",
                        "                  KKKKKKK                  ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                     K                     ",
                        "          KU    KK   Q   KK    UK          ",
                        "         UU  KUUQQVVVVVVVQQUUK  UU         ",
                        "        U PKKMPP           PPMKKP U        ",
                        "         KPPM                 MPPK         ",
                        "        KPPMR                 RMPPK        ",
                        "        KKKM                   MKKK        ",
                        "       PQQQPK                 KPQQQP       ",
                        "    K  P   F                   F   P  K    ",
                        "    K  P   F                   F   P  K    ",
                        "    K  P   F                   F   P  K    ",
                        "       PQQQPK                 KPQQQP       ",
                        "        KKKM                   MKKK        ",
                        "        KPPMR                  MPPK        ",
                        "         KPPM                 MPPK         ",
                        "        U PKKMPP   P   P   PPMKKP U        ",
                        "         UU  KUUQQVVKKKVVQQUUK  UU         ",
                        "          KU    KK   Q   KK    UK          ",
                        "                     K                     ",
                        "                                           "
                    },{
                        "                                           ",
                        "           KU        K        UK           ",
                        "          UU  KK     Q     KK  UU          ",
                        "           PKKQQVVVVVVVVVVVQQKKP           ",
                        "          KPPM               MPPK          ",
                        "       KKKPPR                 RPPKKK       ",
                        "       KMMMR                   RMMMK       ",
                        "       KMMMPR                 RPMMMK       ",
                        "      UKPPPK                   KPPPKU      ",
                        "    K U PFF                     FFP U K    ",
                        "    K U PFF                     FFP U K    ",
                        "    K U PFF                     FFP U K    ",
                        "      UKPPPK                   KPPPKU      ",
                        "       KMMMPR                  PMMMK       ",
                        "       KMMMR                   RMMMK       ",
                        "       KKKPPR                 RPPKKK       ",
                        "          KPPM     P   P     MPPK          ",
                        "           PKKQQVVVVKKKVVVVQQKKP           ",
                        "          UU  KK     Q     KK  UU          ",
                        "           KU        K        UK           ",
                        "                                           "
                    },{
                        "            KU               UK            ",
                        "           UU        K        UU           ",
                        "            PK    VVVQVVV    KP            ",
                        "           KUQVVVV       VVVVQUK           ",
                        "       KKKKPP                 PPKKKK       ",
                        "      F TMMR                   RMMT F      ",
                        "      F   R                     R   F      ",
                        "      F    R                   R    F      ",
                        "        KKK                     KKK        ",
                        "   K                                   K   ",
                        "   K                                   K   ",
                        "   K                                   K   ",
                        "        KKK                     KKK        ",
                        "      F    R                        F      ",
                        "      F   R                         F      ",
                        "      F TMMR                   RMMT F      ",
                        "       KKKKPP      P   P      PPKKKK       ",
                        "           KUQVVVV  KKK  VVVVQUK           ",
                        "            PK    VVVQVVV    KP            ",
                        "           UU        K        UU           ",
                        "            KU               UK            "
                    },{
                        "            UU               UU            ",
                        "             K     KKKKK     K             ",
                        "            KQ  VVVQQQQQVVV  QK            ",
                        "        KKKKQVVV           VVVQKKKK        ",
                        "       K TMM                   MMT K       ",
                        "      F                             F      ",
                        "      F                             F      ",
                        "      F                             F      ",
                        "                                           ",
                        "   K                                   K   ",
                        "   K                                   K   ",
                        "   K                                   K   ",
                        "                                           ",
                        "      F                             F      ",
                        "      F                             F      ",
                        "      F                             F      ",
                        "       K TMM       PP~PP       MMT K       ",
                        "        KKKKQVVV           VVVQKKKK        ",
                        "            KQ  VVVQQQQQVVV  QK            ",
                        "             K     KKKKK     K             ",
                        "            UU               UU            "
                    },{
                        "                     K                     ",
                        "              K  KK  Q  KK  K              ",
                        "           K  QVVQQVVVVVQQVVQ  K           ",
                        "       K  UQVV               VVQU  K       ",
                        "      K   P                     P   K      ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "                                           ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "                                           ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "      K   P                     P   K      ",
                        "       K  UQVV               VVQU  K       ",
                        "           K  QVVQQVVVVVQQVVQ  K           ",
                        "              K  KK  Q  KK  K              ",
                        "                     K                     "
                    },{
                        "                     K                     ",
                        "               KK   VQV   KK               ",
                        "           K  VQQVVV   VVVQQV  K           ",
                        "       K  UQVV               VVQU  K       ",
                        "      K   P                     P   K      ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "                                           ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "                                           ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "      K   P                     P   K      ",
                        "       K  UQVV               VVQU  K       ",
                        "           K  VQQVVV   VVVQQV  K           ",
                        "               KK   VQV   KK               ",
                        "                     K                     "
                    },{
                        "                K    K    K                ",
                        "               KQ VVVQVVV QK               ",
                        "          K  VVQVV       VVQVV  K          ",
                        "       K UQVV                 VVQU K       ",
                        "      K  P                       P  K      ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "                                           ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "                                           ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "      K  P                       P  K      ",
                        "       K UQVV                 VVQU K       ",
                        "          K  VVQVV       VVQVV  K          ",
                        "               KQ VVVQVVV QK               ",
                        "                K    K    K                "
                    },{
                        "                 K KKKKK K                 ",
                        "              K  QVQQQQQVQ  K              ",
                        "          K  VQVV         VVQV  K          ",
                        "      K  UQVV                 VVQU  K      ",
                        "     K   P                       P   K     ",
                        "    F                                 F    ",
                        "    F                                 F    ",
                        "   KF                                 FK   ",
                        "   K                                   K   ",
                        " K K                                   K K ",
                        " K K                                   K K ",
                        " K K                                   K K ",
                        "   K                                   K   ",
                        "   KF                                 FK   ",
                        "    F                                 F    ",
                        "    F                                 F    ",
                        "     K   P                       P   K     ",
                        "      K  UQVV                 VVQU  K      ",
                        "          K  VQVV         VVQV  K          ",
                        "              K  QVQQQQQVQ  K              ",
                        "                 K KKKKK K                 "
                    },{
                        "     U            K     K            U     ",
                        "    U         K VVQVVVVVQVV K         U    ",
                        "   U     K  VVQV           VQVV  K     U   ",
                        "   U  K UQVV                   VVQU K  U   ",
                        "  U  K  P                         P  K  U  ",
                        "  U F                                 F U  ",
                        "  U F                                 F U  ",
                        "  UKF                                 FKU  ",
                        " UKUKK                               KKUKU ",
                        " UKUR                                 RUKU ",
                        " UKUR                                 RUKU ",
                        " UKUR                                 RUKU ",
                        " UKUKK                               KKUKU ",
                        "  UKF                                 FKU  ",
                        "  U F                                 F U  ",
                        "  U F                                 F U  ",
                        "  U  K  P                         P  K  U  ",
                        "   U  K UQVV                   VVQU K  U   ",
                        "   U     K  VVQV           VQVV  K     U   ",
                        "    U         K VVQ     QVV K         U    ",
                        "     U            KVVVVVK            U     "
                    },{
                        "    M            K       K            M    ",
                        "   M         K  VQVVVVVVVQV  K         M   ",
                        "  M      K  VQVV           VVQV  K      M  ",
                        "  M KMK UQVV                   VVQU KMK M  ",
                        " M  KM  P                         P  MK  M ",
                        " M  KM                               MK  M ",
                        " M  KM                               MK  M ",
                        " M  KM                               MK  M ",
                        "MMMPPPK                             KPPPMMM",
                        "MKMPPF                               FPPMKM",
                        "MKMPPF                               FPPMKM",
                        "MKMPPF                               FPPMKM",
                        "MMMPPPK                             KPPPMMM",
                        " M  KM                               MK  M ",
                        " M  KM                               MK  M ",
                        " M  KM                               MK  M ",
                        " M  KM  P                         P  MK  M ",
                        "  M KMK UQVV                   VVQU KMK M  ",
                        "  M      K  VQVV           VVQV  K      M  ",
                        "   M         K  VQ       QV  K         M   ",
                        "    M            KVVVVVVVK            M    "
                    },{
                        "                 K       K                 ",
                        "   T         K VVQVVVVVVVQVV K         T   ",
                        "         K  VQV             VQV  K         ",
                        "  TKKKM UQVV                   VVQU MKKKT  ",
                        "   F  M P                         P M  F   ",
                        " T F  M                             M  F T ",
                        "   F  M                             M  F   ",
                        " T KKKM                             MKKK T ",
                        "  PQQQPK                           KPQQQP  ",
                        "MKP   F                             F   PKM",
                        "MKP   F                             F   PKM",
                        "MKP   F                             F   PKM",
                        "  PQQQPK                           KPQQQP  ",
                        " T KKKM                             MKKK T ",
                        "   F  M                             M  F   ",
                        " T F  M                             M  F T ",
                        "   F  M P                         P M  F   ",
                        "  TKKKM UQVV                   VVQU MKKKT  ",
                        "         K  VQV             VQV  K         ",
                        "   T         K VVQ       QVV K         T   ",
                        "                 KVVVVVVVK                 "
                    },{
                        "              KKKK       KKKK              ",
                        "   T      KKKKQQQQVVVVVVVQQQQKKKK      T   ",
                        "         KQQQQV             VQQQQK         ",
                        "  TKKKM UQVV                   VVQU MKKKT  ",
                        "   F  M P                         P M  F   ",
                        " T F  M                             M  F T ",
                        "   F  M                             M  F   ",
                        " T KKKM                             MKKK T ",
                        "  PQQQPK                           KPQQQP  ",
                        " KPTT F                             F TTPK ",
                        "TOST  F                             F  TSOT",
                        " KPTT F                             F TTPK ",
                        "  PQQQPK                           KPQQQP  ",
                        " T KKKM                             MKKK T ",
                        "   F  M                             M  F   ",
                        " T F  M                             M  F T ",
                        "   F  M P                         P M  F   ",
                        "  TKKKM UQVV                   VVQU MKKKT  ",
                        "         KQQQQV             VQQQQK         ",
                        "   T      KKKKQQQQ       QQQQKKKK      T   ",
                        "              KKKKVVVVVVVKKKK              "
                    },{
                        "                 K       K                 ",
                        "   T         K VVQVVVVVVVQVV K         T   ",
                        "         K  VQV             VQV  K         ",
                        "  TKKKM UQVV                   VVQU MKKKT  ",
                        "   F  M P                         P M  F   ",
                        " T F  M                             M  F T ",
                        "   F  M                             M  F   ",
                        " T KKKM                             MKKK T ",
                        "  PQQQPK                           KPQQQP  ",
                        "MKP   F                             F   PKM",
                        "MKP   F                             F   PKM",
                        "MKP   F                             F   PKM",
                        "  PQQQPK                           KPQQQP  ",
                        " T KKKM                             MKKK T ",
                        "   F  M                             M  F   ",
                        " T F  M                             M  F T ",
                        "   F  M P                         P M  F   ",
                        "  TKKKM UQVV                   VVQU MKKKT  ",
                        "         K  VQV             VQV  K         ",
                        "   T         K VVQ       QVV K         T   ",
                        "                 KVVVVVVVK                 "
                    },{
                        "    M            K       K            M    ",
                        "   M         K  VQVVVVVVVQV  K         M   ",
                        "  M      K  VQVV           VVQV  K      M  ",
                        "  M KMK UQVV                   VVQU KMK M  ",
                        " M  KM  P                         P  MK  M ",
                        " M  KM                               MK  M ",
                        " M  KM                               MK  M ",
                        " M  KM                               MK  M ",
                        "MMMPPPK                             KPPPMMM",
                        "MKMPPF                               FPPMKM",
                        "MKMPPF                               FPPMKM",
                        "MKMPPF                               FPPMKM",
                        "MMMPPPK                             KPPPMMM",
                        " M  KM                               MK  M ",
                        " M  KM                               MK  M ",
                        " M  KM                               MK  M ",
                        " M  KM  P                         P  MK  M ",
                        "  M KMK UQVV                   VVQU KMK M  ",
                        "  M      K  VQVV           VVQV  K      M  ",
                        "   M         K  VQ       QV  K         M   ",
                        "    M            KVVVVVVVK            M    "
                    },{
                        "     U            K     K            U     ",
                        "    U         K VVQVVVVVQVV K         U    ",
                        "   U     K  VVQV           VQVV  K     U   ",
                        "   U  K UQVV                   VVQU K  U   ",
                        "  U  K  P                         P  K  U  ",
                        "  U F                                 F U  ",
                        "  U F                                 F U  ",
                        "  UKF                                 FKU  ",
                        " UKUKK                               KKUKU ",
                        " UKUR                                 RUKU ",
                        " UKUR                                 RUKU ",
                        " UKUR                                 RUKU ",
                        " UKUKK                               KKUKU ",
                        "  UKF                                 FKU  ",
                        "  U F                                 F U  ",
                        "  U F                                 F U  ",
                        "  U  K  P                         P  K  U  ",
                        "   U  K UQVV                   VVQU K  U   ",
                        "   U     K  VVQV           VQVV  K     U   ",
                        "    U         K VVQ     QVV K         U    ",
                        "     U            KVVVVVK            U     "
                    },{
                        "                 K KKKKK K                 ",
                        "              K  QVQQQQQVQ  K              ",
                        "          K  VQVV         VVQV  K          ",
                        "      K  UQVV                 VVQU  K      ",
                        "     K   P                       P   K     ",
                        "    F                                 F    ",
                        "    F                                 F    ",
                        "   KF                                 FK   ",
                        "   K                                   K   ",
                        " K K                                   K K ",
                        " K K                                   K K ",
                        " K K                                   K K ",
                        "   K                                   K   ",
                        "   KF                                 FK   ",
                        "    F                                 F    ",
                        "    F                                 F    ",
                        "     K   P                       P   K     ",
                        "      K  UQVV                 VVQU  K      ",
                        "          K  VQVV         VVQV  K          ",
                        "              K  QVQQQQQVQ  K              ",
                        "                 K KKKKK K                 "
                    },{
                        "                K    K    K                ",
                        "               KQ VVVQVVV QK               ",
                        "          K  VVQVV       VVQVV  K          ",
                        "       K UQVV                 VVQU K       ",
                        "      K  P                       P  K      ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "                                           ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "                                           ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "      K  P                       P  K      ",
                        "       K UQVV                 VVQU K       ",
                        "          K  VVQVV       VVQVV  K          ",
                        "               KQ VVVQVVV QK               ",
                        "                K    K    K                "
                    },{
                        "                     K                     ",
                        "               KK   VQV   KK               ",
                        "           K  VQQVVV   VVVQQV  K           ",
                        "       K  UQVV               VVQU  K       ",
                        "      K   P                     P   K      ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "                                           ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "                                           ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "      K   P                     P   K      ",
                        "       K  UQVV               VVQU  K       ",
                        "           K  VQQVVV   VVVQQV  K           ",
                        "               KK   VQV   KK               ",
                        "                     K                     "
                    },{
                        "                     K                     ",
                        "              K  KK  Q  KK  K              ",
                        "           K  QVVQQVVVVVQQVVQ  K           ",
                        "       K  UQVV               VVQU  K       ",
                        "      K   P                     P   K      ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "                                           ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "  K                                     K  ",
                        "                                           ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "     F                               F     ",
                        "      K   P                     P   K      ",
                        "       K  UQVV               VVQU  K       ",
                        "           K  QVVQQVVVVVQQVVQ  K           ",
                        "              K  KK  Q  KK  K              ",
                        "                     K                     "
                    },{
                        "            UU               UU            ",
                        "             K     KKKKK     K             ",
                        "            KQ  VVVQQQQQVVV  QK            ",
                        "        KKKKQVVV           VVVQKKKK        ",
                        "       K TMM                   MMT K       ",
                        "      F                             F      ",
                        "      F                             F      ",
                        "      F                             F      ",
                        "                                           ",
                        "   K                                   K   ",
                        "   K                                   K   ",
                        "   K                                   K   ",
                        "                                           ",
                        "      F                             F      ",
                        "      F                             F      ",
                        "      F                             F      ",
                        "       K TMM                   MMT K       ",
                        "        KKKKQVVV           VVVQKKKK        ",
                        "            KQ  VVVQQQQQVVV  QK            ",
                        "             K     KKKKK     K             ",
                        "            UU               UU            "
                    },{
                        "            KU               UK            ",
                        "           UU        K        UU           ",
                        "            PK    VVVQVVV    KP            ",
                        "           KUQVVVV       VVVVQUK           ",
                        "       KKKKPP                 PPKKKK       ",
                        "      F TMMR                   RMMT F      ",
                        "      F   R                     R   F      ",
                        "      F    R                   R    F      ",
                        "        KKK                     KKK        ",
                        "   K                                   K   ",
                        "   K                                   K   ",
                        "   K                                   K   ",
                        "        KKK                     KKK        ",
                        "      F    R                   R    F      ",
                        "      F   R                     R   F      ",
                        "      F TMMR                   RMMT F      ",
                        "       KKKKPP                 PPKKKK       ",
                        "           KUQVVVV       VVVVQUK           ",
                        "            PK    VVVQVVV    KP            ",
                        "           UU        K        UU           ",
                        "            KU               UK            "
                    },{
                        "                                           ",
                        "           KU        K        UK           ",
                        "          UU  KK     Q     KK  UU          ",
                        "           PKKQQVVVVVVVVVVVQQKKP           ",
                        "          KPPM               MPPK          ",
                        "       KKKPPR                 RPPKKK       ",
                        "       KMMMR                   RMMMK       ",
                        "       KMMMPR                 RPMMMK       ",
                        "      UKPPPK                   KPPPKU      ",
                        "    K U PFF                     FFP U K    ",
                        "    K U PFF                     FFP U K    ",
                        "    K U PFF                     FFP U K    ",
                        "      UKPPPK                   KPPPKU      ",
                        "       KMMMPR                 RPMMMK       ",
                        "       KMMMR                   RMMMK       ",
                        "       KKKPPR                 RPPKKK       ",
                        "          KPPM               MPPK          ",
                        "           PKKQQVVVVVVVVVVVQQKKP           ",
                        "          UU  KK     Q     KK  UU          ",
                        "           KU        K        UK           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                     K                     ",
                        "          KU    KK   Q   KK    UK          ",
                        "         UU  KUUQQVVVVVVVQQUUK  UU         ",
                        "        U PKKMPP           PPMKKP U        ",
                        "         KPPM                 MPPK         ",
                        "        KPPMR                 RMPPK        ",
                        "        KKKM                   MKKK        ",
                        "       PQQQPK                 KPQQQP       ",
                        "    K  P   F                   F   P  K    ",
                        "    K  P   F                   F   P  K    ",
                        "    K  P   F                   F   P  K    ",
                        "       PQQQPK                 KPQQQP       ",
                        "        KKKM                   MKKK        ",
                        "        KPPMR                 RMPPK        ",
                        "         KPPM                 MPPK         ",
                        "        U PKKMPP           PPMKKP U        ",
                        "         UU  KUUQQVVVVVVVQQUUK  UU         ",
                        "          KU    KK   Q   KK    UK          ",
                        "                     K                     ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                  KKKKKKK                  ",
                        "        KKU  K  UUQQQQQQQUU  K  UKK        ",
                        "        UU  KT  PP       PP  TK  UU        ",
                        "       UPPKKM                 MKKPPU       ",
                        "      UPPPPM                   MPPPPU      ",
                        "    K  PKKKM                   MKKKP  K    ",
                        "       PQQQPK                 KPQQQP       ",
                        "     K P T F                   F T P K     ",
                        "     K P   F                   F   P K     ",
                        "     K P T F                   F T P K     ",
                        "       PQQQPK                 KPQQQP       ",
                        "    K  PKKKM                   MKKKP  K    ",
                        "      UPPPPM                   MPPPPU      ",
                        "       UPPKKM                 MKKPPU       ",
                        "        UU  KT  PP       PP  TK  UU        ",
                        "        KKU  K  UUQQQQQQQUU  K  UKK        ",
                        "                  KKKKKKK                  ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "         K   K    UUUUUUU    K   K         ",
                        "       KKUU K     PPPPPPP     K UUKK       ",
                        "       UUP KT                 TK PUU       ",
                        "     KUPPPKM                   MKPPPUK     ",
                        "    K PPKKKM                   MKKKPP K    ",
                        "     UPPQQQPK                 KPQQQPPU     ",
                        "     UKPT  P                   P  TPKU     ",
                        "     UKST  P                   P  TSKU     ",
                        "     UKPT  P                   P  TPKU     ",
                        "     UPPQQQPK                 KPQQQPPU     ",
                        "    K PPKKKM                   MKKKPP K    ",
                        "     KUPPPKM                   MKPPPUK     ",
                        "       UUP KT                 TK PUU       ",
                        "       KKUU K     PPPPPPP     K UUKK       ",
                        "         K   K    UUUUUUU    K   K         ",
                        "                                           ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "              KKK         KKK              ",
                        "        K   KK               KK   K        ",
                        "      KKUU K                   K UUKK      ",
                        "     KUUPP K                   K PPUUK     ",
                        "    K PPPP K                   K PPPP K    ",
                        "      PPPPPK        KKK        KPPPPP      ",
                        "      UKPPP                     PPPKU      ",
                        "     U OSPP                     PPSO U     ",
                        "      UKPPP                     PPPKU      ",
                        "      PPPPPK        KKK        KPPPPP      ",
                        "    K PPPP K                   K PPPP K    ",
                        "     KUUPP K                   K PPUUK     ",
                        "      KKUU K                   K UUKK      ",
                        "        K   KK               KK   K        ",
                        "              KKK         KKK              ",
                        "                                           ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                 KKKMMMKKK                 ",
                        "              KKK   MMM   KKK              ",
                        "       K    FF      MMM      FF    K       ",
                        "      KUUU  FF      MMM      FF  UUUK      ",
                        "     K PP   FF      MMM      FF   PP K     ",
                        "       PP  U       KPPPK       U  PP       ",
                        "       UK  U        FFF        U  KU       ",
                        "      T K  U        FFF        U  K T      ",
                        "       UK  U        FFF        U  KU       ",
                        "       PP  U       KPPPK       U  PP       ",
                        "     K PP   FF      MMM      FF   PP K     ",
                        "      KUUU  FF      MMM      FF  UUUK      ",
                        "       K    FF      MMM      FF    K       ",
                        "              KKK   MMM   KKK              ",
                        "                 KKKMMMKKK                 ",
                        "                                           ",
                        "                                           ",
                        "                                           "
                    },{
                        "                  U     U                  ",
                        "                                           ",
                        "                                           ",
                        "                   MKKKM                   ",
                        "                 KKM   MKK                 ",
                        "              FFF  M   M  FFF              ",
                        "       KK     FFF  M   M  FFF     KK       ",
                        "     KK       FFF  MKKKM  FFF       KK     ",
                        "        U         KPQQQPK         U        ",
                        "        UK         F   F         KU        ",
                        "       UUK         F   F         KUU       ",
                        "        UK         F   F         KU        ",
                        "        U         KPQQQPK         U        ",
                        "     KK       FFF  MKKKM  FFF       KK     ",
                        "       KK     FFF  M   M  FFF     KK       ",
                        "              FFF  M   M  FFF              ",
                        "                 KKM   MKK                 ",
                        "                   MKKKM                   ",
                        "                                           ",
                        "                                           ",
                        "                  U     U                  "
                    },{
                        "                   M   M                   ",
                        "                  U     U                  ",
                        "                                           ",
                        "                   KKKKK                   ",
                        "                   K   K                   ",
                        "                 FFK   KFF                 ",
                        "                 FFK   KFF                 ",
                        "       KKK       FFKKKKKFF       KKK       ",
                        "                  KPQQQPK                  ",
                        "          KK      RP T PR      KK          ",
                        "          KK      RP   PR      KK          ",
                        "          KK      RP T PR      KK          ",
                        "                  KPQQQPK                  ",
                        "       KKK       FFKKKKKFF       KKK       ",
                        "                 FFK   KFF                 ",
                        "                 FFK   KFF                 ",
                        "                   K   K                   ",
                        "                   KKKKK                   ",
                        "                                           ",
                        "                  U     U                  ",
                        "                   M   M                   "
                    },{
                        "                                           ",
                        "                   MTTTM                   ",
                        "                  U     U                  ",
                        "                  U KKK U                  ",
                        "                    FFF                    ",
                        "                    FFF                    ",
                        "                    FFF                    ",
                        "                 KK KKK KK                 ",
                        "                 KUPQQQPUK                 ",
                        "            KK   KUP T PUK   KK            ",
                        "            KK   KUP T PUK   KK            ",
                        "            KK   KUP T PUK   KK            ",
                        "                 KUPQQQPUK                 ",
                        "                 KK KKK KK                 ",
                        "                    FFF                    ",
                        "                    FFF                    ",
                        "                    FFF                    ",
                        "                  U KKK U                  ",
                        "                  U     U                  ",
                        "                   MTTTM                   ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                   M   M                   ",
                        "                   MTTTM                   ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                  KMPPPMK                  ",
                        "              KKK KMPPPMK KKK              ",
                        "              KKK KMPSPMK KKK              ",
                        "              KKK KMPPPMK KKK              ",
                        "                  KMPPPMK                  ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                  U     U                  ",
                        "                   MTTTM                   ",
                        "                   M   M                   ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                   M   M                   ",
                        "                   MTTTM                   ",
                        "                   M   M                   ",
                        "                   MTTTM                   ",
                        "                  UM   MU                  ",
                        "                 KUKKKKKUK                 ",
                        "                 KUKKOKKUK                 ",
                        "                 KUKKKKKUK                 ",
                        "                  UM   MU                  ",
                        "                   MTTTM                   ",
                        "                   M   M                   ",
                        "                   MTTTM                   ",
                        "                   M   M                   ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           "
                    },{
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                   M   M                   ",
                        "                   MM MM                   ",
                        "                   MMTMM                   ",
                        "                   MM MM                   ",
                        "                   M   M                   ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           ",
                        "                                           "
                    }})
                    // spotless:on
                .addElement(
                    'A',
                    buildHatchAdder(MTESLICE.class)
                        .atLeast(
                            InputBus,
                            OutputBus,
                            InputHatch,
                            OutputHatch,
                            Maintenance,
                            Energy,
                            ExoticEnergy,
                            InputData)
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
                        .casingIndex(Casings.AdvancedComputerCasing.textureId)
                        .hint(2)
                        .build())
                .addElement(
                    'J',
                    buildHatchAdder(MTESLICE.class)
                        .atLeast(
                            InputBus,
                            OutputBus,
                            InputHatch,
                            OutputHatch,
                            Maintenance,
                            Energy,
                            ExoticEnergy,
                            InputData)
                        .casingIndex(Casings.AdvancedIridiumPlatedMachineCasing.textureId)
                        .hint(1)
                        .buildAndChain(
                            onElementPass(
                                MTESLICE::onCasingAdded,
                                Casings.AdvancedIridiumPlatedMachineCasing.asElement())))
                .addElement('K', Casings.RadiantNaquadahAlloyCasing.asElement())
                .addElement('L', Casings.HighPowerCasing.asElement())
                .addElement('M', Casings.PrimaryExoFoundryCasing.asElement())
                .addElement('N', Casings.CuttingFactoryFrame.asElement())
                .addElement('O', Casings.DimensionalBridge.asElement())
                .addElement(
                    'P',
                    buildHatchAdder(MTESLICE.class)
                        .atLeast(
                            InputBus,
                            OutputBus,
                            InputHatch,
                            OutputHatch,
                            Maintenance,
                            Energy,
                            ExoticEnergy,
                            InputData)
                        .casingIndex(Casings.ExtremeDensitySpaceBendingCasing.textureId)
                        .hint(1)
                        .buildAndChain(
                            onElementPass(
                                MTESLICE::onCasingAdded,
                                Casings.ExtremeDensitySpaceBendingCasing.asElement())))
                .addElement('Q', Casings.ReinforcedSpatialStructureCasing.asElement())
                .addElement('R', Casings.NanochipFirewallProjectionCasing.asElement())
                .addElement('S', Casings.HollowCasing.asElement())
                .addElement('T', ofFrame(Materials.TranscendentMetal))
                .addElement('U', ofSheetMetal(Materials.Neutronium))
                .addElement('V', Casings.ForceFieldGlass.asElement())
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

    public ITexture getCasingTexture() {
        return Textures.BlockIcons.getCasingTextureForId(getIndex(mTier));
    }

    private int getIndex(int tier) {
        if (tier <= 1) return Casings.RadiantNaquadahAlloyCasing.textureId;
        if (tier == 2) return Casings.AdvancedIridiumPlatedMachineCasing.textureId;
        return Casings.ExtremeDensitySpaceBendingCasing.textureId;
    }

    @Override
    public byte getUpdateData() {
        return (byte) mTier;
    }

    @Override
    public void receiveClientEvent(byte aEventID, byte aValue) {
        super.receiveClientEvent(aEventID, aValue);
        if (aEventID == GregTechTileClientEvents.CHANGE_CUSTOM_DATA && ((aValue & 0x80) == 0 || aValue == -1)) {
            mTier = aValue;
        }
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Cutting Machine, Laser Engraver, S.L.I.C.E")
            .addInfo("Spatial / Laser Industrial Cutter & Engraver")
            .addBulkMachineInfo(8, 3F, 0.85F)
            .addSupportAny()
            .addUnlimitedTierSkips()
            .addInfo(EnumChatFormatting.WHITE + "Use circuit 15 for Cutting and 16 for Laser Engraver")
            .addSeparator()
            .addInfo("Tier 1: " + EnumChatFormatting.WHITE + "Laser carving")
            .addInfo("Etching and cutting with precise laser")
            .addInfo(
                "Put a " + EnumChatFormatting.GOLD
                    + "Carbon Nanite "
                    + EnumChatFormatting.GRAY
                    + "into the controller slot to unlock this tier")
            .addInfo("Overclocks limited to " + EnumChatFormatting.WHITE + "Energy Hatch Tier")
            .addInfo(
                "Requires " + EnumChatFormatting.BLUE
                    + "1000 Computation per Tick "
                    + EnumChatFormatting.GRAY
                    + "to operate by default")
            .addInfo("Laser source hatch provides benefits: ")
            .addInfo(
                "Parallels increased by " + EnumChatFormatting.GREEN
                    + "log4(laser source amperage) "
                    + EnumChatFormatting.GRAY
                    + "per voltage tier")
            .addInfo("(Cutting mode's parallels are cut down by half)")
            .addInfo("Speed increased by " + EnumChatFormatting.GREEN + "(laser source tier over ZPM) * 100%")
            .addSeparator()
            .addInfo("Tier 2: " + EnumChatFormatting.WHITE + "Nanite operating")
            .addInfo("Operating wafers with programmed nanites")
            .addInfo(
                "Put a " + EnumChatFormatting.GOLD
                    + "Transcendent Metal Nanite "
                    + EnumChatFormatting.GRAY
                    + "into the controller slot to unlock this tier")
            .addSeparator()
            .addInfo("Tier 3: " + EnumChatFormatting.WHITE + "Dimension splitting")
            .addInfo("Splitting space and lowering dimension")
            .addInfo(
                "Put a " + EnumChatFormatting.GOLD
                    + "Transdimensional Alignment Matrix "
                    + EnumChatFormatting.GRAY
                    + "into the controller slot to unlock this tier")
            .beginStructureBlock(12, 9, 12, true)
            .addController("Front center")
            .addEnergyHatch("1", "Any casing", 1)
            .addInputBus("1+", "Any casing", 1)
            .addInputHatch("0+", "Any casing", 1)
            .addMaintenanceHatch("1", "Any casing", 1)
            .addOutputBus("1+", "Any casing", 1)
            .addOutputHatch("0+", "Any casing", 1)
            .addStructureInfo("")
            .addStructureInfo(EnumChatFormatting.AQUA + "Tier 1 ")
            .addCasing("330-348", "Radiant Naquadah Alloy Casing", false)
            .addCasing("62", "Heat Resistant Trinium Plated Casing", false)
            .addCasing("51", "Advanced Computer Casing", false)
            .addCasing("48", "Nanochip Complex Glass", false)
            .addCasing("48", "Infinity Catalyst Frame Box", false)
            .addCasing("40", "Black Plutonium Frame Box", false)
            .addCasing("21", "Laser Resistant Plate", false)
            .addCasing("4", "Cosmic Neutronium Frame Box", false)
            .addCasing("1", "Laser Source Hatch (Hint 2)", false)
            .toolTipFinisher();
        return tt;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            private byte lastMode = -1;

            @NotNull
            @Override
            protected Stream<GTRecipe> findRecipeMatches(@Nullable RecipeMap<?> map) {
                byte mode = getModeFromCircuit(inputItems);

                if (mode == -1) {
                    lastMode = -1;
                    return Stream.empty();
                }
                if (!(mode == lastMode)) {
                    lastRecipe = null;
                    lastMode = mode;
                }
                currentmode = mode;
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

            // capped OC
            @NotNull
            @Override
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe)
                    .setMaxOverclocks(GTUtility.getTier(getAverageInputVoltage()) - GTUtility.getTier(recipe.mEUt));
            }
        }.setSpeedBonusSupplier(() -> (double) (1F / getCurrentSpeed()))
            .setEuModifier(getCurrentEUEfficiency())
            .setMaxParallelSupplier(this::getTrueParallel);

    }

    private int getCurrentParallelPerTier() {
        int CURRENT_PARALLEL_PER_TIER = BASE_PARALLEL_PER_TIER;
        if (mTier == 1) {
            CURRENT_PARALLEL_PER_TIER += GTUtility.log4ceil(laserAmps);
        }
        return CURRENT_PARALLEL_PER_TIER;
    }

    private float getCurrentSpeed() {
        float CURRENT_SPEED = BASE_SPEED;
        if (mTier == 1) {
            CURRENT_SPEED += (float) (Math.max(0, laserTier - 7));
        }
        return CURRENT_SPEED;
    }

    private float getCurrentEUEfficiency() {
        float CURRENT_EU_EFFICIENCY = BASE_EU_EFFICIENCY;
        return CURRENT_EU_EFFICIENCY;
    }

    @Override
    public int getMaxParallelRecipes() {
        int MaxParallelRecipes = getCurrentParallelPerTier() * GTUtility.getTier(this.getMaxInputVoltage());
        if (currentmode == MACHINEMODE_CUTTER) {
            MaxParallelRecipes /= 2;
        }
        return MaxParallelRecipes;
    }

    private void onCasingAdded() {
        casingAmount++;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        mTier = aNBT.getByte("multiTier");
        super.loadNBTData(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setByte("multiTier", mTier);
        super.saveNBTData(aNBT);
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

        if (GTUtility.areStacksEqual(aStack, Materials.Carbon.getNanite(1))) {
            mTier = 1;
            if (!checkPiece(tier1, OFFSET_X1, OFFSET_Y1, OFFSET_Z1, errors)) return;
            checkCasingMin(errors, casingAmount, 330);
        }

        else if (GTUtility.areStacksEqual(aStack, Materials.TranscendentMetal.getNanite(1))) {
            mTier = 2;
            if (!checkPiece(tier2, OFFSET_X2, OFFSET_Y2, OFFSET_Z2, errors)) return;
            checkCasingMin(errors, casingAmount, 200);
        }

        else if (GTUtility.areStacksEqual(aStack, ItemList.Transdimensional_Alignment_Matrix.get(1))) {
            mTier = 3;
            if (!checkPiece(tier3, OFFSET_X3, OFFSET_Y3, OFFSET_Z3, errors)) return;
            checkCasingMin(errors, casingAmount, 800);
        }

        getBaseMetaTileEntity().sendBlockEvent(GregTechTileClientEvents.CHANGE_CUSTOM_DATA, getUpdateData());
        if (mTier == 0) {
            errors.add(StructureErrorRegistry.UNKNOWN_TIER);
            return;
        }

        if (eInputData.isEmpty()) {
            errors.add(StructureErrorRegistry.MISSING_DATA_HATCH);
        }
        checkHasMaintenanceHatch(errors);
        checkHasInputBus(errors);
        checkHasOutputBus(errors);
    }

    @Nonnull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.laserEngraverRecipes, RecipeMaps.cutterRecipes);
    }

    private byte getModeFromCircuit(ItemStack[] t) {
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
    protected CheckRecipeResult checkProcessing_EM() {
        CheckRecipeResult result = super.checkProcessing_EM();
        if (!result.wasSuccessful()) {
            return result;
        }

        this.eRequiredData = REQUIRED_COMPUTATION_PER_TICK;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (eAvailableData < REQUIRED_COMPUTATION_PER_TICK) {
            stopMachine(SimpleShutDownReason.ofCritical("insufficient_computation"));
            return false;
        }

        return super.onRunningTick(aStack);
    }

    @Override
    public void stopMachine(@Nonnull ShutDownReason reason) {
        super.stopMachine(reason);
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
