package org.community.virtualPix.controllers;

import org.community.virtualPix.models.Payment;
import org.community.virtualPix.payments.qrcode.ImageCreator;
import org.community.virtualPix.payments.services.PaymentService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public class PaymentController {
    private final PaymentService paymentService;
    private final Logger logger;

    public PaymentController(PaymentService paymentService, Logger logger) {
        this.paymentService = paymentService;
        this.logger = logger;
    }

    public void handlePlayerPayment(Player player, double amount, String email) {
        try {
            Payment payment = paymentService.createPlayerPayment(player, amount, email);

            final BufferedImage qr = ImageCreator.generateQR(payment.getQrCode());
            if(qr != null) {
                ImageCreator.generateMap(qr, player);

            }
            player.sendMessage("id: " + payment.getPaymentId());
            String paymentLink = "https://www.mercadopago.com.br/checkout/v1/payment/" + payment.getPaymentId() + "/review";
            player.sendMessage("link pagamento: " + paymentLink);
            player.sendMessage("§aPagamento PIX criado com sucesso!");
            player.sendMessage("§eValor: R$ " + amount);

        } catch (Exception e) {
            player.sendMessage("§cErro ao criar pagamento. Tente novamente mais tarde.");
            logger.severe("Erro no pagamento do jogador " + player.getName() + ": " + e.getMessage());
        }
    }

    public void notifyPaymentApproval(Payment payment) {
        Bukkit.broadcastMessage("§aO jogador " + payment.getPlayerName() +
                " realizou um pagamento PIX de R$ " + payment.getAmount() + "!");

        Player player = Bukkit.getPlayer(payment.getPlayerUUID());
        if (player != null && player.isOnline()) {
            player.sendMessage("§aSeu pagamento foi confirmado! Obrigado pela sua contribuição!");
        }
    }
}