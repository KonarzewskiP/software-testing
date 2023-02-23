package com.amigoscode.testing.account;

import com.amigoscode.testing.payment.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AccountCreateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void itShouldThrowWhenCustomerIdIsNull() {
        //Given
        AccountCreateRequest request = new AccountCreateRequest(null, Currency.GBP, "Halifax", BigDecimal.ZERO, 1L);
        //When
        Set<ConstraintViolation<AccountCreateRequest>> violation = validator.validate(request);
        //Then
        assertThat(violation).isNotEmpty();
    }


    @Test
    void itShouldThrowWhenBankNameIsBlank() {
        //Given
        AccountCreateRequest request = new AccountCreateRequest(
                UUID.randomUUID(),
                Currency.GBP,
                "",
                BigDecimal.ZERO,
                1L);
        //When
        Set<ConstraintViolation<AccountCreateRequest>> violation = validator.validate(request);
        //Then
        assertThat(violation).isNotEmpty();
    }

    @Test
    void itShouldThrowWhenDepositIsNegative() {
        //Given
        AccountCreateRequest request = new AccountCreateRequest(
                UUID.randomUUID(),
                Currency.GBP,
                "Halifax",
                new BigDecimal("-10.00"),
                1L);
        //When
        Set<ConstraintViolation<AccountCreateRequest>> violation = validator.validate(request);
        //Then
        assertThat(violation).isNotEmpty();
    }

    @Test
    void itShouldCreateRequest() {
        //Given
        AccountCreateRequest request = new AccountCreateRequest(
                UUID.randomUUID(),
                Currency.GBP,
                "Halifax",
                BigDecimal.TEN,
                1L);
        //When
        Set<ConstraintViolation<AccountCreateRequest>> violation = validator.validate(request);
        //Then
        assertThat(violation).isEmpty();
    }
}