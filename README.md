# JUnit-Product

Class **Product**
Fields:

String ``productName``: The name of the product.

int ``quantity``: The number of units of the product.

double ``totalPrice``: The total price for all units (quantity × price per unit).

### Constructor:

Product(`String productName, int quantity, double totalPrice`)

Methods:

`getProductName()`: Returns the product name.

`getQuantity()`: Returns the quantity.

`getTotalPrice()`: Returns the total price.

`setTotalPrice(double)`: Updates the total price (e.g., after applying a discount).

### Interface IOffer
Method:

`applyOffer(Product product)`: Modifies the product's price based on the discount rule.

### Class BuyXItemGetYItem implements IOffer
Fields:

int `payItem`: The number of items to pay for.

int `freeItem`: The number of free items.

### Logic:
If the product quantity is greater than payItem, the discount is applied.

The number of free items is calculated as: freeItemsToGive = quantity / (payItem + freeItem) * freeItem

The price per unit is calculated as: unitPrice = totalPrice / quantity

The total discount is: discount = freeItemsToGive * unitPrice

The new total price is: newTotalPrice = oldTotalPrice - discount

### Example:
Buy 2, get 1 free.

Shopping cart: 5 items × 30₽ = 150₽.

Free items to give: 5/(2+1)∗1=1.

Total discount: 1×30₽ = 30₽.

Final price: 150₽ - 30₽ = 120₽.

### Class DiscountOnNextItemOffer implements IOffer
Fields:

int `discountPercent`: The discount percentage for every second item.

Logic:
The discount is applied to every second item.

Calculate the price per unit: unitPrice = totalPrice / quantity.

Determine the number of discounted items: numberOfDiscountedItems = quantity / 2.

Calculate the discount per item: discountPerItem = unitPrice * discountPercent / 100.

Calculate the total discount: totalDiscount = numberOfDiscountedItems * discountPerItem.

Update the total price: newTotalPrice = oldTotalPrice - totalDiscount.

Example:
Buy one, get the second at a 50% discount.

Shopping cart: 2 items × 30₽ = 60₽.

Number of discounted items: 2/2=1.

Discount on second item: 30₽ × 50% = 15₽.

Final price: 60₽ - 15₽ = 45₽.

### Class NoOffer implements IOffer
This is a placeholder that does not apply any discounts.

### Class ShoppingCart
Fields:

List<Product> `productList`: The list of products in the cart.

double `totalCartValue`: The total value of the cart.

### IOffer offer: The active offer.

Methods:

`addProduct(Product)`: Adds a product to the cart with validation. If an offer is set, it's applied to the product.

`getProductByName(String)`: Finds a product by its name; otherwise, it throws an exception.

`getProductCount()`: Returns the total number of products in the cart (sum of quantities).

`totalCartValue()`: Recalculates the total value of the cart from all products.

`setOffer(IOffer)`: Sets the active offer for subsequent products.
