package interfaces;

import domain.dto.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findByName(String name);
    List<Product> getAllProducts();
}