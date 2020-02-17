package anticheese;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class AntiCheese extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        sterilize(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        sterilize(event.getInventory());
        sterilize(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onOpenSelf(InventoryCloseEvent event) {
        sterilize(event.getInventory());
        sterilize(event.getPlayer().getInventory());
    }

    public void sterilize(Inventory inventory) {
        inventory.forEach(this::sterilize);
    }

    public void sterilize(ItemStack param) {
        if (param == null) return;
        Map<Enchantment, Integer> enchants = param.getEnchantments();

        for (Enchantment enchantment : enchants.keySet()) param.removeEnchantment(enchantment);
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            boolean addable = true;
            for (Enchantment enchantment : param.getEnchantments().keySet()) {
                if (entry.getKey().conflictsWith(enchantment)) {
                    addable = false;
                    break;
                }
            }
            if (!addable) continue;

            if (!entry.getKey().canEnchantItem(param)) param.removeEnchantment(entry.getKey());
            else
                param.addEnchantment(entry.getKey(), Math.max(Math.min(0, entry.getValue()), entry.getKey().getMaxLevel()));
        }
    }
}