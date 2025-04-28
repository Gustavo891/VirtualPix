package org.community.virtualPix.models;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Discount {

    private final String id;
    private final double discount;
    private int usages;
    private final boolean lifetime;

    public boolean removeUsage() {
        if(lifetime) return true;
        if(usages >= 1) {
            usages--;
            return true;
        } else {
            return false;
        }
    }

    public void increaseUsage(int amount) {
        usages =+ amount;
    }

}
