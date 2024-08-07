package etu.nic.store.repository;

import etu.nic.store.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


@Repository
public class InMemoryProductDAO {
    private final List< Product> PRODUCTS = new ArrayList<>();

    public List< Product> findAllProducts(){
        return PRODUCTS;
    };

    public Product findProductById(long id){
        return PRODUCTS.stream()
                .filter(product -> product.getId() == id)
                .findFirst()
                .orElse(null);
    };

    public Product saveProduct(Product product){
        PRODUCTS.add(product);
        return product;
    };
    public Product updateProduct(Product product){
        var productIndex = IntStream.range(0, PRODUCTS.size())
                .filter(index -> PRODUCTS.get(index).equals(product.getId()))
                .findFirst()
                .orElse(-1);
        if(productIndex > -1){
            PRODUCTS.set(productIndex, product);
            return product;
        }
        return null;
    };

    public void deleteProduct(long id){
        var product = findProductById(id);
        if (product != null) {
            PRODUCTS.remove(id);
        }
    };

}
