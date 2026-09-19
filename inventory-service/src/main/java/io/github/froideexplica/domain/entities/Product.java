package io.github.froideexplica.domain.entities;

import java.math.BigDecimal;
import java.util.Objects;

import io.github.froideexplica.dto.inputdata.NewProductDTO;
import io.github.froideexplica.dto.outputdata.ProductDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "tb_product")
@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "product_name")
	private String name;

	@Column(name = "product_price")
	private BigDecimal price;

	@Column(name = "product_code", unique = true)
	private String code;

	@Column(name = "product_quantity")
	private Integer quantity;

	@Column(name = "product_url")
	private String productUrl;

	public Product() {
	}

	public Product(Long id, String name, BigDecimal price, String code, Integer quantity, String productUrl) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.code = code;
		this.quantity = quantity;
		this.productUrl = productUrl;
	}

	public Product(String name, BigDecimal price, String code, Integer quantity, String productUrl) {
		this.name = name;
		this.price = price;
		this.code = code;
		this.quantity = quantity;
		this.productUrl = productUrl;
	}

	public Product(NewProductDTO p) {
		this.name = p.name();
		this.price = p.price();
		this.code = p.code();
		this.quantity = p.quantity();
		this.productUrl = p.productUrl();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getCode() {
		return code;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public void updateAttributes(ProductDTO dto) {
		if (dto.name() != null && !dto.name().trim().isEmpty()) {
			this.name = dto.name();
		}

		if (dto.price() != null) {
			this.price = dto.price();
		}

		if (dto.quantity() != null) {
			this.quantity = dto.quantity();
		}

		if (dto.productUrl() != null && !dto.productUrl().trim().isEmpty()) {
			this.productUrl = dto.productUrl();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Product product)) {
			return false;
		}
		return Objects.equals(id, product.id)
				&& Objects.equals(name, product.name)
				&& Objects.equals(price, product.price)
				&& Objects.equals(code, product.code)
				&& Objects.equals(quantity, product.quantity)
				&& Objects.equals(productUrl, product.productUrl);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, price, code, quantity, productUrl);
	}
}