package fr.tomy2712.alwaysopenchest.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {

    /**
     * @author Tomy2712
     * @reason Always open shulker box
     * @return Always true to allow opening the shulker box regardless of surrounding blocks
     */
    @Overwrite
    private static boolean canOpen(BlockState pState, Level pLevel, BlockPos pPos, ShulkerBoxBlockEntity pBlockEntity) {
        return true;
    }
}
