package com.store.management.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Supplier")
@NoArgsConstructor
public class Supplier extends PersistedBean {
    private String name;
    private Address address;
    private String url;

    public Supplier(String name) {
        this.name = name;
    }

    @Column(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JoinColumn(name = "AddressId")
    @OneToOne
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Column(name = "Url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
