
import discount.IOffer;
import dto.Product;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    List<Product> productList = new ArrayList<>();
    private double totalCartValue;
    private IOffer offer;

    public int getProductCount() {
        return productList.size();
    }

    public double totalCartValue()
    {

        if(productList.size()>0){
            for(Product product : productList){
                totalCartValue += product.getTotalPrice();
            }
            return totalCartValue;
        }
        return 0;
    }

    public void addProduct(Product product){
      if(product.getQuantity()>0 && product.getProductName()!=null && !product.getProductName().isEmpty())
      {
          if(offer!=null){
              offer.applyOffer(product);
          }
          productList.add(product);
      }
      else
      {
          throw new IllegalArgumentException("Empty Product");
      }
    }

    public Product getProductByName(String name) {
        for (Product product : productList) {
            if (product.getProductName().equals(name)) {
                return product;
            }
        }
        throw new IllegalArgumentException("Product not found!");
    }


    public void setOffer(IOffer offer) {
        this.offer = offer;
    }

}
