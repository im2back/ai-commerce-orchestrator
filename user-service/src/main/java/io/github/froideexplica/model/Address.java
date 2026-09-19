package io.github.froideexplica.model;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "house_number")
    private String houseNumber;

    private String complement;

    public Address() {
    }

    public Address(String streetName, String houseNumber, String complement) {
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.complement = complement;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getComplement() {
        return complement;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }
}