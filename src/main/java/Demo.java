package main.java;

import main.java.entity.*;
import main.java.storage.*;

import java.util.*;

public class Demo {
    static final Product pr1 = new Product(1, "Product 1", 100.1, 100);
    static final Supplier sp1 = new Supplier(1,"Supplier 1", "Address 1", pr1);
    static final Supplier sp2 = new Supplier(2,"Supplier 2", "Address 2");
    static final Customer cs1 = new Customer(1,"Customer 1", "Address 3");

    public static void main(String[] args) throws Exception {
        Storage stg = new Storage();
        stg.save(List.of(sp1, sp2, cs1, pr1), "src\\main\\resources\\data.csv");

        List<CsvSerializable> ls = stg.load("src\\main\\resources\\data.csv");
        ls.forEach(System.out::println);
    }
}
