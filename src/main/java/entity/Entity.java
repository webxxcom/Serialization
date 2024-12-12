package main.java.entity;

import main.java.storage.CsvSerializable;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public abstract class Entity implements Comparable<Entity>, CsvSerializable, Serializable {
    protected final int id;
    protected final String name;

    protected Entity(){
        this.id = -1;
        name = null;
    }

    public String getCsvString(){
        return id + "," + name;
    }

    protected Entity(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id:'" + id + "'" +
                ", name:'" + name + "'";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        return o instanceof Entity that &&
                id == that.getId()
                && Objects.equals(name, that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public int compareTo(@NotNull Entity o) {
        return Integer.compare(id, o.id);
    }
}
