package net.bagaja.random_drops;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Random_drops implements ModInitializer {

    private final Random random = new Random();
    private List<Item> allItems;

    @Override
    public void onInitialize() {
        initializeItemList();
        registerRandomDrops();
    }

    private void initializeItemList() {
        allItems = new ArrayList<>();
        Registries.ITEM.forEach(allItems::add);
    }

    private void registerRandomDrops() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient && !allItems.isEmpty()) {
                // Cancel the block's default drops
                world.setBlockState(pos, state.getFluidState().getBlockState(), 3);

                // Spawn a random item at the position
                spawnRandomItem(world, pos);

                return false; // Cancel the rest of the event (including default drops)
            }

            return true; // Allow the block to break normally if we're on the client side or if there are no items
        });
    }

    private void spawnRandomItem(World world, BlockPos pos) {
        Item randomItem = allItems.get(random.nextInt(allItems.size()));
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        ItemEntity itemEntity = new ItemEntity(world, x, y, z, randomItem.getDefaultStack());
        world.spawnEntity(itemEntity);
    }
}