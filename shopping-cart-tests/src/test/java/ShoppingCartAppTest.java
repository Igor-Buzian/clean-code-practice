import cart.ShoppingCart;
import discount.BuyXItemGetYitem;
import discount.DiscountOnNextItemOffer;
import domain.discount.IOffer;
import discount.NoOffer;
import infrastructure.DatabaseConnectionManager;
import infrastructure.PostgresProductRepository;
import interfaces.ProductRepository;
import domain.dto.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class ShoppingCartAppTest {

    private static final String GATSBY_CREAM_NAME = "Gatsby hair cream";
    private static final String BVLGIRI_SOAP_NAME = "Bvlgiri Soap";
    private static final String AVOCADO_NAME = "Avocado";
    private static final String APPLE_NAME = "Apple";
    private static final double DELTA = 0.0;

    DatabaseConnectionManager connectionManager = new DatabaseConnectionManager("jdbc:postgresql://localhost:5432/clean_code", "postgres", "postgres");
    private final ProductRepository productRepository = new PostgresProductRepository(connectionManager);

    @Test
    public void shouldCreateEmptyShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart(productRepository);
        assertEquals(0, shoppingCart.getProductCount());
    }

    @Test
    public void shouldAddSingleProduct() {
        ShoppingCart shoppingCart = new ShoppingCart(productRepository);
        Product product = new Product(AVOCADO_NAME, 1, 52.1);
        shoppingCart.addProduct(product);

        assertEquals(1, shoppingCart.getProductCount());
        assertEquals(52.1, shoppingCart.totalCartValue());
    }

    @Test
    public void shouldAddMultipleProducts() {
        ShoppingCart shoppingCart = new ShoppingCart(productRepository);
        shoppingCart.addProduct(new Product(GATSBY_CREAM_NAME, 1, 30));
        shoppingCart.addProduct(new Product(BVLGIRI_SOAP_NAME, 1, 100));

        assertEquals(2, shoppingCart.getProductCount());
        assertEquals(130, shoppingCart.totalCartValue());
    }

    @Test
    public void shouldApplyBuyXGetYOfferToCart() {
        IOffer offer = new BuyXItemGetYitem(2, 1);
        ShoppingCart shoppingCart = new ShoppingCart(productRepository);
        Product product = new Product(GATSBY_CREAM_NAME, 5, 150.0);
        shoppingCart.setOffer(offer);
        shoppingCart.addProduct(product);

        assertEquals(120, shoppingCart.totalCartValue());
        assertEquals(1, shoppingCart.getProductCount());
    }

    @Test
    public void shouldApplyBuyXGetYOfferToDifferentProducts() {
        ShoppingCart cart = new ShoppingCart(productRepository);
        cart.setOffer(new BuyXItemGetYitem(2, 1));
        cart.addProduct(new Product(GATSBY_CREAM_NAME, 3, 90.0));
        cart.setOffer(new NoOffer());
        cart.addProduct(new Product(BVLGIRI_SOAP_NAME, 2, 200.0));

        assertEquals(2, cart.getProductCount());
        assertEquals(260, cart.totalCartValue());
    }

    @Test
    public void shouldApplyFiftyPercentDiscountToNextItem() {
        IOffer offer = new DiscountOnNextItemOffer(50);
        ShoppingCart cart = new ShoppingCart(productRepository);
        cart.setOffer(offer);
        Product product = new Product(GATSBY_CREAM_NAME, 2, 60.0);
        cart.addProduct(product);

        assertEquals(1, cart.getProductCount());
        assertEquals(45.0, cart.getProductByName(GATSBY_CREAM_NAME).get().getTotalPrice());
        assertEquals(45.0, cart.totalCartValue());
    }

    @Test
    public void shouldApplyFiftyPercentDiscountToMultipleProducts() {
        IOffer offer = new DiscountOnNextItemOffer(50);
        ShoppingCart cart = new ShoppingCart(productRepository);
        cart.setOffer(offer);
        Product product = new Product(GATSBY_CREAM_NAME, 5, 150);
        cart.addProduct(product);

        assertEquals(1, cart.getProductCount());
        assertEquals(120.0, cart.getProductByName(GATSBY_CREAM_NAME).get().getTotalPrice(), DELTA);
        assertEquals(120.0, cart.totalCartValue(), DELTA);
    }

    @Test
    public void shouldThrowExceptionWhenProductQuantityIsZero() {
        ShoppingCart cart = new ShoppingCart(productRepository);
        Product product = new Product(GATSBY_CREAM_NAME, 0, 150.0);
        assertThrowsExactly(IllegalArgumentException.class,
                () -> cart.addProduct(product),
                () -> "Quantity should not be 0");
    }

    @Test
    public void shouldThrowExceptionWhenProductNameIsNull() {
        ShoppingCart cart = new ShoppingCart(productRepository);
        Product product = new Product(null, 2, 150.0);
        assertThrowsExactly(IllegalArgumentException.class,
                () -> cart.addProduct(product),
                () -> "Product name should not be null");
    }

    @Test
    public void shouldThrowExceptionWhenProductNameIsEmpty() {
        ShoppingCart cart = new ShoppingCart(productRepository);
        Product product = new Product("", 2, 150.0);
        assertThrowsExactly(IllegalArgumentException.class,
                () -> cart.addProduct(product),
                () -> "Product name should not be empty");
    }

    @Test
    public void shouldNotFindProductByName() {
        ShoppingCart cart = new ShoppingCart(productRepository);
        cart.addProduct(new Product("ff", 2, 150.0));
        assertTrue(cart.getProductByName("gg").isEmpty());
    }

    @Test
    public void shouldHandleZeroCartValue() {
        ShoppingCart cart = new ShoppingCart(productRepository);
        assertEquals(0, cart.totalCartValue());
    }

    @Test
    void shouldApplyOfferWithFreeItems() {
        Product product = new Product(APPLE_NAME, 1, 10.0);
        IOffer offer = new BuyXItemGetYitem(2, 1);
        offer.applyOffer(product);
        assertEquals(10.0, product.getTotalPrice());
    }

    @TempDir
    File tempDirectory;

    @Test
    public void shouldCreateTemporaryFolder() throws IOException {
        Assertions.assertTrue(Files.isDirectory(tempDirectory.toPath()));
        Path createdFile = Files.createFile(tempDirectory.toPath().resolve("createdFile.txt"));
        Assertions.assertTrue(createdFile.toFile().exists());
    }

    @Test
    void shouldVerifyOfferIsCalled() {
        IOffer mockitoOffer = mock(IOffer.class);
        ProductRepository mockRepository = mock(ProductRepository.class);
        ShoppingCart cart = new ShoppingCart(mockRepository);
        Product apple = new Product(APPLE_NAME, 2, 30);
        cart.setOffer(mockitoOffer);
        cart.addProduct(apple);
        Mockito.verify(mockitoOffer, Mockito.times(1)).applyOffer(apple);
    }

    @Test
    public void shouldVerifyProductIsAdded() {
        ProductRepository mockRepository = mock(ProductRepository.class);
        ShoppingCart cart = new ShoppingCart(mockRepository);
        ShoppingCart mockedCart = Mockito.spy(cart);

        Product mango = new Product("Mango", 3, 120);
        mockedCart.addProduct(mango);

        Mockito.verify(mockedCart, Mockito.times(1)).addProduct(mango);
        assertEquals(120, mockedCart.totalCartValue());
        assertEquals(1, mockedCart.getProductCount());
    }

    @ParameterizedTest
    @MethodSource("provideCartData")
    void shouldCalculateTotalValueCorrectlyWithOffers(CartTestData data) {
        ShoppingCart cart = new ShoppingCart(productRepository);
        for (int i = 0; i < data.products.length; i++) {
            cart.setOffer(data.offers[i]);
            cart.addProduct(data.products[i]);
        }
        assertEquals(data.expectedCount, cart.getProductCount());
        assertEquals(data.totalPrice, cart.totalCartValue());
    }

    private static Stream<CartTestData> provideCartData() {
        return Stream.of(
                new CartTestData(
                        new Product[]{new Product("Shampoo", 3, 30), new Product(BVLGIRI_SOAP_NAME, 2, 45)},
                        new IOffer[]{new BuyXItemGetYitem(2, 1), new NoOffer()},
                        2,
                        65
                ),
                new CartTestData(
                        new Product[]{new Product("IPhone", 2, 1000), new Product(APPLE_NAME, 10, 10)},
                        new IOffer[]{new DiscountOnNextItemOffer(50), new NoOffer()},
                        2,
                        760
                ),
                new CartTestData(
                        new Product[]{new Product("Piniple", 20, 100), new Product("Pen", 2, 6)},
                        new IOffer[]{new NoOffer(), new NoOffer()},
                        2,
                        106
                )
        );
    }

    static class CartTestData {
        Product[] products;
        IOffer[] offers;
        int expectedCount;
        double totalPrice;

        public CartTestData(Product[] products, IOffer[] offers, int expectedCount, double totalPrice) {
            this.products = products;
            this.offers = offers;
            this.expectedCount = expectedCount;
            this.totalPrice = totalPrice;
        }
    }
}