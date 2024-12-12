package main.java.entity;

import main.java.storage.CsvSerializable;
import main.java.storage.IllegalCsvFormat;

import java.io.Serial;

public class Customer extends Partner {
    @Serial
    private static final long serialVersionUID = 1452981764358903L;
    public Customer(){
        super();
    }

    public Customer(int id, String name, String address) {
        super(id, name, address);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        return o instanceof Customer && super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public Customer fromFields(String[] vals) throws IllegalCsvFormat {
        CsvSerializable.checkNumberOfFields(vals, 3);

        try{
            int id = Integer.parseInt(vals[0]);
            String name = vals[1] == null || vals[1].equals("null") ? null : vals[1];
            String address = vals[2] == null || vals[2].equals("null") ? null : vals[2];

            return new Customer(id, name, address);
        } catch(NumberFormatException ex){
            throw new IllegalCsvFormat("Unable to parse " + vals[1] +
                    " to int in Customer#createByString(String[])", ex);
        } catch(IndexOutOfBoundsException ex){
            throw new IllegalArgumentException(
                    "The values after reading csv line are not enough to instantiate " +
                            getClass().getName() + " class", ex);
        }
    }
}
