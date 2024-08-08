package etu.nic.store.service.Impl;

import etu.nic.store.model.Product;
import etu.nic.store.repository.InMemoryProductDAO;
import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor

public class InMemoryProductServiceImpl implements ProductService {
    private final InMemoryProductDAO repository;

    @Override
    public List<Product> findAllProducts(){
        return repository.findAllProducts();
    }

    @Override
    public Product findProductById(long id) {
        return repository.findProductById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        return repository.saveProduct(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return repository.saveProduct(product);
    }

    @Override
    public void deleteProduct(long id) {
        repository.deleteProduct(id);
    }
}
