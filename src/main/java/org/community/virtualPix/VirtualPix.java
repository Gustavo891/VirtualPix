package org.community.virtualPix;

import lombok.Getter;
import lombok.Setter;
import me.devnatan.inventoryframework.ViewFrame;
import org.community.virtualPix.commands.DiscountCommand;
import org.community.virtualPix.commands.PixCommand;
import org.community.virtualPix.commands.ShopCommand;
import org.community.virtualPix.controllers.*;
import org.community.virtualPix.listener.DiscountListener;
import org.community.virtualPix.payments.services.MercadoPagoService;
import org.community.virtualPix.payments.services.PaymentService;
import org.community.virtualPix.payments.tasks.PaymentCheckTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.community.virtualPix.view.BuyView;
import org.community.virtualPix.view.CashShopView;
import org.community.virtualPix.view.ShopView;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Getter @Setter
public final class VirtualPix extends JavaPlugin {

    private Lamp<BukkitCommandActor> lamp;
    private MongoController mongoController;
    private MercadoPagoService mercadoPagoService;
    private PaymentService paymentService;
    private PaymentController paymentController;
    private CartController cartController;
    private DiscountController discountController;
    private DiscountListener discountListener;
    public static ProductController productController = new ProductController();
    private ViewFrame frame;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.frame = ViewFrame.create(this);
        this.mongoController = new MongoController(
                getConfig().getString("mongodb.connection_string"),
                getLogger()
        );

        this.mercadoPagoService = new MercadoPagoService(
                getConfig().getString("mercado_pago.access_token"),
                getLogger()
        );

        this.paymentService = new PaymentService(
                mongoController,
                mercadoPagoService,
                getLogger()
        );

        this.paymentController = new PaymentController(
                paymentService,
                getLogger()
        );

        this.cartController = new CartController();
        this.discountController = new DiscountController();

        this.discountListener = new DiscountListener(cartController, discountController, frame);
        getServer().getPluginManager().registerEvents(discountListener, this);

        frame.with(new ShopView(cartController, frame), new CashShopView(cartController, frame), new BuyView(this));
        frame.register();

        lamp = BukkitLamp.builder(this).build();
        lamp.register(new PixCommand(paymentController), new ShopCommand(frame), new DiscountCommand(discountController));


        new PaymentCheckTask(this, paymentService).runTaskTimerAsynchronously(
                this,
                20 * 10,
                20 * 10
        );

        getLogger().info("Plugin VirtualPix habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (mongoController != null) {
            mongoController.close();
        }
        getLogger().info("Plugin VirtualPix desabilitado.");
    }
}