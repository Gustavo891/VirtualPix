package org.community.virtualPix.commands;

import org.community.virtualPix.controllers.PaymentController;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;

public class PixCommand {
    private final PaymentController paymentController;

    public PixCommand(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    @Command("pix <value> <email>")
    public void pix(CommandSender sender, @Named("value") double amount, @Named("email") String email) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando só pode ser executado por jogadores.");
            return;
        }

        try {
            if (amount <= 0) {
                player.sendMessage("§cO valor deve ser maior que zero.");
                return;
            }

            if (!isValidEmail(email)) {
                player.sendMessage("§cE-mail inválido. Use um e-mail válido como: seuemail@provedor.com");
                return;
            }

            paymentController.handlePlayerPayment(player, amount, email);

        } catch (NumberFormatException e) {
            player.sendMessage("§cValor inválido. Use números (ex: 10.50)");
        }

        return;
    }

    private boolean isValidEmail(String email) {
        // Validação simples de e-mail
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$");
    }

}