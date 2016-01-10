package com.pse.fotoz.domain.entities;

import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Gijs
 */
@Entity
@Table(name = "product_types")
public class ProductType implements HibernateEntity{
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Basic
    @Column(name = "name")
    @Size(min = 1, message="{error_size_name}")
    private String name;
    
    @Basic
    @Column(name = "description")
    @Size(min = 1, message="{error_size_description}")
    private String description;
            
    @Basic
    @Column(name = "price")
    @NotNull(message="{error_decimal_price}")
    @DecimalMin(value="0.01", message="{error_decimal_price}")
    private BigDecimal price;
         
    @Basic
    @Column(name = "stock")
    private int stock;
    
    @Basic
    @Column(name = "filename")
    private String filename;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    
}
