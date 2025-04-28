package org.community.virtualPix.payments.services;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.community.virtualPix.controllers.MongoController;
import org.community.virtualPix.models.Payment;
import org.bukkit.entity.Player;
import org.bson.Document;

import java.util.Map;
import java.util.logging.Logger;

public class PaymentService {
    private final MongoController mongoController;
    private final MercadoPagoService mercadoPagoService;
    private final Logger logger;

    public PaymentService(MongoController mongoController,
                          MercadoPagoService mercadoPagoService,
                          Logger logger) {
        this.mongoController = mongoController;
        this.mercadoPagoService = mercadoPagoService;
        this.logger = logger;
    }

    public Payment createPlayerPayment(Player player, double amount, String email) {
        try {
            Payment payment = mercadoPagoService.createPixPayment(
                    amount,
                    "Doação no servidor Minecraft por " + player.getName(),
                    email, player);
            mongoController.savePayment(payment.toDocument());
            return payment;
        } catch (Exception e) {
            logger.severe("Erro ao criar pagamento: " + e.getMessage());
            throw new RuntimeException("Falha ao criar pagamento PIX: " + e.getMessage(), e);
        }
    }

    // Métodos auxiliares para extração segura
    private String getStringValue(Map<?, ?> map, String key) {
        if (map == null || !map.containsKey(key)) {
            throw new RuntimeException("Campo obrigatório '" + key + "' não encontrado na resposta");
        }
        return String.valueOf(map.get(key));
    }

    private Map<?, ?> getMapValue(Map<?, ?> map, String key) {
        Object value = map.get(key);
        if (value instanceof Map) {
            return (Map<?, ?>) value;
        }
        throw new RuntimeException("Campo '" + key + "' não é um objeto válido");
    }

    public void checkAndUpdatePayments() {
        try {
            for (Document doc : mongoController.getPendingPayments()) {
                String paymentId = doc.getString("id-pagamento");
                Map<String, Object> paymentData = mercadoPagoService.checkPaymentStatus(paymentId);
                String newStatus = (String) paymentData.get("status");

                if (!newStatus.equalsIgnoreCase(doc.getString("status"))) {
                    mongoController.updatePaymentStatus(paymentId, newStatus);

                    if (newStatus.equalsIgnoreCase("approved")) {
                        Payment payment = new Payment(doc);
                        onPaymentApproved(payment);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void onPaymentApproved(Payment payment) {
        Bukkit.broadcast(Component.text("§r")
                .append(Component.text("  §a§lPagamento aprovado com sucesso."))
                .append(Component.text("  §fValor: §7" + payment.getAmount() + " §fde §a" + payment.getPlayerName())).appendNewline());
        Player player = Bukkit.getPlayer(payment.getPlayerName());
        if (player != null) {
            player.sendMessage("§aSeu pagamento foi confirmado.");
        }
    }
}