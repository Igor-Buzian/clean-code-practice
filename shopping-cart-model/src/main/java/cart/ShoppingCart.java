package cart;

import discount.IOffer;
import dto.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShoppingCart {

    private final List<Product> productList = new ArrayList<>();
    private IOffer offer;

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

    public void addProduct(Product product) {
        if (product == null || product.getQuantity() <= 0 || product.getProductName() == null || product.getProductName().isEmpty()) {
            throw new IllegalArgumentException("Invalid product details");
        }

        if (offer != null) {
            offer.applyOffer(product);
        }

        productList.add(product);
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
}