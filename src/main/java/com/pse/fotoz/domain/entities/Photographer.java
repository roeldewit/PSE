package com.pse.fotoz.domain.entities;

import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author Robert
 */
@Entity
@Table(name = "photographers")
public class Photographer implements HibernateEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Basic
    @Column(name = "name")
    @Size(min = 1, max = 100, message = "{error_size_name}")
    private String name;

    @Basic
    @Column(name = "address")
    @Size(min = 1, message = "{error_size_address}")
    private String address;

    @Basic
    @Column(name = "city")
    @Size(min = 1, message = "{error_size_city}")
    private String city;

    @Basic
    @Column(name = "phone")
    @Size(min = 1, max = 40, message = "{error_size_phone}")
    private String phone;

    @Basic
    @Column(name = "email")
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
            + "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
            + "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "{error_pattern_email}")
    private String email;

    @OneToMany(mappedBy = "photographer", fetch = FetchType.EAGER)
    private Set<Shop> shops;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Shop> getShops() {
        return shops;
    }

    /**
     * checks ownership
     *
     * @param s
     * @return true if this photographer owns Shop
     */
    public boolean doesPhotographerOwnShop(Shop s) {
        return (this.getId() == s.getPhotographer().getId());
    }

}
