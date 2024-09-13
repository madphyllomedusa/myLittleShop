package etu.nic.store.dao;

import etu.nic.store.model.pojo.Bucket;
import etu.nic.store.model.pojo.BucketItem;

import java.util.Optional;

public interface BucketDao {
    Optional<Bucket> findByUserId(Long userId);
    Bucket save(Bucket bucket);
    void update(Bucket bucket);
    void addItemToBucket(BucketItem item);
    void updateItemInBucket(BucketItem item);
    void deleteItemFromBucket(Long itemId);
    void clearBucket(Long bucketId);
}
