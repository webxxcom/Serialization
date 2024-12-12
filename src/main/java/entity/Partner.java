package main.java.entity;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public abstract class Partner extends Entity {
    protected String address;
    protected final Cart cart = new Cart();

    public static class Cart implements Serializable{
        private static final int MAX_PRODUCTS = 8;
        protected final Product[] products = new Product[MAX_PRODUCTS];

        void addProduct(@NotNull Product product) {
            for (Product prd : products) {
                // If two products have the same id
                if (prd != null && prd.getId() == product.getId()) {
                    // Add quantity in stock
                    prd.addQuantityInStock(product.getQuantity());
                    return;
                }
            }

            // If no product with the same id is found, add the new product
            Entities.addEntitiesTo(products, new Product(product));
        }

        boolean removeProduct(int productId){
            return Entities.removeFrom(products, productId);
        }

        public Product getProduct(int id){
            return Entities.findById(products, id);
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && (super.equals(obj)
                            ||
                    obj instanceof Cart that && Arrays.equals(products, that.products));
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), MAX_PRODUCTS, Arrays.hashCode(products));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Cart{");
            for(int i = 0; ;++i){
                if(products[i] != null) sb.append(products[i].toString());
                if(i == products.length - 1) return sb.append('}').toString();
                else if(products[i] != null) sb.append("; ");
            }
        }
    }

    protected Partner(){
        super();

        this.address = null;
    }

    protected Partner(int id, String name, String address){
        super(id, name);

        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public Cart getCart(){
        return cart;
    }

    public boolean removeProduct(int id){
        return cart.removeProduct(id);
    }

    public String getCartAsString(){
        return Entities.toString(cart.products);
    }

    @Override
    public final String toString() {
        return super.toString() + ", address:'" + address + "', " + cart + "}";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;

        return o instanceof Partner that
                && super.equals(that)
                && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address,  cart.hashCode());
    }

    @Override
    public final String getCsvString() {
        return super.getCsvString() + ',' + address;
    }
}
