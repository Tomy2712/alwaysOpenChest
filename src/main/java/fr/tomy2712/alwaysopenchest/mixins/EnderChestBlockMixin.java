package fr.tomy2712.alwaysopenchest.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EnderChestBlock.class)
public class EnderChestBlockMixin {

    @Shadow @Final
    private static Component CONTAINER_TITLE;
    /**
     * @author Tomy2712
     * @reason Always open ender chest
     * @return Always false to prevent the ender chest from being blocked by any block
     */
    @Overwrite
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        PlayerEnderChestContainer playerenderchestcontainer = pPlayer.getEnderChestInventory();
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (playerenderchestcontainer != null && blockentity instanceof EnderChestBlockEntity) {
            if (pLevel.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                EnderChestBlockEntity enderchestblockentity = (EnderChestBlockEntity)blockentity;
                playerenderchestcontainer.setActiveChest(enderchestblockentity);
                pPlayer.openMenu(new SimpleMenuProvider((p_53124_, p_53125_, p_53126_) -> {
                    return ChestMenu.threeRows(p_53124_, p_53125_, playerenderchestcontainer);
                }, CONTAINER_TITLE));
                pPlayer.awardStat(Stats.OPEN_ENDERCHEST);
                PiglinAi.angerNearbyPiglins(pPlayer, true);
                return InteractionResult.CONSUME;
            }
        } else {
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
    }
}
