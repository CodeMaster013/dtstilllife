package wexlabs;

import com.dtteam.dynamictrees.api.cell.CellKit;
import com.dtteam.dynamictrees.api.worldgen.FeatureCanceller;
import com.dtteam.dynamictrees.event.RegistryEvent;
import com.dtteam.dynamictrees.event.TypeRegistryEvent;
import com.dtteam.dynamictrees.systems.genfeature.GenFeature;
import com.dtteam.dynamictrees.systems.growthlogic.GrowthLogicKit;
import com.dtteam.dynamictrees.systems.growthlogic.PalmGrowthLogic;
import com.dtteam.dynamictrees.tree.family.Family;
import com.dtteam.dynamictrees.tree.species.Species;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import wexlabs.dtstilllife.cancellers.StillLifeFeatureCanceller;
import wexlabs.dtstilllife.trees.cells.CellKits;
import wexlabs.dtstilllife.trees.features.ExtraBottomFlareGenFeature;
import wexlabs.dtstilllife.trees.features.ReplaceOnRadiusGenFeature;
import wexlabs.dtstilllife.trees.growthlogic.*;
import wexlabs.dtstilllife.trees.species.DiagonalPalmFamily;
import wexlabs.dtstilllife.trees.species.GenUnderwaterSpecies;

@EventBusSubscriber(modid = Mod.MOD_ID)
public class Registrator {
    public static final FeatureCanceller CANCELLER = new StillLifeFeatureCanceller<>( ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "tree"), TreeConfiguration.class);

    public static final GenFeature REPLACE_ON_RADIUS = new ReplaceOnRadiusGenFeature(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "replace_on_radius"));
    public static final GenFeature EXTRA_FLARE = new ExtraBottomFlareGenFeature(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "extra_bottom_flare"));

    public static final GrowthLogicKit LONG_BRANCH_CANOPY_GROWTH_LOGIC = new LongBranchCanopyGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "long_branch_canopy"));
    public static final GrowthLogicKit UP_AND_OUT_GROWTH_LOGIC = new UpAndOutGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "up_and_out"));
    public static final GrowthLogicKit BAOBAB_GROWTH_LOGIC = new BaobabGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "baobab"));
    public static final GrowthLogicKit CYPRESS_GROWTH_LOGIC = new CypressGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "cypress"));
    public static final GrowthLogicKit REDWOOD_GROWTH_LOGIC = new RedwoodGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "redwood"));
    public static final GrowthLogicKit SMALL_REDWOOD_GROWTH_LOGIC = new SmallRedwoodGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "small_redwood"));
    public static final GrowthLogicKit WILLOW_GROWTH_LOGIC = new WillowGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "willow"));
    public static final GrowthLogicKit CANOPY_GROWTH_LOGIC = new CanopyGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "canopy"));
    public static final GrowthLogicKit JUNIPER_GROWTH_LOGIC = new JuniperGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "juniper"));
    public static final GrowthLogicKit VARIATE_HEIGHT_GROWTH_LOGIC = new VariateHeightGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "variate_height"));
    public static final GrowthLogicKit PALM_GROWTH_LOGIC = new DiagonalPalmGrowthLogic(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "palm"));


    @SubscribeEvent
    public static void registerFeatureCanceller (final RegistryEvent<FeatureCanceller> event) {
        if (event.isEntryOfType(FeatureCanceller.class)) {
            event.getRegistry().registerAll(CANCELLER);
        }
    }

    @SubscribeEvent
    public static void onGenFeatureRegistry(final RegistryEvent<GenFeature> event) {
        if (event.isEntryOfType(GenFeature.class)) {
            event.getRegistry().registerAll(REPLACE_ON_RADIUS, EXTRA_FLARE);
        }

    }


    @SubscribeEvent
    public static void onGrowthLogicKitRegistry(final RegistryEvent<GrowthLogicKit> event) {
        if (event.isEntryOfType(GrowthLogicKit.class)){
           event.getRegistry().registerAll(PALM_GROWTH_LOGIC, VARIATE_HEIGHT_GROWTH_LOGIC, JUNIPER_GROWTH_LOGIC, LONG_BRANCH_CANOPY_GROWTH_LOGIC, UP_AND_OUT_GROWTH_LOGIC, BAOBAB_GROWTH_LOGIC, CYPRESS_GROWTH_LOGIC, REDWOOD_GROWTH_LOGIC, SMALL_REDWOOD_GROWTH_LOGIC, WILLOW_GROWTH_LOGIC, CANOPY_GROWTH_LOGIC);
        }

    }

    @SubscribeEvent
    public static void registerSpeciesTypes(final TypeRegistryEvent<Species> event) {
        event.registerType(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "cypress"), GenUnderwaterSpecies.TYPE);
    }

    @SubscribeEvent
    public static void registerFamilyTypes(final TypeRegistryEvent<Family> event) {
        event.registerType(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "palm"), DiagonalPalmFamily.TYPE);
    }

    @SubscribeEvent
    public static void onCellKitRegistry(final RegistryEvent<CellKit> event) {
        if (event.isEntryOfType(CellKit.class)){
            event.getRegistry().registerAll(CellKits.PALM, CellKits.POPLAR, CellKits.DOME, CellKits.JOSHUA, CellKits.WILLOW);
        }
    }
}
