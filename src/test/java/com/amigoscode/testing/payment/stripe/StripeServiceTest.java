package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

class StripeServiceTest {

    private StripeService underTest;

    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        underTest = new StripeService(stripeApi);
    }

    @Test
    void isShouldChargeCard() {
        //Given
        String cardSource = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency usd = Currency.USD;
        String zakat = "Zakat";

        //When
        underTest.chargeCard(
                cardSource,
                amount,
                usd,
                zakat);

        //Then
    }
}