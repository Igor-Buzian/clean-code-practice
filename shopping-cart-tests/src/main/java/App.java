import cart.ShoppingCart;
import discount.BuyXItemGetYitem;
import infrastructure.DatabaseConnectionManager;
import infrastructure.PostgresProductRepository;
import interfaces.ProductRepository;
import domain.dto.Product;
import infrastructure.FileProductRepository;

import java.util.List;
import java.util.Optional;


public class App {
    public static void main(String[] args) {
        FileProductRepository fileRepository = new FileProductRepository("products.csv");

        DatabaseConnectionManager connectionManager = new DatabaseConnectionManager("jdbc:postgresql://localhost:5432/clean_code", "postgres", "postgres");
        PostgresProductRepository repository = new PostgresProductRepository(connectionManager);

        repository.importFromFile("products.csv");

        Product shampooProduct = new Product("Shampoo",3,30.0);
        ShoppingCart shoppingCart = new ShoppingCart(repository);


        shoppingCart.setOffer(new BuyXItemGetYitem(2,1));
        shoppingCart.addProduct(shampooProduct);
        shoppingCart.addAllProductRepositoryProducts();


        System.out.println("Shopping cart: "+shoppingCart.getProductCount());
        Optional<Product> shampoo = shoppingCart.getProductByName("Shampoo");
        System.out.println("Shampoo cost: "+shampoo.get().getTotalPrice());

        List<Product> allProducts = repository.getAllProducts();

        allProducts.sort((p1, p2) -> Double.compare(p2.getRank(), p1.getRank()));

        allProducts.forEach(product -> System.out.println(product.getProductName() + " Rank: " + product.getRank()));
    }
}