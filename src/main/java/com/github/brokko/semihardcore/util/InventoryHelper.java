package com.github.brokko.semihardcore.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InventoryHelper {

    public static void replace(Player player, ItemStack oldStack, ItemStack newStack) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i <= inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).equals(oldStack)) {
                inventory.removeItem(oldStack);
                inventory.add(i, newStack);

                return;
            }
        }
    }
}
