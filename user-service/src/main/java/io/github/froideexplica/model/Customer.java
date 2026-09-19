package io.github.froideexplica.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import io.github.froideexplica.dto.input.RegisterCustomerDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Table(name = "tb_customer")
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name")
    private String name;

    private String document;

    private String email;

    @Column(unique = true)
    private String phone;

    @Column(name = "is_active", nullable = true)
    private boolean isActive;

    @Embedded
    private Address address;

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<PurchaseRecord> purchaseRecord = new ArrayList<>();

    public Customer() {
    }

    public Customer(
            Long id,
            String name,
            String document,
            String email,
            String phone,
            boolean isActive,
            Address address,
            List<PurchaseRecord> purchaseRecord
    ) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.email = email;
        this.phone = phone;
        this.isActive = isActive;
        this.address = address;
        this.purchaseRecord = purchaseRecord;
    }

    public Customer(String name, String document, String email, String phone, boolean isActive, Address address) {
        this.name = name;
        this.document = document;
        this.email = email;
        this.phone = phone;
        this.isActive = isActive;
        this.address = address;
    }

    public Customer(RegisterCustomerDTO dtoIn) {
        super();
        this.name = dtoIn.name();
        this.document = dtoIn.document();
        this.email = dtoIn.email();
        this.phone = dtoIn.phone();
        this.isActive = true;
        this.address = new Address(dtoIn.address().streetName(),dtoIn.address().houseNumber(), dtoIn.address().complement());

    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseRecord p : this.purchaseRecord) {
            if (p.getStatus() == Status.EM_ABERTO) {
                total = total.add(
                        p.getProductprice().multiply(BigDecimal.valueOf(p.getQuantity()))
                );
            }
        }

        return total;
    }

    public void addPurchaseRecord(PurchaseRecord purchaseRecord) {
        this.purchaseRecord.add(purchaseRecord);
        purchaseRecord.setCustomer(this);
    }

    public void removePurchaseRecord(PurchaseRecord purchaseRecord) {
        this.purchaseRecord.remove(purchaseRecord);
        purchaseRecord.setCustomer(null);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDocument() {
        return document;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isActive() {
        return isActive;
    }

    public Address getAddress() {
        return address;
    }

    public List<PurchaseRecord> getPurchaseRecord() {
        return purchaseRecord;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setPurchaseRecord(List<PurchaseRecord> purchaseRecord) {
        this.purchaseRecord = purchaseRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return id != null && Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}