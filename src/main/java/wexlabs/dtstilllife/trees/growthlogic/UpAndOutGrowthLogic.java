package wexlabs.dtstilllife.trees.growthlogic;

import com.dtteam.dynamictrees.api.configuration.ConfigurationProperty;
import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKit;
import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKitConfiguration;
import com.dtteam.dynamictrees.systems.growthlogic.context.DirectionManipulationContext;
import com.dtteam.dynamictrees.utility.CoordUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class UpAndOutGrowthLogic extends GrowthLogicKit {
    public static final ConfigurationProperty<Integer> MAX_OUTWARD_HEIGHT = ConfigurationProperty.integer("max_outward_height");
    public UpAndOutGrowthLogic(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected GrowthLogicKitConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration()
                .with(MAX_OUTWARD_HEIGHT, 10)
                .with(HEIGHT_VARIATION, 3);
    }

    @Override
    protected void registerProperties() {
        this.register(MAX_OUTWARD_HEIGHT, HEIGHT_VARIATION);
    }

    /**
     * Prefer to move upward and outward, with outward being favored at lower branches while
     * upward is favored at higher branches
     */
    @Override
    public int[] populateDirectionProbabilityMap(GrowthLogicKitConfiguration configuration, DirectionManipulationContext context) {
        final int[] probMap = super.populateDirectionProbabilityMap(configuration, context);
        Direction originDir = context.signal().dir.getOpposite();
        int deltaFromLowestBranch = context.signal().delta.getY() - context.signal().getSpecies().getLowestBranchHeight();
        int outwardHeightRange = configuration.get(MAX_OUTWARD_HEIGHT) - context.signal().getSpecies().getLowestBranchHeight();
        if (!context.signal().isInTrunk()) {
            //Linearly expand outwards based on configured heights
            int outProb = Mth.lerpInt(((float) deltaFromLowestBranch /outwardHeightRange), outwardHeightRange, 0);
            int upProb = Mth.lerpInt(((float) deltaFromLowestBranch /outwardHeightRange), 0, outwardHeightRange);
            probMap[Direction.DOWN.get3DDataValue()] = 0;
            probMap[Direction.UP.get3DDataValue()] = upProb;
            for (Direction dir : CoordUtils.HORIZONTALS) {
                if (probMap[dir.ordinal()] > 0) {
                    probMap[dir.ordinal()] = outProb;
                }
            }
        }
        return probMap;
    }
}
