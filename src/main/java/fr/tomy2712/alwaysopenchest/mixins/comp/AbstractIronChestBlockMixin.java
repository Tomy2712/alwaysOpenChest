package fr.tomy2712.alwaysopenchest.mixins.comp;

import net.minecraft.core.BlockPos;

import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import com.progwml6.ironchest.common.block.regular.AbstractIronChestBlock;
import org.spongepowered.asm.mixin.Overwrite;


@Mixin(AbstractIronChestBlock.class)
public abstract class AbstractIronChestBlockMixin {

    /**
     * @author Tomy2712
     * @reason Always open chest from iron chest
     * @return Always false to prevent blocking the chest
     */
    @Overwrite(remap = false)
    public static boolean isChestBlockedAt(LevelAccessor levelAccessor, BlockPos blockPos) {
        return false;
    }
}

