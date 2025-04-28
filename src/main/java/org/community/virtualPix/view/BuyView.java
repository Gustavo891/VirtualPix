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
import org.community.virtualPix.controllers.DiscountController;
import org.community.virtualPix.listener.DiscountListener;
import org.community.virtualPix.models.Cart;
import org.community.virtualPix.models.Product;
import org.community.virtualPix.util.ItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.community.virtualPix.util.Formatter.df;

public class BuyView extends View {

    private final CartController controller;
    private final ViewFrame frame;
    private final DiscountController discountController;
    private final DiscountListener discountListener;

    public BuyView(VirtualPix virtualPix) {
        this.controller = virtualPix.getCartController();
        this.frame = virtualPix.getFrame();
        this.discountController = virtualPix.getDiscountController();
        this.discountListener = virtualPix.getDiscountListener();
    }

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.cancelOnClick()
                .layout(
                        "         ",
                        "  CP  B  ",
                        "         ",
                        "    F    ")
                .scheduleUpdate(20L);
    }

    public void onFirstRender(@NotNull RenderContext renderContext) {
        Player player = renderContext.getPlayer();
        Cart cart = controller.getPlayerCart(player);

        renderContext.layoutSlot('C', getCart(cart)).onClick(e -> {

        });
        renderContext.layoutSlot('P').renderWith(() -> getDiscount(cart)).onClick(e -> {
            discountListener.getWaitingDiscount().add(player);
            renderContext.closeForPlayer();
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

    public ItemStack getDiscount(Cart cart) {
        ItemBuilder item = new ItemBuilder(Material.NAME_TAG);
        item.name("§fCupom de desconto");
        if(cart.getDiscount() == null) {
            item.lore(List.of("§f\uE036 §7Clique para adicionar."));
        } else {
            item.lore(List.of(
                    "",
                    "  §fNome: §7" + cart.getDiscount().getId().toUpperCase(),
                    "  §fValor: §a" + cart.getDiscount().getDiscount() + "%",
                    "",
                    "§f\uE036 §7Clique para altera-lo."
            ));
        }

        return item.build();
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
            if(cart.getDiscount() != null) {
                lore.add("  §fSub-total: §2R$§f" + df.format(cart.getCost()));
                lore.add("  §fDesconto: §c-" + cart.getDiscount().getDiscount() + "%");
                lore.add("  §fTotal: §2R$§f" + df.format(cart.getDiscountCost()));

            } else {
                lore.add("  §fTotal: §2R$§f" + df.format(cart.getCost()));
            }
            lore.add("");
        } else {
            lore.add("§cNenhum produto adicionado.");
        }
        item.lore(lore);
        return item.build();

    }

}
