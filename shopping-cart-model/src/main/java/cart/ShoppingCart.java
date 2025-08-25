package cart;

import interfaces.ProductRepository;
import domain.discount.IOffer;
import domain.dto.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShoppingCart {

    private final ProductRepository productRepository;
    private final List<Product> productList = new ArrayList<>();
    private IOffer offer;

    public ShoppingCart(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public int getProductCount() {
        return productList.size();
    }

    public double totalCartValue() {
        double totalValue = 0;
        for (Product product : productList) {
            totalValue += product.getTotalPrice();
        }
        return totalValue;
    }

    public void addAllProductRepositoryProducts() {
        if (productRepository != null) {
            List<Product> allRepoProducts = productRepository.getAllProducts();

            for (Product repoProduct : allRepoProducts) {
                Optional<Product> existingCartProduct = this.getProductByName(repoProduct.getProductName());

                if (existingCartProduct.isPresent()) {
                    Product cartProduct = existingCartProduct.get();

                    // count new product discount
                    useOffer(repoProduct);

                    cartProduct.setQuantity(cartProduct.getQuantity() + repoProduct.getQuantity());
                    cartProduct.setTotalPrice(cartProduct.getTotalPrice() + repoProduct.getTotalPrice());
                }
                    else {
                    this.addProduct(repoProduct);
                }
            }
        }
    }


    public void addProduct(Product product) {
        if (product.getQuantity() > 0 && product.getProductName() != null && !product.getProductName().isEmpty()) {
            useOffer(product);
            productList.add(product);
        } else {
            throw new IllegalArgumentException("Invalid product details");
        }
    }

    public Optional<Product> getProductByName(String name) {
        for (Product product : productList) {
            if (product.getProductName().equals(name)) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    public void setOffer(IOffer offer) {
        this.offer = offer;
    }

    private void useOffer(Product product) {

        if (offer != null) {
            offer.applyOffer(product);
        }
    }
}