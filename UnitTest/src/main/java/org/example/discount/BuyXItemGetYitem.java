package org.example.discount;

import org.example.Product;

public class BuyXItemGetYitem implements IOffer {
    private int payItem;
    private  int freeItem;

    public BuyXItemGetYitem(int payItem, int freeItem) {
        this.payItem = payItem;
        this.freeItem = freeItem;
    }

    @Override
    public void applyOffer(Product product) {
        if(product.getQuantity()>payItem){
            int free = product.getQuantity() / (payItem+freeItem);
            double productPrice = product.getTotalPrice() / product.getQuantity();
            double discount = productPrice * free;
            product.setTotalPrice(product.getTotalPrice()-discount);
        }
    }
}
