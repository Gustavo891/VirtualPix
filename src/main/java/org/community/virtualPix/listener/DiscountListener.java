package org.community.virtualPix.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.ViewFrame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.community.virtualPix.controllers.CartController;
import org.community.virtualPix.controllers.DiscountController;
import org.community.virtualPix.models.Cart;
import org.community.virtualPix.models.Discount;
import org.community.virtualPix.view.BuyView;

import javax.swing.text.ViewFactory;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor @Getter
public class DiscountListener implements Listener {

    private final CartController controller;
    private final DiscountController discountController;
    private final ViewFrame frame;
    private final Set<Player> waitingDiscount = new HashSet<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!waitingDiscount.contains(player)) return;

        event.setCancelled(true);
        String couponName = event.getMessage().trim();

        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugins()[0], () -> {
            Cart cart = controller.getPlayerCart(player);
            Discount discount = discountController.getByName(couponName);

            if (discount != null) {
                cart.setDiscount(discount);
                player.sendMessage("§aCupom §e" + couponName + "§a aplicado com sucesso!");
            } else {
                player.sendMessage("§cCupom inválido.");
            }

            waitingDiscount.remove(player);
            frame.open(BuyView.class, player);
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        waitingDiscount.remove(event.getPlayer());
    }


}
