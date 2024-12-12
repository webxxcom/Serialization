package main.java.entity;

import main.java.storage.CsvSerializable;
import main.java.storage.IllegalCsvFormat;

import java.io.Serial;

public class Supplier extends Partner {
    @Serial
    private static final long serialVersionUID = 193405678528017L;

    public Supplier(){
        super();
    }

    public Supplier(int id, String name, String address, Product... availableProducts) {
        super(id, name, address);

        addProducts(availableProducts);
    }

    public void addProducts(Product... products){
        if(products == null) return;

        for(Product product : products){
            if(product == null) continue;

            cart.addProduct(product);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || o.getClass() != getClass()) return false;

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Supplier fromFields(String[] vals) throws IllegalCsvFormat {
        CsvSerializable.checkNumberOfFields(vals, 3);

        try {
            final int id = Integer.parseInt(vals[0]);
            final String name = vals[1] == null || vals[1].equals("null") ? null : vals[1];
            final String address = vals[2] == null || vals[2].equals("null") ? null : vals[2];

            return new Supplier(id, name, address);
        } catch(NumberFormatException ex){
            throw new IllegalCsvFormat("Unable to parse " + vals[0] + " to int", ex);
        }
    }
}
