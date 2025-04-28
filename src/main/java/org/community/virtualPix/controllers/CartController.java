package org.community.virtualPix.controllers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.community.virtualPix.models.Cart;
import org.community.virtualPix.models.Product;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class CartController {

    private final Set<Cart> playerCarts = new HashSet<>();

    public Cart getPlayerCart(Player player) {
        for (Cart cart : playerCarts) {
            if (cart.getPlayer().equals(player)) {
                return cart;
            }
        }
        Cart cart = new Cart(player);
        playerCarts.add(cart);
        return cart;
    }

}
