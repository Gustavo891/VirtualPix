package org.community.virtualPix.view;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.community.virtualPix.controllers.CartController;
import org.community.virtualPix.models.Cart;
import org.community.virtualPix.models.Product;
import org.community.virtualPix.util.ItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.community.virtualPix.util.Formatter.df;

@RequiredArgsConstructor
public class ShopView extends View {

    private final CartController controller;
    private final ViewFrame frame;

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.cancelOnClick()
                .layout(
                        "         ",
                        "    C    ",
                        "         ",
                        "F   P    ")
                .scheduleUpdate(20L);
    }

    public void onFirstRender(@NotNull RenderContext renderContext) {
        Player player = renderContext.getPlayer();
        Cart cart = controller.getPlayerCart(player);

        renderContext.layoutSlot('C', cashShop()).onClick(e -> {
           renderContext.openForPlayer(CashShopView.class, player);
            player.playSound(player, "minecraft.click", 1.0f, 1.0f);
        });
        renderContext.layoutSlot('P').renderWith(() -> getCart(cart)).onClick(e -> {
            renderContext.openForPlayer(BuyView.class, player);
            player.playSound(player, "minecraft.click", 1.0f, 1.0f);
        });
        renderContext.layoutSlot('F', fechar()).onClick(e -> {
            player.playSound(player, "minecraft.click", 1.0f, 1.0f);
            renderContext.closeForPlayer();
        });
    }

    public ItemStack fechar() {
        return new ItemBuilder(Material.ARROW).name("§cFechar").build();
    }

    public ItemStack cashShop() {
        return new ItemBuilder(Material.GOLD_INGOT).name("§6Cash").lore(List.of("§7Utilize o cash para adquirir", "§7produtos no factions.", "", "§8Clique para acessar.")).build();
    }

    public ItemStack getCart(Cart cart) {
        ItemBuilder item = new ItemBuilder(Material.MINECART)
                .name("§fCarrinho de Compras");
        List<String> lore = new ArrayList<>(List.of(
                "§7Visualize os produtos que",
                "§7está comprando.",
                ""
        ));

        if(!cart.getProducts().isEmpty()) {
           lore.add("  §fLista de produtos:");
           for(Map.Entry<Product, Integer> entry : cart.getProducts().entrySet()) {
               Product product = entry.getKey();
               double totalPrice = entry.getValue() * product.getValue();
               lore.add(String.format("§8  x%s §f%s §7➡ §2R$§f%s", entry.getValue(), product.getName(), df.format(totalPrice)));
           }
           lore.add("");
           lore.add("  §fTotal: §2R$§f" + df.format(cart.getCost()));
           lore.add("");
        } else {
            lore.add("§cNenhum produto adicionado.");
        }
        item.lore(lore);
        return item.build();

    }


}
