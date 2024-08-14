package etu.nic.store.controller;

import etu.nic.store.model.Product;
import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor

public class ProductController {
    private final ProductService productsService;

    @GetMapping()
    public ResponseEntity<List<Product>> findAllProducts() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productsService.findAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productsService.findProductById(id));
    }

    @PostMapping()
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productsService.saveProduct(product));
    }

    @PutMapping()
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productsService.updateProduct(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable long id) {
        productsService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
