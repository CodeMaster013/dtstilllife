package wexlabs.dtstilllife.trees.growthlogic.nodemappers;

import com.dtteam.dynamictrees.api.network.NodeInspector;
import com.dtteam.dynamictrees.tree.TreeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Attempts to determine the length of a continue branch in a single direction.
 * This is done by
 * 1. Assuming the point at which we are inspecting represents a branch length of at least 1
 * 2. Searching forward from the start point and counting the number of branch blocks traveling in the same direction, stopping when we encounter a different direction
 * 3. Performing the same search in reverse to count preceding blocks
 * This should account for the full length of the branch.
 * TODO: Find a min and max in the same plane. Using fromDir doesn't give an accurate length.
 */
public class RunLengthFinder implements NodeInspector {

    private final BlockPos start;
    private Direction runDirection;
    public int length = 1;
    private boolean forwardComplete = false;

    public RunLengthFinder(BlockPos start) {
        this.start = start;
    }

    @Override
    public boolean run(BlockState state, LevelAccessor level, BlockPos pos, Direction fromDir) {
        if (!forwardComplete && TreeHelper.isTreePart(state)) {
            if (pos.equals(start)) {
                runDirection = fromDir;
            }
            if (runDirection != null) {
                if (runDirection.equals(fromDir)) {
                    if (!pos.equals(start)) {
                        length++;
                    }
                } else {
                    forwardComplete = true;
                    runDirection = null;
                }
            }
        }
        return true;
    }


    @Override
    public boolean returnRun(BlockState state, LevelAccessor level, BlockPos pos, Direction fromDir) {

        return true;
    }
}
