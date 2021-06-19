package de.melanx.utilitix;

import de.melanx.utilitix.content.bell.ItemMobBell;
import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import de.melanx.utilitix.network.StickyChunkRequestSerializer;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {

    private static final IFormattableTextComponent BLACKLISTED_MOB = new TranslationTextComponent("tooltip." + UtilitiX.getInstance().modid + ".blacklisted_mob").mergeStyle(TextFormatting.DARK_RED);
    private static final IFormattableTextComponent GILDED = new TranslationTextComponent("tooltip.utilitix.gilded").mergeStyle(TextFormatting.GOLD);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        PlayerEntity player = event.getPlayer();

        if (player.isSneaking() && player.getHeldItem(event.getHand()).getItem() == ModItems.mobBell && event.getTarget() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) event.getTarget();
            Hand hand = event.getHand();
            ItemStack stack = player.getHeldItem(hand);
            ResourceLocation entityKey = EntityType.getKey(target.getType());
            if (entityKey.toString().equals(stack.getOrCreateTag().getString("Entity"))) {
                return;
            }

            if (UtilitiXConfig.HandBells.blacklist.contains(entityKey)) {
                player.sendStatusMessage(BLACKLISTED_MOB, true);
                return;
            }

            stack.getOrCreateTag().putString("Entity", entityKey.toString());
            player.setHeldItem(hand, stack);
            player.sendStatusMessage(ItemMobBell.getCurrentMob(target.getType()), true);
            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
        }
    }

    // TODO wait for https://github.com/MinecraftForge/MinecraftForge/pull/7715
