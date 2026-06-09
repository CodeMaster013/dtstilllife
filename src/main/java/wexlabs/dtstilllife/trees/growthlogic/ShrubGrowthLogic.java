package wexlabs.dtstilllife.trees.growthlogic;

import com.dtteam.dynamictrees.api.configuration.ConfigurationProperty;
import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKit;
import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKitConfiguration;
import com.dtteam.dynamictrees.systems.growthlogic.context.DirectionManipulationContext;
import com.dtteam.dynamictrees.utility.CoordUtils;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ShrubGrowthLogic extends GrowthLogicKit {
    public static final ConfigurationProperty<Integer> MAX_TRUNK_HEIGHT = ConfigurationProperty.integer("max_trunk_height");
    public ShrubGrowthLogic(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected GrowthLogicKitConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration()
                .with(MAX_TRUNK_HEIGHT, 1);
    }

    @Override
    protected void registerProperties() {
        this.register(MAX_TRUNK_HEIGHT);
    }

    /**
     * Trunk fixed to one block in height, branches spread outward from there
     */
    @Override
    public int[] populateDirectionProbabilityMap(GrowthLogicKitConfiguration configuration, DirectionManipulationContext context) {
        final int[] probMap = super.populateDirectionProbabilityMap(configuration, context);
        Direction originDir = context.signal().dir.getOpposite();

        //TODO: Shrubs die if leaves ever disconnect, and possibly if branches grow into the trunk?
        if (context.signal().isInTrunk() && context.signal().delta.getY() >= configuration.get(MAX_TRUNK_HEIGHT)) {
            probMap[Direction.UP.get3DDataValue()] = 0;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                probMap[dir.get3DDataValue()] = 10;
            }
        }
        return probMap;
    }
}
