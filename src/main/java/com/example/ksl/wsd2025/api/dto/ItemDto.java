package com.example.ksl.wsd2025.api.dto;

import com.example.ksl.wsd2025.api.enums.Rarity;

public class ItemDto {
    private Long id;
    private String name;
    private Integer price;
    private String description;
    private Rarity itemRarity;
    private Integer stock_quantity;

    public ItemDto() {

    }

    public ItemDto(Long id, String name, Integer price, String description, Rarity itemRarity, Integer stock_quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.itemRarity = itemRarity;
        this.stock_quantity = stock_quantity;
    }

    // getter / setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public Rarity getItemRarity() { return itemRarity; }
    public void setItemRarity(Rarity itemRarity) { this.itemRarity = itemRarity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStock_quantity() { return stock_quantity;}
    public void setStock_quantity(Integer stock_quantity) { this.stock_quantity = stock_quantity; }
}