//    @SubscribeEvent
//    public void onBowFindAmmo(PlayerFindProjectileEvent event) {
//        if (event.getFoundAmmo().isEmpty()) {
//            PlayerEntity player = event.getPlayer();
//            Stream.concat(Stream.of(player.getHeldItemOffhand()), player.inventory.mainInventory.stream())
//                    .filter(stack -> stack.getItem() == ModItems.quiver)
//                    .filter(stack -> !Quiver.isEmpty(stack))
//                    .findFirst()
//                    .ifPresent(stack -> {
//                        IItemHandlerModifiable inventory = Quiver.getInventory(stack);
//                        assert inventory != null;
//                        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
//                        if (enchantmentLevel >= 1) {
//                            for (int i = 0; i < inventory.getSlots(); i++) {
//                                ItemStack arrow = inventory.getStackInSlot(i);
//                                if (!arrow.isEmpty()) {
//                                    event.setAmmo(arrow.copy());
//                                    return;
//                                }
//                            }
//                        } else {
//                            for (int i = 0; i < inventory.getSlots(); i++) {
//                                ItemStack arrow = inventory.getStackInSlot(i);
//                                if (!arrow.isEmpty()) {
//                                    arrow = player.isCreative() ? arrow.copy() : arrow;
//                                    event.setAmmo(arrow);
//                                    return;
//                                }
//                            }
//                        }
//                    });
//        }
//    }

    @SubscribeEvent
    public void entityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof ArmorStandEntity && event.getTarget().getPersistentData().getBoolean("UtilitiXArmorStand")) {
            if (event.getItemStack().getItem() == Items.FLINT && event.getPlayer().isSneaking()) {
                ArmorStandEntity entity = (ArmorStandEntity) event.getTarget();
                if (UtilitiXConfig.armorStandPoses.size() >= 2) {
                    int newIdx = (entity.getPersistentData().getInt("UtilitiXPoseIdx") + 1) % UtilitiXConfig.armorStandPoses.size();
                    entity.getPersistentData().putInt("UtilitiXPoseIdx", newIdx);
                    UtilitiXConfig.armorStandPoses.get(newIdx).apply(entity);
                }
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void loadChunk(ChunkEvent.Load event) {
        if (event.getWorld().isRemote()) {
            UtilitiX.getNetwork().instance.sendToServer(new StickyChunkRequestSerializer.StickyChunkRequestMessage(event.getChunk().getPos()));
        }
    }

    @SubscribeEvent
    public void neighbourChange(BlockEvent.NeighborNotifyEvent event) {
        if (!event.getWorld().isRemote() && event.getWorld() instanceof World) {
            World world = (World) event.getWorld();
            for (Direction dir : Direction.values()) {
                BlockPos thePos = event.getPos().offset(dir);
                BlockState state = world.getBlockState(thePos);
                if (state.getBlock() == Blocks.MOVING_PISTON && (state.get(BlockStateProperties.FACING) == dir || state.get(BlockStateProperties.FACING) == dir.getOpposite())) {
                    // Block has been changed because of a piston move.
                    // Glue logic is handled in the piston til
                    // Skip this here
                    return;
                } else if (state.getBlock() == Blocks.PISTON_HEAD && state.get(BlockStateProperties.SHORT) && (state.get(BlockStateProperties.FACING) == dir || state.get(BlockStateProperties.FACING) == dir.getOpposite())) {
                    // Block has been changed because of a piston move.
                    // Glue logic is handled in the piston til
                    // Skip this here
                    // This is sometimes buggy but we can't really do anything about this.
                    return;
                }
            }
            Chunk chunk = world.getChunkAt(event.getPos());
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
            //noinspection ConstantConditions
            if (glue != null) {
                int x = event.getPos().getX() & 0xF;
                int y = event.getPos().getY();
                int z = event.getPos().getZ() & 0xF;
                for (Direction dir : Direction.values()) {
                    if (glue.get(x, y, z, dir) && !SlimyCapability.canGlue(world, event.getPos(), dir)) {
                        glue.set(x, y, z, dir, false);
                        chunk.markDirty();
                        chunk.markDirty();
                        BlockPos targetPos = event.getPos().offset(dir);
                        ItemEntity ie = new ItemEntity(world, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, new ItemStack(ModItems.glueBall));
                        ie.setPickupDelay(20);
                        world.addEntity(ie);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onItemDespawn(ItemExpireEvent event) {
        ItemEntity entity = event.getEntityItem();
        World world = entity.getEntityWorld();
        if (!world.isRemote) {
            BlockPos pos = entity.getPosition();
            ItemStack stack = entity.getItem();
            if (stack.getItem() instanceof BlockItem) {
                BlockItem item = (BlockItem) stack.getItem();
                if (!UtilitiXConfig.plantsOnDespawn.test(item.getRegistryName())) {
                    return;
                }

                DirectionalPlaceContext context = new DirectionalPlaceContext(world, pos, Direction.DOWN, stack, Direction.UP);
                if (item.tryPlace(context) == ActionResultType.SUCCESS) {
                    world.setBlockState(pos, item.getBlock().getDefaultState());
                    return;
                }

                context = new DirectionalPlaceContext(world, pos.up(), Direction.DOWN, stack, Direction.UP);
                if (item.tryPlace(context) == ActionResultType.SUCCESS) {
                    world.setBlockState(pos.up(), item.getBlock().getDefaultState());
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseItem() == Event.Result.ALLOW || event.getUseBlock() == Event.Result.DENY) {
            return;
        }

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof DoorBlock) && !BlockTags.DOORS.contains(state.getBlock()) || state.getBlock().material == Material.IRON) {
            return;
        }

        Direction facing = state.get(DoorBlock.FACING);
        DoorHingeSide hinge = state.get(DoorBlock.HINGE);
        DoubleBlockHalf half = state.get(DoorBlock.HALF);
        boolean open = state.get(DoorBlock.OPEN);

        BlockPos neighborPos = pos.offset(hinge == DoorHingeSide.LEFT ? facing.rotateY() : facing.rotateYCCW());

        BlockState neighborState = world.getBlockState(neighborPos);
        if (!(neighborState.getBlock() instanceof DoorBlock) && !BlockTags.DOORS.contains(neighborState.getBlock()) || neighborState.getBlock().material == Material.IRON) {
            return;
        }

        if (neighborState.get(DoorBlock.HALF) == half && neighborState.get(DoorBlock.HINGE) != hinge && neighborState.get(DoorBlock.FACING) == facing) {
            ((DoorBlock) neighborState.getBlock()).openDoor(world, neighborState, neighborPos, !open);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (GildingArmorRecipe.isGilded(stack)) {
            event.getToolTip().add(2, GILDED);
        }
    }
}
