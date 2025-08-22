import org.example.*;
import org.example.discount.BuyXItemGetYitem;
import org.example.discount.DiscountOnNextItemOffer;
import org.example.discount.IOffer;
import org.example.discount.NoOffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartAppTest {

    @Test
    public void createShoppingCartEmpty() {
        ShoppingCart shoppingCart = new ShoppingCart();
        assertEquals(shoppingCart.getProductCount(), 0);
    }

    @Test
    public void addSingleProduct() {
        ShoppingCart shoppingCart = new ShoppingCart();
        Product product = new Product("Avocado", 1, 52.1);
        shoppingCart.addProduct(product);
        assertEquals(shoppingCart.getProductCount(), 1);
        assertEquals(shoppingCart.totalCartValue(), 52.1);
    }

    @Test
    public void addSomeProducts() {
        ShoppingCart shoppingCart = new ShoppingCart();
        Product gatsByCream = new Product("Gatsby hair cream", 1, 30);
        Product bvlgiriSoap = new Product("Bvlgiri Soap", 1, 100);
        shoppingCart.addProduct(gatsByCream);
        shoppingCart.addProduct(bvlgiriSoap);
        assertEquals(shoppingCart.getProductCount(), 2);
        assertEquals(shoppingCart.totalCartValue(), 130);
    }

    @Test
    public void testAddMultipleQuantityOfAProductAndApplyOfferToCart() {
        IOffer offer = new BuyXItemGetYitem(2, 1);
        ShoppingCart shoppingCart = new ShoppingCart();
        Product product = new Product("Gatsby hair cream", 5, 150.0);
        shoppingCart.setOffer(offer);
        shoppingCart.addProduct(product);
        assertEquals(shoppingCart.totalCartValue(), 120);
        assertEquals(shoppingCart.getProductCount(), 1);
    }

    @Test
    public void addDifferentProductsAndAppyOfferToTheCart() {
        ShoppingCart cart = new ShoppingCart();
        Product gatsByCream = new Product("Gatsby hair cream", 3, 90.0);
        Product bvlgiriSoap = new Product("Bvlgiri Soap", 2, 200.0);
        IOffer discount = new BuyXItemGetYitem(2, 1);
        IOffer noOffer = new NoOffer();
        cart.setOffer(discount);
        cart.addProduct(gatsByCream);
        cart.setOffer(noOffer);
        cart.addProduct(bvlgiriSoap);
        assertEquals(cart.getProductCount(), 2);
        assertEquals(cart.totalCartValue(), 260);
    }

    @Test
    public void testApplyBuyOneGetFiftyPercentDiscountOnNextOfferToTheCart() {
        IOffer offer = new DiscountOnNextItemOffer(50);
        ShoppingCart cart = new ShoppingCart();
        cart.setOffer(offer);
        Product gatsByCream = new Product("Gatsby hair cream", 2, 60.0);
        cart.addProduct(gatsByCream);
        assertEquals(1, cart.getProductCount());
        assertEquals(45.0, cart.getProductByName("Gatsby hair cream").getTotalPrice());
        assertEquals(45.0, cart.totalCartValue());
    }

    @Test
    public void testApplyBuyOneGetFiftyPercentDiscountOnNextOfferToTheMultipleProductsInCart() {
        IOffer offer = new DiscountOnNextItemOffer(50);
        ShoppingCart cart = new ShoppingCart();
        cart.setOffer(offer);
        Product gatsByCream = new Product("Gatsby hair cream", 5, 150.0);
        cart.addProduct(gatsByCream);
        assertEquals(1, cart.getProductCount());
        assertEquals(120.0, cart.getProductByName("Gatsby hair cream").getTotalPrice(), 0.0);
        assertEquals(120.0, cart.totalCartValue(), 0.0);
    }

    @TempDir
    File tempDirectory;

    @Test
    public void testTemporaryFolder() throws IOException {

        Assertions.assertTrue(Files.isDirectory(tempDirectory.toPath()));

        System.out.println("Temp directory: " + tempDirectory.getAbsolutePath());
        Path createdFile = Files.createFile(tempDirectory.toPath().resolve("createdFile.txt"));
        System.out.println("Created file: " + createdFile.toAbsolutePath());

        Assertions.assertTrue(createdFile.toFile().exists());
    }

    @Test
    public void testZeroSizeOnProductList() {
        ShoppingCart cart = new ShoppingCart();
        Product gatsByCream = new Product("Gatsby hair cream", 0, 150.0);
       Assertions.assertThrowsExactly(IllegalArgumentException.class, ()->cart.addProduct(gatsByCream),()->"Quantity is not 0");
    }

    @Test
    public void testAddProductWithNullName() {
        ShoppingCart cart = new ShoppingCart();
        Product gatsByCream = new Product(null, 2, 150.0);

        Assertions.assertThrowsExactly(IllegalArgumentException.class,
                () -> cart.addProduct(gatsByCream),
                () -> "This product has Name");
    }

    @Test
    public void testAddProductWithEmptyName() {
        ShoppingCart cart = new ShoppingCart();
        Product gatsByCream = new Product("", 2, 150.0);

        Assertions.assertThrowsExactly(IllegalArgumentException.class,
                () -> cart.addProduct(gatsByCream),
                () -> "This product has Name");
    }

    @Test
    public void testGetProductByNameNotFound() {
        ShoppingCart cart = new ShoppingCart();
        Product gatsByCream1 = new Product("ff", 2, 150.0);
        cart.addProduct(gatsByCream1);

        Assertions.assertThrowsExactly(IllegalArgumentException.class,
                () -> cart.getProductByName("gg"),
                () -> "This product has incorrect name");
    }

    @Test
    public void totalCartValueZero(){
        ShoppingCart cart = new ShoppingCart();
        Product gatsByCream1 = new Product("ff", 0, 150.0);
        assertEquals(cart.totalCartValue(), 0);
    }
}
