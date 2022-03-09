package me.twoaster.chequeslite;

import me.twoaster.chequeslite.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EventManager implements Listener {
    public EventManager() {
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        Player player = event.getPlayer();

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.PAPER)
            return;

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore()) {
            Messages.sendMessage(Messages.Keys.INVALID_CLICK_CHEQUE, player);
            return;
        }

        if (!player.hasPermission("chequeslite.cashclick")) {
            Messages.sendMessage(Messages.Keys.NO_PERMISSION_CLICK, player);
            return;
        }

        if (!ItemStackUtil.hasNBTData(item, "creator")) {
            Messages.sendMessage(Messages.Keys.INVALID_CHEQUE, player);
            return;
        }

        double worth;
        try {
            worth = Double.parseDouble(ItemStackUtil.getNBTData(item, "worth"));
        } catch (NumberFormatException exception) {
            Messages.sendMessage(Messages.Keys.INVALID_CHEQUE_VALUE, player);
            return;
        }

        int amount = item.getAmount();
        double total = worth * amount;
        ItemStackUtil.removeStack(item, player, item.getAmount(), event.getHand() == EquipmentSlot.OFF_HAND);

        ChequesLite.economy.depositPlayer(player, total);

        if (amount > 1)
            player.sendMessage(Messages.getMessage(Messages.Keys.CHEQUE_CASHED_MULTIPLE)
                    .replace("%worth%", ChequesLite.economy.format(worth))
                    .replace("%count%", String.valueOf(amount))
                    .replace("%total%", ChequesLite.economy.format(total)));
        else
            player.sendMessage(Messages.getMessage(Messages.Keys.CHEQUE_CASHED).replace("%worth%", ChequesLite.economy.format(worth)));
    }
}
