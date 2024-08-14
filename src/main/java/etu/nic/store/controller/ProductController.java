package etu.nic.store.controller;

import etu.nic.store.model.Product;
import etu.nic.store.model.dto.ProductCreateDTO;
import etu.nic.store.model.dto.ProductDTO;
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
    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<List<ProductDTO>> findAllProducts() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.findAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findProductById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.findProductById(id));
    }

    @PostMapping()
    public ResponseEntity<ProductDTO> saveProduct(@RequestBody ProductCreateDTO productCreateDTO) {
        ProductDTO savedProduct = productService.saveProduct(productCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping()
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable long id, @RequestBody ProductCreateDTO productCreateDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productCreateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}