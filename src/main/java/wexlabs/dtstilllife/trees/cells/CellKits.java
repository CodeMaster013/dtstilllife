package wexlabs.dtstilllife.trees.cells;

import com.dtteam.dynamictrees.api.cell.Cell;
import com.dtteam.dynamictrees.api.cell.CellKit;
import com.dtteam.dynamictrees.api.cell.CellNull;
import com.dtteam.dynamictrees.api.cell.CellSolver;
import com.dtteam.dynamictrees.api.voxmap.SimpleVoxmap;
import com.dtteam.dynamictrees.systems.cell.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import wexlabs.Mod;

public class CellKits {

    public static final CellKit SAGEBRUSH = new CellKit(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "sagebrush")) {
        private final Cell branchCell = new NormalCell(5);

        private final Cell[] leafCells = {
                CellNull.NULL_CELL,
                new NormalCell(1),
                new NormalCell(2),
                new NormalCell(3),
                new NormalCell(4),
                new NormalCell(5),
                new NormalCell(6),
                new NormalCell(7)
        };

        private final com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver solver = new com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver(new short[]{0x0514, 0x0423, 0x0412, 0x0312, 0x0211});

        @Override
        public Cell getCellForLeaves(int distance) {
            return this.leafCells[distance];
        }

        @Override
        public Cell getCellForBranch(int radius, int meta) {
            return radius == 1 ? this.branchCell : CellNull.NULL_CELL;
        }

        @Override
        public CellSolver getCellSolver() {
            return this.solver;
        }

        @Override
        public SimpleVoxmap getLeafCluster() {
            return LeafClusters.SAGEBRUSH;
        }

        @Override
        public int getDefaultHydration() {
            return 2;
        }
    };

    public static final CellKit PALM = new CellKit(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "palm")) {

        private final Cell palmBranch = new Cell() {
            @Override
            public int getValue() {
                return 5;
            }

            @Override
            public int getValueFromSide(Direction side) {
                return side == Direction.UP ? getValue() : 0;
            }

        };

        private final Cell[] palmFrondCells = {
                CellNull.NULL_CELL,
                new PalmFrondCell(1),
                new PalmFrondCell(2),
                new PalmFrondCell(3),
                new PalmFrondCell(4),
                new PalmFrondCell(5),
                new PalmFrondCell(6),
                new PalmFrondCell(7)
        };

        private final com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver palmSolver = new com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver(new short[]{0x0514, 0x0413, 0x0312, 0x0221});

        @Override
        public Cell getCellForLeaves(int hydro) {
            return palmFrondCells[hydro];
        }

        @Override
        public Cell getCellForBranch(int radius, int meta) {
            return radius == 3? palmBranch : CellNull.NULL_CELL;
        }

        @Override
        public SimpleVoxmap getLeafCluster() {
            return com.dtteam.dynamictrees.systems.cell.LeafClusters.PALM;
        }

        @Override
        public CellSolver getCellSolver() {
            return palmSolver;
        }

        @Override
        public int getDefaultHydration() {
            return 4;
        }

    };

    public static final CellKit POPLAR = new CellKit(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "poplar")) {

        private final Cell poplarBranch = new PoplarBranchCell();
        private final Cell poplarTopBranch = new PoplarTopBranchCell();
        private final Cell poplarUpperTrunk = new NormalCell(4);

        private final Cell[] poplarLeaves = new Cell[] {
                CellNull.NULL_CELL,
                new PoplarLeafCell(1),
                new PoplarLeafCell(2),
                new PoplarLeafCell(3),
                new PoplarLeafCell(4),
                new PoplarLeafCell(5),
                new PoplarLeafCell(6),
                new PoplarLeafCell(7),
        };

        private final CellSolver solver = new com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver(new short[] {
                0x0412, 0x0311, 0x0211
        });

        @Override
        public Cell getCellForLeaves(int hydro) {
            return poplarLeaves[hydro];
        }

        @Override
        public Cell getCellForBranch(int radius, int meta) {
            if (meta == MetadataCell.TOP_BRANCH) return poplarTopBranch;
            if (radius == 1) return poplarBranch;
            if (radius < 4) return poplarUpperTrunk;
            return CellNull.NULL_CELL;
        }

        @Override
        public SimpleVoxmap getLeafCluster() {
            return LeafClusters.POPLAR;
        }

        @Override
        public CellSolver getCellSolver() {
            return solver;
        }

        @Override
        public int getDefaultHydration() {
            return 4;
        }

    };

    public static final CellKit DOME = new CellKit(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "dome")) {

        private final Cell acaciaBranch = new Cell() {
            @Override
            public int getValue() {
                return 5;
            }

            final int[] map = {0, 3, 5, 5, 5, 5};

            @Override
            public int getValueFromSide(Direction side) {
                return map[side.ordinal()];
            }

        };

        private final Cell[] acaciaLeafCells = {
                CellNull.NULL_CELL,
                new AcaciaLeafCell(1),
                new AcaciaLeafCell(2),
                new AcaciaLeafCell(3),
                new AcaciaLeafCell(4),
                new AcaciaLeafCell(5),
                new AcaciaLeafCell(6),
                new AcaciaLeafCell(7)
        };

        private final com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver acaciaSolver = new com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver(new short[]{0x0514, 0x0423, 0x0412, 0x0312, 0x0211});

        @Override
        public Cell getCellForLeaves(int hydro) {
            return acaciaLeafCells[hydro];
        }

        @Override
        public Cell getCellForBranch(int radius, int meta) {
            return radius == 1 ? acaciaBranch : CellNull.NULL_CELL;
        }

        @Override
        public SimpleVoxmap getLeafCluster() {
            return com.dtteam.dynamictrees.systems.cell.LeafClusters.ACACIA;
        }

        @Override
        public CellSolver getCellSolver() {
            return acaciaSolver;
        }

        @Override
        public int getDefaultHydration() {
            return 4;
        }

    };

    public static final CellKit JOSHUA = new CellKit(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "joshua")) {

        private final Cell branch = new Cell() {
            @Override
            public int getValue() {
                return 5;
            }

            @Override
            public int getValueFromSide(Direction side) {
                return side == Direction.UP ? getValue() : 0;
            }

        };

        private final Cell[] frondCells = {
                CellNull.NULL_CELL,
                new JoshuaFrondCell(1),
                new JoshuaFrondCell(2),
                new JoshuaFrondCell(3),
                new JoshuaFrondCell(4),
                new JoshuaFrondCell(5),
                new JoshuaFrondCell(6),
                new JoshuaFrondCell(7)
        };

        private final com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver joshuaSolver = new com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver(new short[]{0x0514, 0x0413});

        @Override
        public Cell getCellForLeaves(int hydro) {
            return frondCells[hydro];
        }

        @Override
        public Cell getCellForBranch(int radius, int meta) {
            return radius == 2 ? branch : CellNull.NULL_CELL;
        }

        @Override
        public SimpleVoxmap getLeafCluster() {
            return LeafClusters.JOSHUA;
        }

        @Override
        public CellSolver getCellSolver() {
            return joshuaSolver;
        }

        @Override
        public int getDefaultHydration() {
            return 4;
        }

        class JoshuaFrondCell extends MatrixCell {

            public JoshuaFrondCell(int value) {
                super(value, valMap);
            }

            static final byte[] valMap = {
                    0, 0, 0, 0, 0, 0, 0, 0, //D Maps * -> 0
                    0, 1, 2, 3, 4, 5, 6, 7, //U Maps
                    0, 0, 0, 0, 0, 0, 0, 0, //N Maps * -> 0
                    0, 0, 0, 0, 0, 0, 0, 0, //S Maps * -> 0
                    0, 0, 0, 0, 0, 0, 0, 0, //W Maps * -> 0
                    0, 0, 0, 0, 0, 0, 0, 0  //E Maps * -> 0
            };

        }
    };

    public static final CellKit WILLOW = new CellKit(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "willow")) {

        private final Cell branch = new WillowBranchCell();

        private final Cell[] willowLeafCells = {
                CellNull.NULL_CELL,
                new WillowLeafCell(1),
                new WillowLeafCell(2),
                new WillowLeafCell(3),
                new WillowLeafCell(4),
                new WillowLeafCell(5),
                new WillowLeafCell(6),
                new WillowLeafCell(7)
        };

        private final com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver solver = new com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver(new short[]{0x0817, 0x0726, 0x0625, 0x0714, 0x0614, 0x0514, 0x0413, 0x0312, 0x0211});

        @Override
        public Cell getCellForLeaves(int distance) {
            return this.willowLeafCells[distance];
        }

        @Override
        public Cell getCellForBranch(int radius, int meta) {
            return radius == 1 ? this.branch : CellNull.NULL_CELL;
        }

        @Override
        public CellSolver getCellSolver() {
            return this.solver;
        }

        @Override
        public SimpleVoxmap getLeafCluster() {
            return LeafClusters.WILLOW;
        }

        @Override
        public int getDefaultHydration() {
            return 7;
        }
    };

    public static final CellKit SPARSE = new CellKit(ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, "sparse")) {

        private final Cell sparseBranch = new SparseBranchCell();
        private final Cell sparseLeaves = new NormalCell(1);

        private final CellSolver solver = new  com.dtteam.dynamictrees.systems.cell.CellKits.BasicSolver(new short[] {0x0211});

        @Override
        public Cell getCellForLeaves(int hydro) {
            return hydro > 0 ? sparseLeaves : CellNull.NULL_CELL;
        }

        @Override
        public Cell getCellForBranch(int radius, int meta) {
            return radius == 1 ? sparseBranch : CellNull.NULL_CELL;
        }

        @Override
        public SimpleVoxmap getLeafCluster() {
            return LeafClusters.SPARSE;
        }

        @Override
        public CellSolver getCellSolver() {
            return solver;
        }

        @Override
        public int getDefaultHydration() {
            return 1;
        }

    };
}
