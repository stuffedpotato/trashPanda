package com.trashpanda;


/*
 * This is the Item class that creates an item with basic properties to be used by ShareListEntry & WantListEntry.
 */

 public class Item {
    private String name;
    private ItemCategory category;
    private ItemQuantityType qtyType;

    public Item(String name, ItemCategory category, ItemQuantityType qtyType) {
        this.name = normalize(name);
        this.category = category;
        this.qtyType = qtyType;
    }

    private String normalize(String name) {
        if (name == null || name.isEmpty()) 
            return "";

        name = name.trim().toLowerCase();

        if (name.endsWith("es")) {
            return name.substring(0, name.length() - 2);
        } else if (name.endsWith("s")) {
            return name.substring(0, name.length() - 1);
        }

        return name;
    }

    // Getters

    public String getName() {
        return name;
    }

    public ItemCategory getItemCategory() {
        return category;
    }

    public ItemQuantityType getQtyType() {
        return qtyType;
    }

    // SETTERS

    public void setQtyType(ItemQuantityType qtyType) {
        this.qtyType = qtyType;
    }
}
