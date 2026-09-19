package io.github.froideexplica.model;

import java.math.BigDecimal;
import java.time.Instant;

import io.github.froideexplica.dto.input.PurchasedProductsDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "tb_purchase")
@Entity
public class PurchaseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price")
    private BigDecimal productprice;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "purchase_date")
    private Instant purchaseDate;

    @Column(name = "product_quantity")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public PurchaseRecord() {
    }
    public PurchaseRecord(PurchasedProductsDTO dto, Instant instant, Customer customer, Status status) {
        super();
        this.productName = dto.name();
        this.productprice = dto.price();
        this.productCode = dto.code();
        this.purchaseDate = instant;
        this.customer = customer;
        this.quantity = dto.quantity();
        this.status = status;
    }
    public PurchaseRecord(
            Long id,
            String productName,
            BigDecimal productprice,
            String productCode,
            Instant purchaseDate,
            Integer quantity,
            Status status,
            Customer customer
    ) {
        this.id = id;
        this.productName = productName;
        this.productprice = productprice;
        this.productCode = productCode;
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.status = status;
        this.customer = customer;
    }

    public PurchaseRecord(
            String productName,
            BigDecimal productprice,
            String productCode,
            Instant purchaseDate,
            Integer quantity,
            Status status,
            Customer customer
    ) {
        this.productName = productName;
        this.productprice = productprice;
        this.productCode = productCode;
        this.purchaseDate = purchaseDate;
        this.customer = customer;
        this.quantity = quantity;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getProductprice() {
        return productprice;
    }

    public String getProductCode() {
        return productCode;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Status getStatus() {
        return status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductprice(BigDecimal productprice) {
        this.productprice = productprice;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}