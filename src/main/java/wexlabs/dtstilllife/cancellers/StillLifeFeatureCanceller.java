package wexlabs.dtstilllife.cancellers;
import com.dtteam.dynamictrees.api.worldgen.BiomePropertySelectors;
import com.dtteam.dynamictrees.api.worldgen.FeatureCanceller;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.antlr.v4.runtime.tree.Tree;
import org.apache.commons.lang3.StringUtils;
import wexlabs.Mod;

public class StillLifeFeatureCanceller<T extends FeatureConfiguration> extends FeatureCanceller {

    private record TreeSearchResult(boolean isOrContainsTrees, String namespace) {
    }

    private final Class<T> treeFeatureConfigClass;

    public StillLifeFeatureCanceller(final ResourceLocation registryName, Class<T> treeFeatureConfigClass) {
        super(registryName);
        this.treeFeatureConfigClass = treeFeatureConfigClass;
    }

    private TreeSearchResult containsTrees(FeatureConfiguration featureConfig, BiomePropertySelectors.NormalFeatureCancellation featureCancellations){
        if (featureConfig instanceof RandomFeatureConfiguration randomFeatureConfig) { //Handles random_selector
            return this.containsTrees(randomFeatureConfig, featureCancellations);
        } else  if (featureConfig instanceof RandomPatchConfiguration randomPatchConfig) { //handles random_patch
            return this.containsTrees(randomPatchConfig, featureCancellations);
        }
        return new TreeSearchResult(false, null);
    }

    @Override
    public boolean shouldCancel(ConfiguredFeature<?, ?> configuredFeature, BiomePropertySelectors.NormalFeatureCancellation featureCancellations) {
        TreeSearchResult result = isOrContainsTreeFeature(configuredFeature, featureCancellations);
        return result.isOrContainsTrees && shouldCancel(result.namespace, featureCancellations);
    }

    private TreeSearchResult isOrContainsTreeFeature(ConfiguredFeature<?, ?> configuredFeature, BiomePropertySelectors.NormalFeatureCancellation featureCancellations) {
        if (isTree(configuredFeature, "tree")) {
            final ConfiguredFeature<?, ?> nextConfiguredFeature = configuredFeature.getFeatures().findFirst().get();
            final ResourceLocation featureRegistryName = BuiltInRegistries.FEATURE.getKey(nextConfiguredFeature.feature());
            String nameSpace = null;
            if (featureRegistryName != null) {
                nameSpace = featureRegistryName.getNamespace();
            }
            return new TreeSearchResult(true, nameSpace);
        } else {
            return containsTrees(configuredFeature.config(), featureCancellations);
        }
    }

    private TreeSearchResult containsTrees(RandomPatchConfiguration randomPatchConfig, BiomePropertySelectors.NormalFeatureCancellation featureCancellations) {
        PlacedFeature placedFeature = randomPatchConfig.feature().value();

        if (isTree(placedFeature.feature().value())) {
            ResourceLocation featureResLoc = BuiltInRegistries.FEATURE.getKey(placedFeature.feature().value().feature());
            String nameSpace = null;
            if (featureResLoc != null) {
                nameSpace = featureResLoc.getNamespace();
            }
            return new TreeSearchResult(true, nameSpace);
        }
        return new TreeSearchResult(false, null);

    }

    private TreeSearchResult containsTrees(RandomFeatureConfiguration featureConfig, BiomePropertySelectors.NormalFeatureCancellation featureCancellations) {
        for (WeightedPlacedFeature feature : featureConfig.features) {
            final PlacedFeature currentPlacedFeature = feature.feature.value();
            final ConfiguredFeature<?,?> currentConfiguredFeature = currentPlacedFeature.feature().value();
            if (isTree(currentConfiguredFeature, feature.feature.getRegisteredName())) {
                final ResourceLocation featureRegistryName = BuiltInRegistries.FEATURE.getKey(currentConfiguredFeature.getFeatures().findFirst().get().feature());
                String nameSpace = null;
                if (featureRegistryName != null) {
                    nameSpace = featureRegistryName.getNamespace();
                }

                return new TreeSearchResult(true, nameSpace);
            }
            return containsTrees(currentConfiguredFeature.config(), featureCancellations);
        }
        return new TreeSearchResult(false, null);
    }

    private boolean isTree(ConfiguredFeature<?, ?> featureConfig, String featureName) {
        //name check may help narrow the cancellations due to still life's usage of /tree/ in the naming convention.
        return isTree(featureConfig) && (StringUtils.isNotEmpty(featureName) && featureName.contains("tree"));
    }

    private boolean isTree(ConfiguredFeature<?, ?> featureConfig) {
        return this.treeFeatureConfigClass.isInstance(featureConfig.config());
    }

    private boolean shouldCancel(String namespace, BiomePropertySelectors.NormalFeatureCancellation featureCancellations) {
        return namespace != null && featureCancellations.shouldCancelNamespace(namespace);
    }


}