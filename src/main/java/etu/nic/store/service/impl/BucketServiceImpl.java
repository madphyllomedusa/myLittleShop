package etu.nic.store.service.impl;

import etu.nic.store.dao.BucketDao;
import etu.nic.store.dao.ProductDao;
import etu.nic.store.model.dto.BucketDto;
import etu.nic.store.model.mappers.BucketMapper;
import etu.nic.store.model.pojo.Bucket;
import etu.nic.store.model.pojo.BucketItem;
import etu.nic.store.model.pojo.Product;
import etu.nic.store.service.BucketService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BucketServiceImpl implements BucketService {
    private static final Logger logger = LoggerFactory.getLogger(BucketServiceImpl.class);


    private final BucketDao bucketDao;
    private final ProductDao productDao;
    private final BucketMapper bucketMapper;

    @Override
    @Transactional
    public BucketDto addProductToBucket(Long userId, Long productId, Integer quantity) {
        logger.info("Adding product to bucket. UserId = {}, productId = {}", userId, productId);
        Bucket bucket = bucketDao.findByUserId(userId).orElseGet(() -> {
            Bucket newBucket = new Bucket();
            newBucket.setUserId(userId);
            newBucket.setTotalCost(BigDecimal.ZERO);
            newBucket.setItems(new ArrayList<>());
            return bucketDao.save(newBucket);
        });

        if (bucket.getItems() == null) {
            bucket.setItems(new ArrayList<>());
        }

        addProductToBucketLogic(bucket, productId, quantity);
        logger.info("Bucket added successfully {}. UserId = {}, productId = {}" ,bucket, userId, productId);
        bucket.calculateTotalCost();
        logger.info("Bucket calculated total cost is {}", bucket.getTotalCost());
        bucketDao.update(bucket);

        return bucketMapper.toDto(bucket);
    }

    @Override
    public BucketDto getBucketByUserId(Long userId) {
        Optional<Bucket> bucketOptional = bucketDao.findByUserId(userId);
        if (bucketOptional.isEmpty()) {
            logger.error("Bucket not found with userId: {}", userId);
            throw new IllegalArgumentException("Корзина для пользователя с ID " + userId + " не найдена.");
        }
        return bucketMapper.toDto(bucketOptional.get());
    }


    @Override
    @Transactional
    public void deleteProductFromBucket(Long productId) {
        logger.info("Delete product from bucket with id {}", productId);
        bucketDao.deleteItemFromBucket(productId);
        logger.info("Product successfully deleted {}", productId);
    }

    @Override
    @Transactional
    public BucketDto updateBucket(BucketDto bucketDto) {
        logger.info("Updating bucket with id {}, and bucket: {}", bucketDto.getId(), bucketDto);
        Bucket bucket = bucketMapper.toEntity(bucketDto);
        for (BucketItem item : bucket.getItems()) {
            if (item.getId() == null) {
                bucketDao.addItemToBucket(item);
            } else {
                bucketDao.updateItemInBucket(item);
            }
        }

        bucket.calculateTotalCost();
        bucketDao.update(bucket);
        logger.info("Bucket successfully updated {}", bucketDto.getId());
        logger.info("Bucket after update: {}", bucket);
        return bucketMapper.toDto(bucket);
    }

    private void addProductToBucketLogic(Bucket bucket, Long productId, int quantity) {
        Product product = productDao.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        bucket.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        item -> {
                            item.setQuantity(item.getQuantity() + quantity);
                            item.setItemTotalCost(product.getPrice()
                                    .multiply(BigDecimal.valueOf(item.getQuantity())));
                        },
                        () -> {
                            BucketItem newItem = new BucketItem(null,
                                    productId,
                                    bucket.getId(),
                                    quantity,
                                    product.getPrice()
                                    .multiply(BigDecimal.valueOf(quantity)));
                            bucketDao.addItemToBucket(newItem);
                            bucket.getItems().add(newItem);
                        }
                );
    }

}
