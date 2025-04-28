package org.community.virtualPix.controllers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.community.virtualPix.models.Product;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ProductController {

    private final Product cashProduct1 = new Product("§6✪500 Cash", Material.GOLD_NUGGET, null, 10.00, List.of(""));
    private final Product cashProduct2 = new Product("§6✪1.000 Cash", Material.GOLD_INGOT, null, 18.00, List.of(""));

}
