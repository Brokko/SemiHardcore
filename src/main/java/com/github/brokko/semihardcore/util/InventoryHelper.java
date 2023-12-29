package com.github.brokko.semihardcore.util;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
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

    /**
     * Rename items to contain the players name
     */
    public static void updateName(ItemStack stack, Player player) {
        // TODO Should use Component api
        String itemName =   I18n.get(stack.getItem().getName(stack).getString());;
        String playerName = player.getScoreboardName();
        stack.setHoverName(Component.Serializer.fromJson("{\"text\":\"" + itemName + " (" + playerName + ")" + "\"}"));
    }
}
