package org.community.virtualPix.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Setter @Getter
@RequiredArgsConstructor
public class Cart {

    private final Player player;
    private Map<Product, Integer> products = new HashMap<>();
    @Setter
    private Discount discount;

    public void increaseProduct(Product product) {
        products.put(product, products.getOrDefault(product, 0) + 1);
    }

    public void decreaseProduct(Product product) {
        if(products.containsKey(product) && products.get(product) >= 1) {
            products.put(product, products.get(product) - 1);
        }
        if(products.get(product) <= 0) {
            products.remove(product);
        }
    }

    public void clearProduct(Product product) {
        products.remove(product);
    }

    public void clearProducts() {
        products.clear();
    }

    public double getCost() {
        double price = 0;
        for(Map.Entry<Product, Integer> entry : products.entrySet()) {
            price += (entry.getValue() * entry.getKey().getValue());
        }
        return price;
    }

    public double getDiscountCost() {
        double cost = getCost();
        double discountValue = getDiscount().getDiscount();
        return cost * ((100 - discountValue)/100.0);
    }

}
