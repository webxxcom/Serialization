package main.java.entity;

import org.jetbrains.annotations.NotNull;
import main.java.storage.CsvSerializable;
import main.java.storage.IllegalCsvFormat;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Objects;

public final class Product extends Entity {
    private final BigDecimal price;
    private int quantityInStock;
    @Serial
    private static final long serialVersionUID = 193401234128017L;
    private void validateQuantity(int q) throws IllegalArgumentException{
        if(q <= 0) throw new IllegalArgumentException();
    }

    public Product(){
        super();

        price = null;
        quantityInStock = -1;
    }

    public Product(int id, String name, double price, int quantityInStock) {
        super(id, name);
        validateQuantity(quantityInStock);

        this.price = BigDecimal.valueOf(price);
        this.quantityInStock = quantityInStock;
    }

    public Product(int id, String name, BigDecimal price, int quantityInStock) {
        super(id, name);
        validateQuantity(quantityInStock);

        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    public Product(@NotNull Product other){
        super(other.id, other.name);

        this.price = other.price;
        this.quantityInStock = other.quantityInStock;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public int getQuantity(){
        return quantityInStock;
    }

    public void addQuantityInStock(int dq){
        this.quantityInStock += dq;
    }

    @Override
    public String toString() {
        return super.toString() + ", price:"
                + price + "₴, quantityInStock:"
                + quantityInStock + "}";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        return o instanceof Product that
                && super.equals(o)
                && Objects.equals(that.price, this.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), price);
    }

    @Override
    public String getCsvString() {
        return super.getCsvString() + ',' + price + ',' + quantityInStock;
    }

    @Override
    public Product fromFields(String[] vals) throws IllegalCsvFormat, IndexOutOfBoundsException {
        CsvSerializable.checkNumberOfFields(vals, 4);

        try{
            int id = Integer.parseInt(vals[0]);
            String name = vals[1] == null || vals[1].equals("null") ? null : vals[1];
            double pr = Double.parseDouble(vals[2]);
            int qInStock = Integer.parseInt(vals[3]);

            return new Product(id, name, pr, qInStock);
        } catch(NumberFormatException ex){
            throw new IllegalCsvFormat("Unable to parse something from:  "
                    + vals[0] + ',' + vals[2] + ',' + vals[3] + " to int, double, int " +
                    "respectively", ex);

        }
    }
}
