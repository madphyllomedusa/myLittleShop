package etu.nic.store.controller;

import etu.nic.store.model.dto.ProductDto;
import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:5173")  // разрешаем запросы с фронтенда
@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/")
    public ResponseEntity<List<ProductDto>> findAllProducts() {
        List<ProductDto> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findProductById(@PathVariable long id) {
        ProductDto product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping()
    public ResponseEntity<ProductDto> saveProduct( @RequestBody ProductDto productDTO) {
        ProductDto savedProduct = productService.saveProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable long id, @RequestBody ProductDto productDTO) {
        ProductDto updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> getProductsByCategoryId(@PathVariable Long categoryId) {
        logger.info("Received request to fetch products for category: {}", categoryId);
        List<ProductDto> products = productService.getProductsByCategoryId(categoryId);
        if (products.isEmpty()) {
            logger.warn("No products found for category: {}", categoryId);
        }
        return ResponseEntity.ok(products);
    }

}
