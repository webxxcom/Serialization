package test.java;

import main.java.storage.CsvSerializable;
import main.java.storage.IllegalCsvFormat;

import java.util.Objects;

public class Person implements CsvSerializable {
    private final String name;
    private final int age;

    public Person(){
        name = null;
        age = 0;
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String getCsvString() {
        return name + ',' + age;
    }

    @Override
    public Person fromFields(String[] values) {
        try{
            return new Person(values[0].equals("null") ? null  : values[0], Integer.parseInt(values[1]));
        } catch(NumberFormatException ex){
            throw new IllegalCsvFormat(ex);
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}
