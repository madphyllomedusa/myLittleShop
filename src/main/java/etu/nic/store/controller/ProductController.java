package etu.nic.store.controller;

import etu.nic.store.model.Product;
import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor

public class ProductController {
    private final ProductService productsService;

    @GetMapping()
    public List<Product> findAllProducts() {
        return productsService.findAllProducts();
    }

    @PostMapping()
    public Product saveProduct(@RequestBody Product products) {
        return productsService.saveProduct(products);
    }

    @GetMapping("/{id}")
    public Product findProductById(@PathVariable long id) {
        return productsService.findProductById(id);
    }

    @PutMapping()
    public Product updateProduct(Product products) {
        return productsService.updateProduct(products);
    }

    @DeleteMapping()
    public void deleteProductById(@PathVariable long id) {
        productsService.deleteProduct(id);
    }
}
