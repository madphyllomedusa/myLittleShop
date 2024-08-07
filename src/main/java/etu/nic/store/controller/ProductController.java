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

    @PostMapping("save_product")
    public Product saveProduct(@RequestBody Product products) {
        return productsService.saveProduct(products);
    }

    @GetMapping("/{id}")
    public Product findProductById(@PathVariable long id) {
        return productsService.findProductById(id);
    }

    @PutMapping("update_product")
    public Product updateProduct(Product products) {
        return productsService.updateProduct(products);
    }

    @DeleteMapping("delete_product/{id}")
    public void deleteProductById(@PathVariable long id) {
        productsService.deleteProduct(id);
    }
}
