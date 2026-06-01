package wexlabs.dtstilllife.trees.growthlogic;

import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKit;
import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKitConfiguration;
import com.dtteam.dynamictrees.systems.growthlogic.context.DirectionManipulationContext;
import com.dtteam.dynamictrees.utility.CoordUtils;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class LongBranchCanopyGrowthLogic extends GrowthLogicKit {
    public LongBranchCanopyGrowthLogic(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    public int[] populateDirectionProbabilityMap(GrowthLogicKitConfiguration configuration, DirectionManipulationContext context) {
        final int[] probMap = super.populateDirectionProbabilityMap(configuration, context);
        probMap[Direction.UP.get3DDataValue()] = 4;

        //Branches don't go down
        if (!context.signal().isInTrunk()) {
            probMap[Direction.DOWN.get3DDataValue()] = 0;
        }

        //Amplify cardinal directions to encourage spread the higher we get
        float energyRatio = context.signal().delta.getY() / context.species().getEnergy(context.level(), context.pos());
        float spreadPush = energyRatio * 2;
        spreadPush = Math.max(spreadPush, 1.0f);
        for (Direction dir : CoordUtils.HORIZONTALS) {
            probMap[dir.ordinal()] *= spreadPush;
        }
        //Prefer longer straight runs
        if (context.signal().numTurns == 1 && context.signal().delta.distToCenterSqr(0, context.signal().delta.getY(), 0) <= 2.0) {
            for (Direction dir : CoordUtils.HORIZONTALS) {
                if (context.signal().dir != dir) {
                    probMap[dir.ordinal()] = 0;
                }
            }
        }

        return probMap;
    }
}
