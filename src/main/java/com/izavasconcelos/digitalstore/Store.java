package com.izavasconcelos.digitalstore;

import com.izavasconcelos.digitalstore.model.Customer;
import com.izavasconcelos.digitalstore.model.Payment;
import com.izavasconcelos.digitalstore.model.Product;
import com.izavasconcelos.digitalstore.model.Subscription;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Store {

  public static void main(String[] args) {
    Customer paulo = new Customer("Paulo Silveira");
    Customer rodrigo = new Customer("Rodrigo Turini");
    Customer guilherme = new Customer("Guilherme Silveira");
    Customer adriano = new Customer("Adriano Almeida");

    Product bach = new Product("Bach Completo", Paths.get("/music/bach.mp3"), new BigDecimal(100));
    Product anitta =
        new Product("Poderosas Anita", Paths.get("/music/poderosas.mp3"), new BigDecimal(90));
    Product bandeira =
        new Product("Bandeira Brasil", Paths.get("/images/brasil.jpg"), new BigDecimal(50));
    Product beauty = new Product("Beleza Americana", Paths.get("beauty.mov"), new BigDecimal(150));
    Product vingadores =
        new Product("Os Vingadores", Paths.get("/movies/vingadores.mov"), new BigDecimal(200));
    Product amelie =
        new Product("Amelie Poulain", Paths.get("/movies/amelie.mov"), new BigDecimal(100));

    LocalDateTime today = LocalDateTime.now();
    LocalDateTime yesterday = today.minusDays(1);
    LocalDateTime lastMonth = today.minusMonths(1);
    Payment payment1 = new Payment(Arrays.asList(bach, anitta), today, paulo);
    Payment payment2 = new Payment(Arrays.asList(bach, bandeira, amelie), yesterday, rodrigo);
    Payment payment3 = new Payment(Arrays.asList(beauty, vingadores, bach), today, adriano);
    Payment payment4 = new Payment(Arrays.asList(bach, anitta, amelie), lastMonth, guilherme);
    Payment payment5 = new Payment(Arrays.asList(beauty, amelie), yesterday, paulo);
    List<Payment> payments = Arrays.asList(payment1, payment2, payment3, payment4, payment5);

    // Ordenando pagamentos pela data
    payments.stream().sorted(Comparator.comparing(Payment::getDate)).forEach(System.out::println);

    // Somando o total da compra
    payment1.getProducts().stream()
        .map(Product::getPrice)
        .reduce(BigDecimal::add)
        .ifPresent(System.out::println);

    // Somando o total de todas as compras
    System.out.println("-----");
    payments.stream()
        .map(
            payment ->
                payment.getProducts().stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
        .reduce(BigDecimal::add)
        .ifPresent(System.out::println);
    System.out.println("-----");
    // FlapMap
    BigDecimal totalFlap =
        payments.stream()
            .flatMap(payment -> payment.getProducts().stream().map(Product::getPrice))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    System.out.println("Total com flapMap: " + totalFlap);
    System.out.println("-----");
    // Produtos mais vendidos
    Map<Product, Long> topProducts =
        payments.stream()
            .flatMap(sales -> sales.getProducts().stream())
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    System.out.println("Produtos mais vendidos com groupingBy: " + topProducts + "\n\n");

    topProducts.entrySet().stream()
        .max(Comparator.comparing(Map.Entry::getValue))
        .ifPresent(System.out::println);
    System.out.println("-----");
    // Valores gerados por produto

    Map<Product, BigDecimal> totalValuePerProduct =
        payments.stream()
            .flatMap(p -> p.getProducts().stream())
            .collect(
                Collectors.groupingBy(
                    Function.identity(),
                    Collectors.reducing(BigDecimal.ZERO, Product::getPrice, BigDecimal::add)));

    totalValuePerProduct.entrySet().stream()
        .sorted(Comparator.comparing(Map.Entry::getValue))
        .forEach(System.out::println);

    System.out.println("-----");
    Map<Product, BigDecimal> totalValuePerProduct2 =
        payments.stream()
            .flatMap(p -> p.getProducts().stream())
            .collect(Collectors.toMap(Function.identity(), Product::getPrice, BigDecimal::add));

    totalValuePerProduct2.entrySet().forEach(System.out::println);

    System.out.println("-----");
    // Produtos de cada cliente
    Map<Customer, List<Payment>> customerToPayments =
        payments.stream().collect(Collectors.groupingBy(Payment::getCustomer));

    customerToPayments.entrySet().forEach(System.out::println);

    Map<Customer, List<List<Product>>> customerToProductsList =
        payments.stream()
            .collect(
                Collectors.groupingBy(
                    Payment::getCustomer,
                    Collectors.mapping(Payment::getProducts, Collectors.toList())));

    Map<Customer, List<Product>> customerToProducts2steps =
        customerToProductsList.entrySet().stream()
            .collect(
                Collectors.toMap(
                    Entry::getKey,
                    e -> e.getValue().stream().flatMap(List::stream).collect(Collectors.toList())));

    customerToProducts2steps.entrySet().stream()
        .sorted(Comparator.comparing(e -> e.getKey().getName()))
        .forEach(System.out::println);

    System.out.println("-----");
    // Cliente da compra mais cara

    Map<Customer, BigDecimal> customerBigDecimalMap =
        payments.stream()
            .collect(
                Collectors.groupingBy(
                    Payment::getCustomer,
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        p ->
                            p.getProducts().stream()
                                .map(Product::getPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                        BigDecimal::add)));

    Function<Payment, BigDecimal> paymentsTotal =
        p ->
            p.getProducts().stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    System.out.println("-----");
    Map<Customer, BigDecimal> totalValuePerCustomer =
        payments.stream()
            .collect(
                Collectors.groupingBy(
                    Payment::getCustomer,
                    Collectors.reducing(BigDecimal.ZERO, paymentsTotal, BigDecimal::add)));

    totalValuePerCustomer.entrySet().stream()
        .sorted(Comparator.comparing(Map.Entry::getValue))
        .forEach(System.out::println);

    System.out.println("-----");
    // Relatórios com datas

    Map<YearMonth, List<Payment>> paymentsPerMonth =
        payments.stream().collect(Collectors.groupingBy(p -> YearMonth.from(p.getDate())));
    paymentsPerMonth.entrySet().stream().forEach(System.out::println);

    System.out.println("-----");
    // Lucro por mês

    Map<YearMonth, BigDecimal> paymentsValuePerMonth =
        payments.stream()
            .collect(
                Collectors.groupingBy(
                    p -> YearMonth.from(p.getDate()),
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        p ->
                            p.getProducts().stream()
                                .map(Product::getPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                        BigDecimal::add)));

    System.out.println("-----");
    // Sistema de assinaturas

    BigDecimal monthlyFee = new BigDecimal("99.90");
    Subscription s1 = new Subscription(monthlyFee, yesterday.minusMonths(5), paulo);
    Subscription s2 = new Subscription(monthlyFee, yesterday.minusMonths(8), today.minusMonths(1), rodrigo);
    Subscription s3 = new Subscription(monthlyFee, yesterday.minusMonths(5), today.minusMonths(2), adriano);
    List<Subscription> subscriptions = Arrays.asList(s1, s2, s3);

    long meses = ChronoUnit.MONTHS.between(s1.getBegin(), s1.getEnd().orElse(LocalDateTime.now()));

    BigDecimal totalPaid = subscriptions.stream()
        .map(Subscription::getTotalPaid)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    System.out.println("Total pago na assinatura: " + totalPaid);
  }
}
