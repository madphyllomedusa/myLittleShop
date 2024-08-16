package etu.nic.store.service.impl;

import etu.nic.store.exceptionhandler.BadRequestException;
import etu.nic.store.exceptionhandler.InternalServerErrorException;
import etu.nic.store.exceptionhandler.NotFoundException;
import etu.nic.store.model.dto.ProductDTO;
import etu.nic.store.model.entity.Product;
import etu.nic.store.model.mappers.ProductMapper;
import etu.nic.store.repository.ProductRepository;
import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Primary
public class ProductServiceDBImpl implements ProductService {
    private static Logger logger = LoggerFactory.getLogger(ProductServiceDBImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDTO> findAllProducts() {
        List<Product> products = productRepository.findAllByDeletedTimeIsNull();

        if (products.isEmpty()) {
            logger.warn("Product list is empty");
            throw new NotFoundException("Product list is empty");
        }
        return products.stream()
                .map(productMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO findProductById(long id) {
        if (id <= 0) {
            logger.error("Invalid product ID: {}", id);
            throw new BadRequestException("Invalid product ID");
        }
        return productRepository.findByIdAndDeletedTimeIsNull(id)
                .map(productMapper.INSTANCE::toDto)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {

        if (productDTO.getPrice().intValue() <= 0) {
            logger.error("Input price unavailable : {}", productDTO.getPrice());
            throw new BadRequestException("Price must be greater than null");
        }

        try {
            logger.info("Received: {}. Entity: {}" , productDTO,productMapper.toEntity(productDTO));
            Product savedProduct = productRepository.save(productMapper.toEntity(productDTO));
            savedProduct.setDeletedTime(null);
            logger.info("Product saved: {}", savedProduct.toString());
            return productMapper.toDto(savedProduct);
        } catch (Exception e) {
            logger.error("Failed to save product:{}", e.getMessage());
            throw new BadRequestException("Failed to save product");
        }
    }



    @Override
    public ProductDTO updateProduct(long id, ProductDTO productCreateDTO) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found");
        }
        try {
            Product product = productMapper.toEntity(productCreateDTO);
            product.setId(id);
            Product updatedProduct = productRepository.save(product);
            logger.info("Product updated: {}", updatedProduct.toString());
            return productMapper.toDto(updatedProduct);
        } catch (Exception e) {
            logger.error("Failed to update product: {}" , e.getMessage());
            throw new InternalServerErrorException("Failed to update product");
        }
    }


    @Override
    public void deleteProduct(long id) {
        if (id <= 0) {
            logger.error("Invalid product ID: {}", id);
            throw new BadRequestException("Invalid product ID");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getDeletedTime() != null && productRepository.existsById(id) ) {
            logger.warn("Product is already deleted, deleted time: {}", product.getDeletedTime());
            throw new NotFoundException("Product is already deleted");
        }

        product.setDeletedTime(LocalDateTime.now());
        productRepository.save(product);
        logger.info("Product softly deleted: {}" , product.toString());
    }

}
