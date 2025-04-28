package org.community.virtualPix.commands;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.community.virtualPix.controllers.DiscountController;
import org.community.virtualPix.models.Discount;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;

@RequiredArgsConstructor
public class DiscountCommand {

    private final DiscountController controller;

    @Command("discount <id> <value> <lifetime> <usages>")
    public void discountCommand(CommandSender commandSender, @Named("id") String id, @Named("value") double value, @Named("lifetime") boolean lifetime, @Optional @Named("usages") int usages) {
        if (!(commandSender instanceof Player player)) return;
        if(!player.hasPermission("virtualpix.discount")) return;
        if (controller.getByName(id) == null) {
            if (!lifetime && usages < 0) {
                player.sendMessage("§cCaso cupom não for infinito, defina a quantidade de usos.");
                return;
            }
            Discount discount = new Discount(id, value, usages, lifetime);
            controller.addDiscount(discount);
            player.sendMessage("§aCupom de desconto criado com sucesso.");
        } else {
            player.sendMessage("§cCupom já existente.");
        }
    }


    @Command("getdiscounts")
    public void discountCommand(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;
        if (!player.hasPermission("virtualpix.discount")) return;
        player.sendMessage("Cupons: " + controller.getDiscounts());
    }
}
