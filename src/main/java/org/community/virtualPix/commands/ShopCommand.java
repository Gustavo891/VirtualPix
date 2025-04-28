package org.community.virtualPix.commands;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.ViewFrame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.community.virtualPix.view.ShopView;
import revxrsal.commands.annotation.Command;

import javax.swing.text.ViewFactory;

@RequiredArgsConstructor
public class ShopCommand {

    private final ViewFrame frame;

    @Command("shop")
    public void shop(CommandSender sender) {
        if(!(sender instanceof Player player)) return;

        frame.open(ShopView.class, player);
    }

}
