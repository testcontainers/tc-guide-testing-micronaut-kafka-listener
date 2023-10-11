package com.testcontainers.demo;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@MicronautTest(transactional = false)
class ProductPriceChangedEventHandlerTestResourcesTest {

    @Test
    void shouldHandleProductPriceChangedEvent(
            ProductPriceChangesClient productPriceChangesClient, ProductRepository productRepository) {
        Product product = new Product(null, "P100", "Product One", BigDecimal.TEN);
        Long id = productRepository.save(product).getId();

        ProductPriceChangedEvent event = new ProductPriceChangedEvent("P100", new BigDecimal("14.50"));

        productPriceChangesClient.send(event.productCode(), event);

        await().pollInterval(Duration.ofSeconds(3)).atMost(10, SECONDS).untilAsserted(() -> {
            Optional<Product> optionalProduct = productRepository.findByCode("P100");
            assertThat(optionalProduct).isPresent();
            assertThat(optionalProduct.get().getCode()).isEqualTo("P100");
            assertThat(optionalProduct.get().getPrice()).isEqualTo(new BigDecimal("14.50"));
        });

        productRepository.deleteById(id);
    }
}
