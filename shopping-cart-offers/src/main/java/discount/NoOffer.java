package discount;

import domain.discount.IOffer;
import domain.dto.Product;

public class NoOffer implements IOffer {
    @Override
    public void applyOffer(Product product) {
    }
}
