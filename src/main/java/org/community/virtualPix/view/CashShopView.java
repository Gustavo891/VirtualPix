package org.community.virtualPix.view;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.community.virtualPix.VirtualPix;
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
public class CashShopView extends View {

    private final CartController controller;
    private final ViewFrame frame;

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.cancelOnClick()
                .layout(
                        "         ",
                        "   1 2   ",
                        "         ",
                        "F   P    ")
                .scheduleUpdate(20L);
    }

    public void onFirstRender(@NotNull RenderContext renderContext) {
        Player player = renderContext.getPlayer();
        Cart cart = controller.getPlayerCart(player);

        renderContext.layoutSlot('1').renderWith(() -> product1(VirtualPix.productController.getCashProduct1(), cart)).onClick(e -> {
            player.playSound(player, "minecraft.click", 1.0f, 1.0f);
            if(e.isLeftClick()) {
                cart.increaseProduct(VirtualPix.productController.getCashProduct1());
            } else if(e.getClickIdentifier().equalsIgnoreCase("RIGHT")) {
                cart.decreaseProduct(VirtualPix.productController.getCashProduct1());
            } else if (e.getClickIdentifier().equals("SHIFT_RIGHT")) {
                cart.clearProduct(VirtualPix.productController.getCashProduct1());
            }
            renderContext.update();
        });
        renderContext.layoutSlot('2').renderWith(() -> product1(VirtualPix.productController.getCashProduct2(), cart)).onClick(e -> {
            player.playSound(player, "minecraft.click", 1.0f, 1.0f);
            if(e.isLeftClick()) {
                cart.increaseProduct(VirtualPix.productController.getCashProduct2());
            } else if(e.getClickIdentifier().equalsIgnoreCase("RIGHT")) {
                cart.decreaseProduct(VirtualPix.productController.getCashProduct2());
            } else if (e.getClickIdentifier().equals("SHIFT_RIGHT")) {
                cart.clearProduct(VirtualPix.productController.getCashProduct2());
            }
            renderContext.update();
        });
        renderContext.layoutSlot('P').renderWith(() -> getCart(cart)).onClick(e -> {
            renderContext.openForPlayer(BuyView.class, player);
            player.playSound(player, "minecraft.click", 1.0f, 1.0f);

        });
        renderContext.layoutSlot('F', fechar()).onClick(e -> {
            player.playSound(player, "minecraft.click", 1.0f, 1.0f);
            renderContext.openForPlayer(ShopView.class);
        });
    }

    public ItemStack product1(Product product, Cart cart) {
        ItemBuilder builder = new ItemBuilder(product.getItem());
        List<String> lore = new ArrayList<>();
        lore.add("§r");
        lore.add("§r  §fCusto: §2R$§f" + df.format(product.getValue()));
        lore.add("§r");
        if(cart.getProducts().containsKey(product)) {
            builder.name(product.getName() + " §7[x" + cart.getProducts().get(product) + "]");
            lore.add("§f\uE036 §7Botão-esquerdo para adicionar.");
            lore.add("§f\uE037 §7Botão-direito para remover.");
            lore.add("§f\uE037 §7Shift + botão-direito para limpar.");
        } else {
            builder.name(product.getName());
            lore.add("§f\uE036 §7Botão-esquerdo para adicionar.");
        }
        builder.lore(lore);

        return builder.build();
    }

    public ItemStack fechar() {
        return new ItemBuilder(Material.ARROW).name("§cVoltar").build();
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
