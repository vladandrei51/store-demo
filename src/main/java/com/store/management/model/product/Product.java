package com.store.management.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.store.management.model.PersistedBean;
import com.store.management.model.Supplier;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Product")
@NamedQueries({
        @NamedQuery(name = "Product.findById",
                    query = "SELECT p FROM Product p WHERE p.id = :id")
})
public class Product extends PersistedBean {
    private String name;
    private Supplier supplier;
    private ProductSpec productSpec;
    private Set<Category> categories;
    private int price;
    private String description;

    @Column(name = "Name")
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    @JoinColumn(name = "SupplierId", referencedColumnName = "Id", nullable = false)
    @ManyToOne
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @JsonIgnore
    @JoinColumn(name = "ProductSpecId", referencedColumnName = "Id", nullable = false)
    @ManyToOne
    public ProductSpec getProductSpec() {
        return productSpec;
    }

    public void setProductSpec(ProductSpec productSpec) {
        this.productSpec = productSpec;
    }

    @Column(name = "Price")
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Column(name = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToMany
    @JoinTable(
            name = "ProductCategory",
            joinColumns = @JoinColumn(name = "ProductId"),
            inverseJoinColumns = @JoinColumn(name = "CategoryId"))
    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
}