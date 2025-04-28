package org.community.virtualPix.controllers;

import lombok.Getter;
import lombok.Setter;
import org.community.virtualPix.models.Discount;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class DiscountController {

    private final Set<Discount> discounts = new HashSet<>();

    public void addDiscount(Discount discount) {
        discounts.add(discount);
    }
    public void removeDiscount(Discount discount) {
        discounts.remove(discount);
    }

    public Discount getByName(String name) {
        for (Discount discount : discounts) {
            if(discount.getId().equalsIgnoreCase(name)) {
                return discount;
            }
        }
        return null;
    }

}
