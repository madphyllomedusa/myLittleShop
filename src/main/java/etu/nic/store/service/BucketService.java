package etu.nic.store.service;

import etu.nic.store.model.dto.BucketDto;

public interface BucketService {
    BucketDto addProductToBucket(Long userId, Long productId, Integer quantity);
    BucketDto getBucketByUserId(Long userId);
    void deleteProductFromBucket(Long productId);

    BucketDto updateBucket(BucketDto bucketDto);
}