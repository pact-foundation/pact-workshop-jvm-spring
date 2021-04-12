package au.com.dius.pactworkshop.provider;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final Map<String, Product> PRODUCTS = Map.of(
            "09", new Product("09", "CREDIT_CARD", "Gem Visa", "v1"),
            "10", new Product("10", "CREDIT_CARD", "28 Degrees", "v1"),
            "11", new Product("11", "PERSONAL_LOAN", "MyFlexiPay", "v2")
    );

    public List<Product> fetchAll() {
        return List.copyOf(PRODUCTS.values());
    }

    public Optional<Product> getById(String id) {
        return Optional.ofNullable(PRODUCTS.get(id));
    }

}
