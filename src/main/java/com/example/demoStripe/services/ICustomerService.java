package com.example.demoStripe.services;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

public interface ICustomerService {
    Customer createCustomer(String email, String name) throws StripeException;
}
