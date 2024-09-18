package etu.nic.store.controller;

import etu.nic.store.model.dto.BucketDto;
import etu.nic.store.service.BucketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bucket")
@AllArgsConstructor
public class BucketController {
    private final BucketService bucketService;

    @PostMapping("/add/{userId}/{productId}")
    public ResponseEntity<BucketDto> addProductToBucket(@PathVariable Long userId, @PathVariable Long productId, @RequestParam int quantity) {
        BucketDto bucketDto = bucketService.addProductToBucket(userId, productId, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(bucketDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BucketDto> getBucket(@PathVariable Long userId) {
        BucketDto bucketDto = bucketService.getBucketByUserId(userId);
        return ResponseEntity.ok(bucketDto);
    }

    @PostMapping("/update")
    public ResponseEntity<BucketDto> updateBucket(@RequestBody BucketDto bucketDto) {
        BucketDto updatedBucket = bucketService.updateBucket(bucketDto);
        return ResponseEntity.ok(updatedBucket);
    }

}
