package com.testcontainers.demo;

import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;

@Serdeable
public record ProductPriceChangedEvent(String productCode, BigDecimal price) {}
