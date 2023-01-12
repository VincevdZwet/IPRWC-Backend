package com.hsleiden.iprwcbackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hsleiden.iprwcbackend.service.EntityIdResolver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = EntityIdResolver.class, scope = Product.class)
public class Product {
    @Id
    @Column(name = "id")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id = UUID.randomUUID();

    @Column(unique = true)
    private String title;

    private Date releaseDate;
    private String duration;
    private String imageUrl;
    private BigDecimal price;
    @JsonIgnore
    private boolean enabled = true;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private Set<OrderProduct> orderProducts;

    @ManyToMany(mappedBy = "products")
    private Set<Cart> carts;
}
