package org.community.virtualPix.util;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int amount, int data) {
        this.itemStack = new ItemStack(material, amount, (byte) data);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack otherItem) {
        this.itemStack = otherItem.clone();
        this.itemMeta = itemStack.getItemMeta();
    }


    public ItemBuilder material(Material material) {
        return acceptItemStack(itemStack -> itemStack.setType(material));
    }

    public ItemBuilder name(String name) {
        return acceptItemMeta(itemMeta -> itemMeta.setDisplayName(name));
    }

    public ItemBuilder nameRgb(String name) {
        return acceptItemMeta(itemMeta -> itemMeta.displayName(MiniMessage.miniMessage().deserialize(name).decoration(TextDecoration.ITALIC, false)));
    }

    public ItemBuilder amount(int amount) {
        return acceptItemStack(itemStack -> itemStack.setAmount(amount));
    }

    public ItemBuilder durability(int durability) {
        return acceptItemStack(itemStack -> itemStack.setDurability((short) durability));
    }

    public ItemBuilder lore(String... lore) {
        return acceptItemMeta(itemMeta -> itemMeta.setLore(Arrays.asList(lore)));
    }

    public ItemBuilder lore(List<String> lore) {
        return acceptItemMeta(itemMeta -> itemMeta.setLore(lore));
    }

    public ItemBuilder loreRgb(List<Component> lore) {
        return acceptItemMeta(itemMeta -> itemMeta.lore(lore));
    }

    public ItemBuilder addLoreLine(String... line) {
        final List<String> lore = Optional.ofNullable(itemMeta.getLore()).orElse(Lists.newArrayList());

        lore.addAll(Arrays.asList(line));
        return acceptItemMeta(itemMeta -> itemMeta.setLore(lore));
    }

    public List<String> lore() {
        return itemMeta.getLore();
    }

    public void clearLore() {
        itemMeta.setLore(null);
    }

    public ItemBuilder addLoreLineIf(boolean condition, String... line) {
        if (condition) addLoreLine(line);
        return this;
    }

    public ItemBuilder setColorIf(boolean condition, Color color) {
        if (condition) setColor(color);
        return this;
    }

    public ItemBuilder setColor(Color color) {
        final LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
        leatherArmorMeta.setColor(color);

        return acceptItemStack(itemStack -> itemStack.setItemMeta(leatherArmorMeta));
    }

    public ItemBuilder replace(String placeholder, String replace) {
        if (itemMeta.hasDisplayName()) name(itemMeta.getDisplayName().replace(placeholder, replace));

        if (itemMeta.hasLore()) {
            final List<String> lore = itemMeta.getLore();

            lore.replaceAll($ -> $.replace(placeholder, replace));
            lore(lore);
        }

        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        return acceptItemMeta(itemMeta -> itemMeta.addEnchant(enchantment, level, true));
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        return acceptItemMeta(itemMeta -> itemMeta.addItemFlags(flags));
    }

    public ItemBuilder hideEnchantments() {
        return addFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemBuilder hideAttributes() {
        return addFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    public ItemBuilder hideAll() {
        return addFlags(ItemFlag.values());
    }

    public ItemStack build() {
        replace("&", "§");

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private ItemBuilder acceptItemStack(Consumer<ItemStack> consumer) {
        consumer.accept(itemStack);
        return this;
    }

    private ItemBuilder acceptItemMeta(Consumer<ItemMeta> consumer) {
        consumer.accept(itemMeta);
        return this;
    }

    public ItemBuilder glowing() {
        enchantment(Enchantment.PROTECTION, 1);
        return hideAttributes();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}