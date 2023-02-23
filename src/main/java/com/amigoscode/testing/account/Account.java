package com.amigoscode.testing.account;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;
    private UUID customerId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private BigDecimal accountBalance;
    private Long branchId;

}
