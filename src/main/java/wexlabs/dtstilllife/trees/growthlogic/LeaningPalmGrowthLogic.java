package wexlabs.dtstilllife.trees.growthlogic;

import com.dtteam.dynamictrees.api.configuration.ConfigurationProperty;
import com.dtteam.dynamictrees.api.network.MapSignal;
import com.dtteam.dynamictrees.systems.GrowSignal;
import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKitConfiguration;
import com.dtteam.dynamictrees.systems.growthlogic.PalmGrowthLogic;
import com.dtteam.dynamictrees.systems.growthlogic.context.DirectionManipulationContext;
import com.dtteam.dynamictrees.tree.species.Species;
import com.dtteam.dynamictrees.utility.CoordUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import wexlabs.dtstilllife.trees.growthlogic.nodemappers.RunLengthFinder;


public class LeaningPalmGrowthLogic extends PalmGrowthLogic {

    public static final ConfigurationProperty<Float> CHANCE_TO_SPLIT = ConfigurationProperty.floatProperty("chance_to_split");
    public static final ConfigurationProperty<Float> SPLIT_MAX_ENERGY_FACTOR = ConfigurationProperty.floatProperty("split_max_energy_factor");
    //Reduce height step by 1 every X blocks up
    public static final ConfigurationProperty<Integer> LEAN_INCREMENT_STEP = ConfigurationProperty.integer("lean_increment_step");
    //Every X steps up, step over 1
    public static final ConfigurationProperty<Integer> INITIAL_HEIGHT_STEP = ConfigurationProperty.integer("initial_height_step");
    //% chance to vary the lean +- 1 block
    public static final ConfigurationProperty<Float> LEAN_VARIABILITY = ConfigurationProperty.floatProperty("lean_variability");
    //When varying, the most variation a tree can have - more or less possible lean
    public static final ConfigurationProperty<Integer> MAX_VARIATION = ConfigurationProperty.integer("max_lean_variation");

    public LeaningPalmGrowthLogic(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected GrowthLogicKitConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration()
                .with(CHANCE_TO_SPLIT, 0.06f)
                .with(SPLIT_MAX_ENERGY_FACTOR, 0.5f)//can only split under the bottom half
                .with(INITIAL_HEIGHT_STEP, 3)
                .with(LEAN_INCREMENT_STEP, 0)
                .with(LEAN_VARIABILITY, 0.5f)
                .with(MAX_VARIATION, 1);
    }

    @Override
    protected void registerProperties() {
        this.register(CHANCE_TO_SPLIT, SPLIT_MAX_ENERGY_FACTOR, LEAN_INCREMENT_STEP, INITIAL_HEIGHT_STEP, LEAN_VARIABILITY, MAX_VARIATION);
    }

    private boolean isPositionOffshoot(BlockPos position, BlockPos rootPosition, Direction primaryLeanDirection) {
        BlockPos delta = rootPosition.subtract(position);
        return (primaryLeanDirection.getNormal().getX() != 0 && delta.getX() != 0 && (delta.getX() ^ primaryLeanDirection.getOpposite().getNormal().getX()) < 0)
                 || (primaryLeanDirection.getNormal().getZ() != 0 && delta.getZ() != 0 && (delta.getZ() ^ primaryLeanDirection.getOpposite().getNormal().getZ()) < 0);
    }

    @Override
    public int[] populateDirectionProbabilityMap(GrowthLogicKitConfiguration configuration, DirectionManipulationContext context) {
        final Species species = context.species();
        final Level world = context.level();
        final GrowSignal signal = context.signal();
        final int[] probMap = context.probMap();
        final BlockPos pos = context.pos();
        Direction originDir = signal.dir.getOpposite();
        RunLengthFinder runLengthFinder = new RunLengthFinder(context.pos());
        MapSignal mapSignal = new MapSignal(runLengthFinder);


        context.branch().analyse(context.level().getBlockState(context.pos()), context.level(), context.pos(), context.signal().dir, mapSignal);
        int currentRunLength = runLengthFinder.length;

        // Alter probability map for direction change
        probMap[0] = 0; // Down is always disallowed for palm
        probMap[1] = species.getUpProbability();
        // Start by disabling probability on the sides
        probMap[2] = probMap[3] = probMap[4] = probMap[5] =  0;

        int stepCoordSeed = Math.abs(CoordUtils.coordHashCode(pos, 2));
        int variationSeed = Math.abs(CoordUtils.coordHashCode(signal.rootPos, 1));
        int treeCoordSeed = Math.abs(CoordUtils.coordHashCode(signal.rootPos, 2));

        int splitChance = (int)(1/configuration.get(CHANCE_TO_SPLIT));
        int variationChance = (int)(1/configuration.get(LEAN_VARIABILITY));

        int primaryDirection = treeCoordSeed % 4;
        int secondaryDirection = (treeCoordSeed + variationSeed) % 4;
        boolean shouldSplit = signal.isInTrunk() && stepCoordSeed % splitChance == 0;
        boolean shouldLeanVary = variationSeed % variationChance == 0;
        int leanVariance = 0;
        if (shouldLeanVary) {
            leanVariance = variationSeed % configuration.get(MAX_VARIATION);
        }

        //Lean more the higher we go
        int stepHeight = configuration.get(INITIAL_HEIGHT_STEP) + leanVariance;

        if (configuration.get(LEAN_INCREMENT_STEP) > 0) {
            stepHeight = Math.max(1, stepHeight - (signal.delta.getY() / configuration.get(LEAN_INCREMENT_STEP)));
        }

        //Primary lean determines the orthogonal lean direction, while secondary allows one additional degree of freedom to lean on a diagonal
        //To ensure we avoid oddities like bending backwards or zigzagging from side to side, we limit this to one static direction per tree
        //When secondary lean == primary lean, we get a tree that only leans orthogonally
        if (signal.energy > 1){
            if (originDir == Direction.DOWN && currentRunLength >= stepHeight){
                Direction primaryLean = Direction.values()[2 + primaryDirection];
                Direction secondaryLean = Direction.values()[2 + secondaryDirection];

                if (secondaryLean.equals(primaryLean.getOpposite())) {
                    secondaryLean = primaryLean;
                }

                if (isPositionOffshoot(pos, signal.rootPos, Direction.values()[2 + primaryDirection])) {
                    if (stepCoordSeed % 2 == 0) {
                        probMap[primaryLean.getOpposite().ordinal()] = 10;
                    } else {
                        probMap[secondaryLean.getOpposite().ordinal()] = 10;
                    }


                } else {
                    //Important to only pick one, as the probability is permanent - essentially causes a split
                    if (stepCoordSeed % 2 == 0) {
                        probMap[primaryLean.ordinal()] = 10;
                    } else {
                        probMap[secondaryLean.ordinal()] = 10;
                    }
                    //if the chance to split is met, the clockwise direction is also enabled
                    if (shouldSplit && signal.energy > species.getEnergy(world, signal.rootPos) * Math.max(0, Math.min(1, 1 - configuration.get(SPLIT_MAX_ENERGY_FACTOR)))){
                        probMap[primaryLean.getClockWise().ordinal()] = 10;
                    }
                }
                probMap[1] = 0;
            }
        }

        probMap[originDir.ordinal()] = 0; // Disable the direction we came from

        return probMap;
    }

}
