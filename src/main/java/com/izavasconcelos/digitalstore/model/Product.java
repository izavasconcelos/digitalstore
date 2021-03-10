package com.izavasconcelos.digitalstore.model;

import java.math.BigDecimal;
import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Product {
  private String name;
  private Path file;
  private BigDecimal price;

  public Product(String name, Path file, BigDecimal price) {
    this.name = name;
    this.file = file;
    this.price = price;
  }
  public String toString() {
    return this.name;
  }
}
