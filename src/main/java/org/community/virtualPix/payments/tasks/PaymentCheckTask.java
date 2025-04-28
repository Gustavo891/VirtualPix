package org.community.virtualPix.payments.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.community.virtualPix.payments.services.PaymentService;

public class PaymentCheckTask extends BukkitRunnable {
    private final PaymentService paymentService;

    public PaymentCheckTask(org.bukkit.plugin.Plugin plugin, PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void run() {
        paymentService.checkAndUpdatePayments();
    }
}