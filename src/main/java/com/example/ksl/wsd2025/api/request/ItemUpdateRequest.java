package com.example.ksl.wsd2025.api.request;

import com.example.ksl.wsd2025.api.enums.Rarity;

public class ItemUpdateRequest {
    private String name;
    private Integer price;
    private String description;
    private Rarity itemRarity;
    private Integer stock_quantity;

    // getter / setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Rarity getItemRarity() { return itemRarity; }
    public void setItemRarity(Rarity itemRarity) { this.itemRarity = itemRarity; }

    public Integer getStock_quantity() { return stock_quantity; }
    public void setStock_quantity(Integer stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

}