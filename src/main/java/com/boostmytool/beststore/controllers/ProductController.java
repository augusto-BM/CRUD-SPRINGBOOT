package com.boostmytool.beststore.controllers;

import com.boostmytool.beststore.models.Product;
import com.boostmytool.beststore.models.ProductDto;
import com.boostmytool.beststore.services.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path; // CORRECTO
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductsRepository repo;

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result,
            Model model
    ) {

        if (productDto.getImageFile() == null || productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "La imagen es requerida"));
        }

        if (result.hasErrors()) {
            return "products/createProduct";
        }

        // Guardar imagen
        MultipartFile image = productDto.getImageFile();
        String storageFileName = new Date().getTime() + "_" + image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception ex) {
            System.out.println("Error al guardar imagen: " + ex.getMessage());
        }

        // Crear entidad Product
        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setImageFileName(storageFileName);
        product.setCreatedAt(new Date());

        // Guardar en BD
        repo.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(@RequestParam int id, Model model) {

        Product product = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        ProductDto productDto = new ProductDto();
        productDto.setName(product.getName());
        productDto.setBrand(product.getBrand());
        productDto.setCategory(product.getCategory());
        productDto.setPrice(product.getPrice());
        productDto.setDescription(product.getDescription());

        model.addAttribute("product", product);
        model.addAttribute("productDto", productDto);

        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(
            @RequestParam int id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result
    ) {

        Product product = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (result.hasErrors()) {
            return "products/editProduct";
        }

        // Si hay nueva imagen
        if (!productDto.getImageFile().isEmpty()) {
            try {
                String uploadDir = "public/images/";
                String fileName = new Date().getTime() + "_" +
                        productDto.getImageFile().getOriginalFilename();

                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = productDto.getImageFile().getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(fileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImageFileName(fileName);

            } catch (Exception ex) {
                System.out.println("Error imagen: " + ex.getMessage());
            }
        }

        // actualizar datos
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());

        repo.save(product);

        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {

        Product product = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // borrar imagen (opcional pero recomendado)
        try {
            Path imagePath = Paths.get("public/images/" + product.getImageFileName());
            Files.deleteIfExists(imagePath);
        } catch (Exception ex) {
            System.out.println("Error borrando imagen: " + ex.getMessage());
        }

        repo.delete(product);

        return "redirect:/products";
    }
}