package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    @Test
    void isShouldChargeCardSuccessfully() {
        //Given
        UUID customerId = UUID.randomUUID();

        //... customer exist
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        customerId,
                        new BigDecimal("100.00"),
                        Currency.USD,
                        "card123xx",
                        "Donation"
                )
        );

        // ... Card is charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));

        //When
        underTest.chargeCard(customerId, paymentRequest);

        //Then
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment savedPaymentValue = paymentArgumentCaptor.getValue();
        assertThat(savedPaymentValue).isEqualToIgnoringGivenFields(paymentRequest.getPayment(), "customerId");

        assertThat(savedPaymentValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void isShouldThrowWhenCardIsNotCharged() {
        //Given
        UUID customerId = UUID.randomUUID();

        //... customer exist
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        customerId,
                        new BigDecimal("100.00"),
                        Currency.USD,
                        "card123xx",
                        "Donation"
                )
        );

        // ... Card is not charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        //When
        //Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .hasMessageContaining(String.format("Card not debited for customer %s", customerId))
                .isInstanceOf(IllegalStateException.class);

        // ... No interactions with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void isShouldNotChargeCardAndThrowWhenCurrencyNotSupported() {
        //Given
        UUID customerId = UUID.randomUUID();

        //... customer exist
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        customerId,
                        new BigDecimal("100.00"),
                        Currency.EUR,
                        "card123xx",
                        "Donation"
                )
        );

        //When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .hasMessageContaining(String.format("Currency [%s] not supported", paymentRequest.getPayment().getCurrency()))
                .isInstanceOf(IllegalStateException.class);


    }

    @Test
    void isShouldNotChargeAndThrowWhenCustomerNotFound() {
        //Given
        UUID customerId = UUID.randomUUID();

        //... customer not found in db
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        //When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, mock(PaymentRequest.class)))
                .hasMessageContaining(String.format("Customer with id [%s] not found!", customerId))
                .isInstanceOf(IllegalStateException.class);

        //Then
        // ... No interactions with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoInteractions();
        // ... No interactions with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }
}





