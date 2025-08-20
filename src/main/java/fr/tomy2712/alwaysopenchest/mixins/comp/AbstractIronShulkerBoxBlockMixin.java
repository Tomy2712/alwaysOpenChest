package fr.tomy2712.alwaysopenchest.mixins.comp;

import com.progwml6.ironshulkerbox.common.block.AbstractIronShulkerBoxBlock;
import com.progwml6.ironshulkerbox.common.block.entity.AbstractIronShulkerBoxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AbstractIronShulkerBoxBlock.class)
public class AbstractIronShulkerBoxBlockMixin {

    /**
     * @author Tomy2712
     * @reason Always open shulker box
     * @return Always true to allow opening the shulker box regardless of surrounding blocks
     */
    @Overwrite(remap = false)
    private static boolean canOpen(BlockState pState, Level pLevel, BlockPos pPos, AbstractIronShulkerBoxBlockEntity pBlockEntity) {
        return true;
    }
}
