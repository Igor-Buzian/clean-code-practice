package discount;

import domain.discount.IOffer;
import domain.dto.Product;

public class DiscountOnNextItemOffer implements IOffer {

    private final double discountRate;

    public DiscountOnNextItemOffer(int discountPercent) {
        if (isDiscountValid(discountPercent)) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100");
        }
        this.discountRate = discountPercent / 100.0;
    }

    @Override
    public void applyOffer(Product product) {
        if (product.getQuantity() > 1) {
            double discountAmount = calculateDiscountAmount(product);
            product.setTotalPrice(product.getTotalPrice() - discountAmount);
        }
    }

    private double calculateDiscountAmount(Product product) {
        int pairsCount = product.getQuantity() / 2;
        double unitPrice = product.getTotalPrice() / product.getQuantity();
        return (pairsCount * unitPrice) * this.discountRate;
    }

    private  boolean isDiscountValid(int discountPercent){
       return discountPercent < 0 || discountPercent > 100;
    }
}