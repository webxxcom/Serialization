package main.java.entity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;

public final class Entities {

    public static final class NameComparator implements Comparator<Entity>{
        @Override
        public int compare(Entity o1, Entity o2) {
            if(o1 == null) return -1;
            if(o2 == null) return 1;

            return o1.compareTo(o2);
        }
    }

    public static final class PriceComparator implements Comparator<Product>{
        @Override
        public int compare(Product o1, Product o2) {
            if(o1 == null) return -1;
            if(o2 == null) return 1;

            return o1.getPrice().compareTo(o2.getPrice());
        }
    }

    public static final class AddressComparator implements Comparator<Partner>{
        @Override
        public int compare(Partner o1, Partner o2) {
            if(o1 == null) return -1;
            if(o2 == null) return 1;

            return o1.getAddress().compareTo(o2.getAddress());
        }
    }

    public static final class NameAndIdComparator implements Comparator<Entity>{
        @Override
        public int compare(Entity o1, Entity o2) {
            if(o2 == null) return 1;
            if(o1 == null) return -1;

            int result = o1.compareTo(o2);
            if(result != 0)
                return result;

            return o1.name.compareTo(o2.name);
        }
    }

    //Hide constructor in order not to be able to create an instance of Entities
    private Entities(){ }

    @SafeVarargs
    public static <T extends Entity> boolean addEntitiesTo(T[]  l, T... entities){
        boolean flag = true;
        int i = 0;

        for(T entity : entities) {
            for (; i < l.length; ++i) {
                if (l[i] == null) {
                    l[i] = entity;
                    flag = true;
                    break;
                } else flag = false;
            }
        }
        return flag;
    }

    public static <T extends Entity> @NotNull String toString(T[] l){
        StringBuilder str = new StringBuilder();
        for(T entity : l){
            if(entity != null) {
                if(!str.isEmpty()) str.append('\n');
                str.append(entity);
            }
        }
        return str.isEmpty() ?
                "No " + l.getClass().getComponentType().getSimpleName() + "s in the list"
                : str.toString();
    }

    public static <T extends Entity> boolean removeFrom(T[] l, int id){
        return replaceIn(l, id, null);
    }

    public static <T extends Entity> boolean replaceIn(T[] l, int id, T newEntity) {
        int index = indexOf(l, id);
        if(index == -1) return false;

        l[index] = newEntity;
        return true;
    }

    public static <T extends Entity> int indexOf(T[] l, int id){
        for(int i = 0; i < l.length; ++i)
            if (l[i] != null && l[i].getId() == id)
                return i;
        return -1;
    }

    @Contract(pure = true)
    public static <T extends Entity> int indexOf(T[] l, T e){
        for(int i = 0; i < l.length; ++i)
            if (l[i] != null && l[i].equals(e))
                return i;
        return -1;
    }

    public static <T extends Entity> @Nullable T findById(T[] l, int id){
        int index = indexOf(l, id);
        return index != -1 ? l[index] : null;
    }

    @Contract(pure = true)
    public static <T extends Entity> @Nullable T find(T[] l, T e){
        int index = indexOf(l, e);
        return index != -1 ? l[index] : null;
    }

    @Contract(pure = true)
    public static <T extends Entity> int count(T[] l, T e){
        int result = 0;
        for(var ent : l)
            if((ent == null && e == null) || Objects.equals(ent, e))
                ++result;
        return result;
    }
}
