package utils;

import domain.dto.Product;

import java.util.List;

public class ProductsByRank {

    public void sortProductsByRank(List<Product> products) {
        products.sort((p1, p2) -> Double.compare(p2.getRank(), p1.getRank()));
    }
}
