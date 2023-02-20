package com.amigoscode.testing.payment.stripe;

import com.amigoscode.testing.payment.CardPaymentCharge;
import com.amigoscode.testing.payment.Currency;
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class StripeServiceTest {

    private StripeService underTest;

    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new StripeService(stripeApi);
    }

    @Test
    void isShouldChargeCard() throws StripeException {
        //Given
        String cardSource = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;
        String description = "Zakat";

        //... Charge has been successful
        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(),any())).willReturn(charge);

        //When
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(cardSource, amount, currency, description);

        //Then
        // ... Stripe API was called with correct input parameters
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);

        then(stripeApi).should().create(mapArgumentCaptor.capture(), optionsArgumentCaptor.capture());

        Map<String, Object> requestMap = mapArgumentCaptor.getValue();

        assertThat(requestMap.keySet()).hasSize(4);
        // ... correct parameters were passed to the Stripe API
        assertThat(requestMap.get("amount")).isEqualTo(amount);
        assertThat(requestMap.get("currency")).isEqualTo(currency);
        assertThat(requestMap.get("source")).isEqualTo(cardSource);
        assertThat(requestMap.get("description")).isEqualTo(description);

        RequestOptions options = optionsArgumentCaptor.getValue();
        assertThat(options).isNotNull();

        // ... the charge was successful
        assertThat(cardPaymentCharge.isCardDebited()).isTrue();
    }

    @Test
    void isShouldThrowExceptionWhenChargeCard() throws StripeException {
        //Given
        String cardSource = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;
        String description = "Zakat";

        //... Charge has been unsuccessful
        given(stripeApi.create(anyMap(),any())).willThrow(new ApiConnectionException(""));

        //When
        //Then
        assertThatThrownBy(() -> underTest.chargeCard(cardSource,amount,currency,description))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot make stripe charge");
    }
}