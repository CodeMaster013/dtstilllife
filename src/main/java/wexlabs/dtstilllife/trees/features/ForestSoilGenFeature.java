package wexlabs.dtstilllife.trees.features;

import com.dtteam.dynamictrees.api.configuration.ConfigurationProperty;
import com.dtteam.dynamictrees.block.soil.SoilHelper;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import com.dtteam.dynamictrees.systems.genfeature.GenFeatureConfiguration;
import com.dtteam.dynamictrees.systems.genfeature.context.FullGenerationContext;
import com.dtteam.dynamictrees.tree.species.Species;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;


/**
 * Basically yoinked from DT addon lib. Though I hate to do so since it is literally a library, people really love
 * their private access modifiers on API methods, meaning I can't just extend it to provide the extra capabilities I need.
 */
public class ForestSoilGenFeature extends GenFeature  {

    //Although this is intended to be used with specific blocks, since there is no list ConfigurationProperty, we will
    //Expose these blocks to config just in case.
    public static final ConfigurationProperty<Block> COARSE_DIRT_BLOCK = ConfigurationProperty.block("coarse_dirt_block");
    public static final ConfigurationProperty<Block> PODZOL_BLOCK = ConfigurationProperty.block("podzol_block");
    public static final ConfigurationProperty<Block> ROOTED_DIRT_BLOCK = ConfigurationProperty.block("rooted_dirt_block");
    public static final ConfigurationProperty<String> REPLACEABLE_SOILS = ConfigurationProperty.string("replaceable_soils");
    public static final ConfigurationProperty<Float> GEN_CHANCE = ConfigurationProperty.floatProperty("generate_chance");
    public static final ConfigurationProperty<Integer> MAX_LIGHT_LEVEL = ConfigurationProperty.integer("max_light_level");

