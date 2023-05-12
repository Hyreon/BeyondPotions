package top.hyreon.beyondPotions.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.hyreon.beyondPotions.BrewAction;

public class StenchCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Your console is incapable of smelling the stench of things.");
            return true;
        }

        Player player = (Player) commandSender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        ItemStack heldIngredient = player.getInventory().getItemInOffHand();

        if (heldItem.getType() == Material.AIR) {
            player.sendMessage("You smell the air in your hand. It smells like scentlessness.");
            return true;
        } else {
            //TODO replace this with the actual stench command, rather than this hacky brewing test.
            BrewAction.RANDOM.brew(null, heldItem, heldIngredient);
        }

        /*
        Stench stench = Stench.get(heldItem.getItemMeta().getLore());

        if (stench == null) {
            commandSender.sendMessage("Smells like " +  heldItem.getType().name() + ".");
            return true;
        }

        commandSender.sendMessage(stench.getInfoString());
        */

        return true;
    }
}
