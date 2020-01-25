package me.towaha.chequeslite.Classes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemStackBuilder {
    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemStackBuilder(Material material) {
        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemStackBuilder displayName(String displayName) {
        itemMeta.setDisplayName(displayName);
        return this;
    }

    public ItemStackBuilder lore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }

    public NBTItemStack NBTBuild() {
        NBTItemStack clonedStack = new NBTItemStack(itemStack.clone());
        clonedStack.setItemMeta(itemMeta.clone());
        return clonedStack;
    }
}