package etu.nic.store.service.Impl;

import etu.nic.store.model.Product;
import etu.nic.store.repository.ProductRepository;
import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;


@Service
@AllArgsConstructor
@Primary
public class ProductServiceDBImpl implements ProductService {
    private static Logger logger = Logger.getLogger(ProductServiceDBImpl.class.getName());

    private final ProductRepository productRepository;

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product findProductById(long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        logger.info("Product saved: " + product.toString());
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        logger.info("Product updated: " + product.toString());
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(long id) {

        productRepository.deleteById(id);
    }
}
