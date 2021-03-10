package com.izavasconcelos.digitalstore.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {
  private String name;

  public Customer(String name) {
    this.name = name;
  }

  public String toString() {
    return this.name;
  }
}
