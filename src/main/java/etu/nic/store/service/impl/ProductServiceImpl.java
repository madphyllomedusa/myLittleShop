package etu.nic.store.service.impl;

import etu.nic.store.dao.impl.ProductDAOImpl;
import etu.nic.store.exceptionhandler.BadRequestException;
import etu.nic.store.exceptionhandler.InternalServerErrorException;
import etu.nic.store.exceptionhandler.NotFoundException;
import etu.nic.store.model.dto.ProductDTO;

import etu.nic.store.model.entity.Product;
import etu.nic.store.model.mappers.ProductMapper;

import etu.nic.store.service.ProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductDAOImpl productDAO;
    private final ProductMapper productMapper;


    @Override
    public List<ProductDTO> findAllProducts() {
        List<Product> products = productDAO.findAllProducts();
        if (products.isEmpty()) {
            logger.warn("Product list is empty");
            throw new NotFoundException("Product list is empty");
        }
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
        public ProductDTO findProductById(Long id) {
            if (id <= 0) {
                logger.error("Invalid product ID: {}", id);
                throw new BadRequestException("Invalid product ID");
            }
            return productDAO.findProductById(id)
                    .map(productMapper::toDTO)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
        }
    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
            if (productDTO.getPrice().intValue() <= 0) {
                logger.error("Input price unavailable: {}", productDTO.getPrice());
                throw new BadRequestException("Price must be greater than zero");
            }
            logger.info("Received from client {}", productDTO);
            Product product = productMapper.toEntity(productDTO);
            product.setDeletedTime(null); // Не забываем обнулить время удаления

            Product savedProduct = productDAO.save(product);
            logger.info("Product saved: {}", savedProduct);
            return productMapper.toDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        if (productDAO.findProductById(id).isEmpty()) {
            throw new NotFoundException("Product not found");
        }

        try {
            Product product = productMapper.toEntity(productDTO);
            product.setId(id);
            Product updatedProduct = productDAO.update(product);
            logger.info("Product updated: {}", updatedProduct);
            return productMapper.toDTO(updatedProduct);
        } catch (Exception e) {
            logger.error("Failed to update product: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to update product");
        }
    }



    @Override
    public void deleteProduct(Long id) {
        if (id <= 0) {
            logger.error("Invalid product ID: {}", id);
            throw new BadRequestException("Invalid product ID");
        }

        Product product = productDAO.findProductById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (product.getDeletedTime() != null && productDAO.findProductByIdAndDeleted(id).isPresent()) {
            logger.warn("Product is already deleted, deleted time: {}", product.getDeletedTime());
            throw new NotFoundException("Product is already deleted");
        }

        productDAO.deleteById(id);
        logger.info("Product softly deleted with ID: {}", id);
    }

}
