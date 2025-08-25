package infrastructure;

import interfaces.ProductRepository;
import domain.dto.Product;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL; // Добавьте этот импорт
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FileProductRepository implements ProductRepository {

    private final Map<String, Product> productData = new HashMap<>();

    public FileProductRepository(String fileName) {
        loadProductsFromFile(fileName);
    }

    // isValid() task 6
    private boolean isValid(String[] data) {
        try {
            return data.length == 3 &&
                    !data[0].trim().isEmpty() &&
                    Integer.parseInt(data[1].trim()) > 0 &&
                    Double.parseDouble(data[2].trim()) > 0;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    private void loadProductsFromFile(String fileName) {
        URL resourceUrl = getClass().getClassLoader().getResource(fileName);
        if (resourceUrl == null) {
            throw new RuntimeException("products.csv file not found in resources folder");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(resourceUrl.getFile()))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (isValid(data)) {
                    String name = data[0].trim();
                    int quantity = Integer.parseInt(data[1].trim());
                    double price = Double.parseDouble(data[2].trim());
                    productData.put(name, new Product(name, quantity, price));
                } else {
                    System.err.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading product data file: " + fileName, e);
        }
    }

    @Override
    public Optional<Product> findByName(String name) {
        return Optional.ofNullable(productData.get(name));
    }

    @Override
    public List<Product> getAllProducts() {
        return new java.util.ArrayList<>(productData.values());
    }
}