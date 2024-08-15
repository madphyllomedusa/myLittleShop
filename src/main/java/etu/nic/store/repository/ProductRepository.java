package etu.nic.store.repository;

import etu.nic.store.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByDeletedFalse();
    Optional<Product> findByIdAndDeletedFalse(Long id);
}
