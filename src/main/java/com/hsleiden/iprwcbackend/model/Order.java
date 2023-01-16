package com.hsleiden.iprwcbackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.hsleiden.iprwcbackend.repository.OrderRepo;
import com.hsleiden.iprwcbackend.service.EntityIdResolver;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Table(name = "`order`")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = EntityIdResolver.class, scope = Order.class)
public class Order {

    @Id
    @Column(name = "id")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private Set<OrderProduct> orderProducts;

    private Date createdAt = new Date();

    private String invoiceNumber;

    @Column(nullable = false)
    private String bank;

    @Column(nullable = false)
    private String emailSentTo;

    private BigDecimal totalPrice;

    @Transient
    private List<UUID> products = new ArrayList<>();

    public String createInvoiceNumber(OrderRepo orderRepo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());
        String formattedDate = formatter.format(Instant.now());

        Long totalOrdersToday;
        totalOrdersToday = orderRepo.countOrdersToday();

        String invoiceNumber = formattedDate + (totalOrdersToday + 1);
        return invoiceNumber;
    }

    @Transient
    @JsonIgnore
    private List<String> bankOptions = Arrays.asList("ING", "RABO", "ABN", "SNS", "KNAB");

    public boolean isValidBank(String bank) {
        return bankOptions.contains(bank);
    }
}