    public ForestSoilGenFeature(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    protected void registerProperties() {
        register(BIOME_PREDICATE, MAX_HEIGHT, GEN_CHANCE, PODZOL_BLOCK, ROOTED_DIRT_BLOCK, COARSE_DIRT_BLOCK, REPLACEABLE_SOILS, MAX_LIGHT_LEVEL);
    }

    @Override
    protected GenFeatureConfiguration createDefaultConfiguration() {
        return super.createDefaultConfiguration()
                .with(BIOME_PREDICATE, biome -> true)
                .with(COARSE_DIRT_BLOCK, Blocks.COARSE_DIRT)
                .with(ROOTED_DIRT_BLOCK, Blocks.ROOTED_DIRT)
                .with(PODZOL_BLOCK, Blocks.PODZOL)
                .with(REPLACEABLE_SOILS, SoilHelper.DIRT_LIKE)
                .with(GEN_CHANCE, 1.0f)
                .with(MAX_HEIGHT, 6)
                .with(MAX_LIGHT_LEVEL, 16);
    }

    @Override
    public boolean shouldApply(Species species, GenFeatureConfiguration configuration) {
        return  configuration.get(COARSE_DIRT_BLOCK) != Blocks.AIR
                || configuration.get(ROOTED_DIRT_BLOCK) != Blocks.AIR
                || configuration.get(PODZOL_BLOCK) != Blocks.AIR;
    }

    /**
     * Determine the next block type that should be used during generation.
     * Prefer to stay with the last type used, otherwise favor podzol, coarse dirt, and rooted dirt in that order.
     */
    protected Block getNextBlock(GenFeatureConfiguration configuration, RandomSource random, Block lastBlock){
        float chance = random.nextFloat();
        if (lastBlock != null && random.nextFloat() < 0.75) {
            return lastBlock;
        } else if (chance > 0.8) {
            return configuration.get(ROOTED_DIRT_BLOCK);
        } else if (chance > 0.55) {
            return configuration.get(COARSE_DIRT_BLOCK);
        } else if (chance > 0.05) {
            return configuration.get(PODZOL_BLOCK);
        } else {
            //used to skip some replacements
            return Blocks.AIR;
        }
    }

    @Override
    protected boolean generate(GenFeatureConfiguration configuration, FullGenerationContext context) {
        final RandomSource random = context.random();
        if (!configuration.get(BIOME_PREDICATE).test(context.biome())) {
            return false;
        }
        if (random.nextFloat() >= configuration.get(GEN_CHANCE)) {
            return false;
        }
        int rad = Math.max(context.radius(), 4);
        int startH = context.species().getLowestBranchHeight() + 3;
        int layers = startH + configuration.get(MAX_HEIGHT);
        BlockPos.MutableBlockPos placePos = context.pos().above(startH).mutable();
        LevelAccessor level = context.level();
        Block placedBlock = getNextBlock(configuration, random, null);
        String soilTag = configuration.get(REPLACEABLE_SOILS);

        for (int i=0; i<layers; i++){
            this.tryPlaceCircle(configuration, level, placePos.west().north(), placedBlock.defaultBlockState(), soilTag);
            placedBlock = getNextBlock(configuration, random, placedBlock);
            this.tryPlaceCircle(configuration, level, placePos.east(2).north(), placedBlock.defaultBlockState(), soilTag);
            placedBlock = getNextBlock(configuration, random, placedBlock);
            this.tryPlaceCircle(configuration, level, placePos.west().south(2), placedBlock.defaultBlockState(), soilTag);
            placedBlock = getNextBlock(configuration, random, placedBlock);
            this.tryPlaceCircle(configuration, level, placePos.east(2).south(2), placedBlock.defaultBlockState(), soilTag);
            //Magic numbers :v)
            for(int j = 0; j < 5; ++j) {
                int k = context.random().nextInt(48);
                int l = k % rad;
                int i1 = k / rad;
                if (l == 0 || l == 7 || i1 == 0 || i1 == 7) {
                    placedBlock = getNextBlock(configuration, random, placedBlock);
                    this.tryPlaceCircle(configuration, level, placePos.offset(-3 + l, 0, -3 + i1), placedBlock.defaultBlockState(), soilTag);
                }
            }
            placePos.move(0,-1,0);
        }
        return false;
    }

    /**
     * Extra context checks for each circle generation
     */
    protected void tryPlaceCircle(GenFeatureConfiguration configuration, LevelAccessor level, BlockPos pos, BlockState placeState, String soilTag){
        if (level.getBrightness(LightLayer.SKY, pos.below()) > configuration.get(MAX_LIGHT_LEVEL)) {
            return;
        }
        if (placeState.getBlock() == Blocks.AIR) {
            return;
        }
        placeCircle(level, pos, placeState, soilTag);
    }

    protected void placeCircle(LevelAccessor level, BlockPos pos, BlockState placeState, String soilTag) {

        for(int i = -2; i <= 2; ++i) {
            for(int j = -2; j <= 2; ++j) {
                if (Math.abs(i) != 2 || Math.abs(j) != 2) {
                    this.placeBlockAt(level, pos.offset(i, 0, j), placeState, soilTag);
                }
            }
        }

    }

    public boolean isAcceptableSoil(LevelSimulatedReader level, BlockPos pos, String soilTag) {
        return level.isStateAtPosition(pos, (s)->SoilHelper.isSoilAcceptable(s, SoilHelper.getSoilFlags(soilTag)));
    }

    protected void placeBlockAt(LevelAccessor level, BlockPos pos, BlockState placeState, String soilTag) {
        for(int i = 2; i >= -3; --i) {
            BlockPos blockpos = pos.above(i);
            if (this.isAcceptableSoil(level, blockpos, soilTag) && !level.getBlockState(blockpos.above()).isCollisionShapeFullBlock(level, blockpos.above())) {
                level.setBlock(blockpos, placeState, 2);
                break;
            }

            if (!level.isEmptyBlock(blockpos) && i < 0) {
                break;
            }
        }

    }

}
