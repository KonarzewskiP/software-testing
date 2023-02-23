package com.amigoscode.testing.account;

import com.amigoscode.testing.payment.Currency;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;

public class AccountCreateRequest {

    @NotNull
    private final UUID customerId;
    @NotNull
    private final Currency currency;
    @NotBlank
    private final String bankName;
    @PositiveOrZero
    @NotNull
    private final BigDecimal deposit;
    @NotNull
    private final Long branchId;

    public AccountCreateRequest(UUID customerId, Currency currency, String bankName, BigDecimal deposit, Long branchId) {
        this.customerId = customerId;
        this.currency = currency;
        this.bankName = bankName;
        this.deposit = deposit;
        this.branchId = branchId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getBankName() {
        return bankName;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public Long getBranchId() {
        return branchId;
    }
}
