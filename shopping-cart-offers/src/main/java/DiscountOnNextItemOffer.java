import discount.IOffer;
import dto.Product;

public class DiscountOnNextItemOffer implements IOffer {
    int discountPercent;

    public DiscountOnNextItemOffer(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Override
    public void applyOffer(Product product) {
        double oneProductPrice = product.getTotalPrice() / product.getQuantity();
        int totalQuantity = product.getQuantity();
        while (totalQuantity>1)
        {
            double discountAmount = oneProductPrice * discountPercent / 100.0;
            product.setTotalPrice(product.getTotalPrice()-discountAmount);
            totalQuantity-=2;
        }
    }
}
