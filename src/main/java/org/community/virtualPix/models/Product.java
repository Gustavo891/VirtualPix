package org.community.virtualPix.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter @Setter
@RequiredArgsConstructor
public class Product {

    private final String name;
    private final Material item;
    private final String[] description;
    private final double value;
    private final List<String> commands;

}
