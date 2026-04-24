package com.boostmytool.beststore.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ProductDto {
    @NotEmpty(message = "El nombre del producto es requerido")
    private String name;

    @NotEmpty(message = "El nombre de la marca es requerido")
    private String brand;

    @NotEmpty(message = "El nombre de la categoría es requerido")
    private String category;

    @Min(0)
    private double price;

    @Size(min = 10, message = "La descripción debe tener al menos 10 caracteres")
    @Size(max = 2000, message = "La descripción no puede tener más de 2000 caracteres")
    private String description;

    private MultipartFile imageFile;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
