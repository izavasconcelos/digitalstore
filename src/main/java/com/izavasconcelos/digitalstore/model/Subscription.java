package com.izavasconcelos.digitalstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Subscription {
  private BigDecimal monthlyFee;
  private LocalDateTime begin;
  private Optional<LocalDateTime> end;
  private Customer customer;

  public Subscription(BigDecimal monthlyFee,
                      LocalDateTime begin,
                      Customer customer) {

    this.monthlyFee = monthlyFee;
    this.begin = begin;
    this.end = Optional.empty();
    this.customer = customer;
  }

  public Subscription(BigDecimal monthlyFee,
                      LocalDateTime begin,
                      LocalDateTime end,
                      Customer customer) {

    this.monthlyFee = monthlyFee;
    this.begin = begin;
    this.end = Optional.of(end);
    this.customer = customer;
  }

  public BigDecimal getTotalPaid() {
    return getMonthlyFee()
        .multiply(new BigDecimal(ChronoUnit.MONTHS.between(getBegin(),
                                                           getEnd().orElse(LocalDateTime.now()))));
  }
}
