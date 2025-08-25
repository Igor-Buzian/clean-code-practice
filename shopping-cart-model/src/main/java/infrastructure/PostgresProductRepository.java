package infrastructure;

import domain.dto.Product;
import interfaces.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresProductRepository implements ProductRepository {

    private final DatabaseConnectionManager connectionManager;

    public PostgresProductRepository(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void importFromFile(String fileName) {
        FileProductRepository fileRepo = new FileProductRepository(fileName);
        List<Product> products = fileRepo.getAllProducts();
        String insertSql = "INSERT INTO products (name, quantity, price) \n" +
                "VALUES (?, ?, ?) \n" +
                "ON CONFLICT (name) DO NOTHING;";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Product product : products) {
                if (isValid(product)) {
                    pstmt.setString(1, product.getProductName());
                    pstmt.setInt(2, product.getQuantity());
                    pstmt.setDouble(3, product.getTotalPrice());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error importing products to database", e);
        }
    }
    // isValid() task 6
    private boolean isValid(Product product) {
        return product.getProductName() != null && !product.getProductName().isEmpty() &&
                product.getQuantity() > 0 &&
                product.getTotalPrice() >= 0;
    }

    @Override
    public Optional<Product> findByName(String name) {
        String selectSql = "SELECT name, quantity, price FROM products WHERE name = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Product(
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String selectAllSql = "SELECT name, quantity, price FROM products";
        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllSql)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}