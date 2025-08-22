package discount;

import dto.Product;

public class BuyXItemGetYitem implements IOffer {
    private final int paidItemsCount;
    private final int freeItemsCount;

    public BuyXItemGetYitem(int paidItemsCount, int freeItemsCount) {
        this.paidItemsCount = paidItemsCount;
        this.freeItemsCount = freeItemsCount;
    }

    @Override
    public void applyOffer(Product product) {
        if (isDiscountApplicable(product)) {
            double discountAmount = calculateDiscountAmount(product);
            product.setTotalPrice(product.getTotalPrice() - discountAmount);
        }
    }

    private boolean isDiscountApplicable(Product product) {
        return product.getQuantity() >= paidItemsCount + freeItemsCount;
    }

    private double calculateDiscountAmount(Product product) {
        if (product == null || product.getQuantity() == 0) {
            return 0;
        }
        int freeItems = product.getQuantity() / (paidItemsCount + freeItemsCount);
        double unitPrice = product.getTotalPrice() / product.getQuantity();
        return unitPrice * freeItems;
    }
}