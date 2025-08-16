package fr.tomy2712.alwaysopenchest.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;


@Mixin(ChestBlock.class)
public class ChestBlockMixin {

    /**
     * @author Tomy2712
     * @reason Always open chest
     * @return Always false to prevent the chest from being blocked by any block
     */
    @Overwrite
    public static boolean isChestBlockedAt(LevelAccessor levelAccessor, BlockPos blockPos) {
        return false;
    }
}

