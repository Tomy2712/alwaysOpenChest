package fr.tomy2712.alwaysopenchest.mixins.comp;

import com.google.common.base.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.*;
import shetiphian.core.common.DyeHelper;
import shetiphian.core.common.Function;
import shetiphian.core.common.ToolHelper;
import shetiphian.enderchests.Configuration;
import shetiphian.enderchests.Values;
import shetiphian.enderchests.common.block.BlockEnderChest;
import shetiphian.enderchests.common.inventory.ContainerProviders;
import shetiphian.enderchests.common.misc.ChestHelper;
import shetiphian.enderchests.common.misc.ChestInfoHelper;
import shetiphian.enderchests.common.tileentity.TileEntityEnderChest;
import static shetiphian.enderchests.common.block.BlockEnderChest.getFacing;

@Mixin(BlockEnderChest.class)
public abstract class BlockEnderChestMixin {

    @Shadow @Final
    private static EnumProperty<BlockEnderChest.EnumType> VARIANT;


    @Shadow
    private TileEntityEnderChest getTile(BlockGetter world, BlockPos pos) { return null; }

    @Unique
    private BlockEnderChest alwaysOpenChest$self() {
        return (BlockEnderChest)(Object)this;
    }

    /**
     * @author Tomy2712
     * @reason Always open ender chest
     */
    @Overwrite(remap = false)
    public InteractionResult onBlockActivated(BlockState state, Level world, BlockPos pos, Player player, ItemStack heldItem) {
        TileEntityEnderChest chestTile = this.getTile(world, pos);
        if (chestTile == null) {
            return InteractionResult.PASS;
        } else if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else if (player.isShiftKeyDown()) {
            if (!heldItem.isEmpty()) {
                if (chestTile.isPublic()) {
                    boolean changed = false;
                    if (Values.listPersonal.contains(heldItem.getItem())) {
                        if ((Boolean) Configuration.ACCESS_SETTINGS.allowPersonalChests.get()) {
                            changed = true;
                            chestTile.setOwner(player, heldItem.copy().split(1));
                            Function.setBlock(world, pos, (BlockState)state.setValue(VARIANT, BlockEnderChest.EnumType.PRIVATE), true);
                        } else {
                            ChestInfoHelper.sendError(player, "enderchests.chest.private.disabled");
                        }
                    }

                    if (!changed && Values.listTeam.contains(heldItem.getItem())) {
                        if ((Boolean)Configuration.ACCESS_SETTINGS.allowTeamChests.get()) {
                            String teamID = Function.getPlayerTeamID(player);
                            if (!Strings.isNullOrEmpty(teamID)) {
                                changed = true;
                                chestTile.setOwner(Function.getTeamDisplayName(teamID), "#" + teamID, heldItem.copy().split(1));
                                Function.setBlock(world, pos, (BlockState)state.setValue(VARIANT, BlockEnderChest.EnumType.TEAM), true);
                            } else {
                                ChestInfoHelper.sendError(player, "shetiphian.team.noteam");
                            }
                        } else {
                            ChestInfoHelper.sendError(player, "enderchests.chest.team.disabled");
                        }
                    }

                    if (changed) {
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }

                        return InteractionResult.SUCCESS;
                    }
                }

                if (!chestTile.canEdit(player)) {
                    return InteractionResult.SUCCESS;
                }

                short capacity = ChestHelper.getCapacity(world, chestTile.getOwnerID(), chestTile.getCode());
                if (capacity < (Integer)Configuration.UPGRADE_SETTINGS.chestSizeMax.get()) {
                    int newValue = 0;
                    if (Values.listSmallCap_Single.contains(heldItem.getItem())) {
                        newValue = -1;
                        if (chestTile.applyItem(heldItem)) {
                            newValue = 3;
                        } else {
                            ChestInfoHelper.sendError(player, "enderchests.chest.upgrade_used");
                        }
                    }

                    if (newValue == 0 && Values.listSmallCap_Multi.contains(heldItem.getItem())) {
                        newValue = 3;
                    }

                    if (newValue == 0 && capacity + 9 <= (Integer)Configuration.UPGRADE_SETTINGS.chestSizeMax.get()) {
                        if (Values.listLargeCap_Single.contains(heldItem.getItem())) {
                            newValue = -1;
                            if (chestTile.applyItem(heldItem)) {
                                newValue = 9;
                            } else {
                                ChestInfoHelper.sendError(player, "enderchests.chest.upgrade_used");
                            }
                        }

                        if (newValue == 0 && Values.listLargeCap_Multi.contains(heldItem.getItem())) {
                            newValue = 9;
                        }
                    }

                    if (newValue > 0) {
                        ChestHelper.setCapacity(world, chestTile.getOwnerID(), chestTile.getCode(), (byte)(capacity + newValue));
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }

                        ChestInfoHelper.sendCapacityInfo(player, chestTile);
                        return InteractionResult.SUCCESS;
                    }
                }

                if ((Boolean)Configuration.GENERAL.enableInWorldRecoloring.get()) {
                    int subHit = Function.getSubShapeHit(player, pos, (VoxelShape[])BlockEnderChest.SHAPES.get(state.getValue(BlockEnderChest.FACING)));
                    if (subHit > 0 && subHit < 4) {
                        DyeColor color = DyeHelper.getDyeColor(heldItem.getItem());
                        if (color != null && chestTile.doColorRing(color, player)) {
                            if (!player.getAbilities().instabuild) {
                                heldItem.shrink(1);
                            }

                            world.updateNeighborsAt(pos, alwaysOpenChest$self());
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }

            return InteractionResult.SUCCESS;
        } else {
            if (heldItem.isEmpty() || !ToolHelper.isWrench(heldItem)) {
                BlockPos blockpos = pos.relative(getFacing(state).getOpposite());
                if (player instanceof ServerPlayer && chestTile.canUse(player)) {
                    NetworkHooks.openScreen((ServerPlayer)player, new ContainerProviders.EnderChest(chestTile), pos);
                }
            }

            return InteractionResult.SUCCESS;
        }
    }
}
