package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest {

    @InjectMocks
    private CustomerRegistrationService underTest;

    @Mock
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

//    @BeforeEach
//    void setUp() {
//        underTest = new CustomerRegistrationService(customerRepository);
//    }

    @Test
    void isShouldSaveNewCustomer() {
        //Given
        String phoneNumber = "12345";
        Customer customer = new Customer(UUID.randomUUID(), "Zoe", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());

        //When
        underTest.registerNewCustomer(request);

        //Then
        then(customerRepository).should(times(1)).save(customerArgumentCaptor.capture());

        Customer savedCustomer = customerArgumentCaptor.getValue();

        assertThat(savedCustomer).isEqualToComparingFieldByField(customer);
    }

    @Test
    void isShouldSaveNewCustomerWithIdIsNull() {
        //Given
        String phoneNumber = "12345";
        Customer customer = new Customer(null, "Zoe", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());

        //When
        underTest.registerNewCustomer(request);

        //Then
        then(customerRepository).should(times(1)).save(customerArgumentCaptor.capture());

        Customer savedCustomer = customerArgumentCaptor.getValue();

        assertThat(savedCustomer).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(savedCustomer.getId()).isNotNull();
    }

    @Test
    void isShouldNotSaveCustomerWhenCustomerExists() {
        //Given
        String phoneNumber = "12345";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Zoe", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer));

        //When
        underTest.registerNewCustomer(request);

        //Then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void isShouldThrowExceptionWhenPhoneNumberIsAlreadyTaken() {
        //Given
        String phoneNumber = "12345";
        UUID id = UUID.randomUUID();
        Customer customerRequest = new Customer(null, "Zoe", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customerRequest);

        Customer customer = new Customer(UUID.randomUUID(), "Marta", phoneNumber);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer));

        //When
        //Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Phone number [%s] is taken", phoneNumber));

        //Finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }
}