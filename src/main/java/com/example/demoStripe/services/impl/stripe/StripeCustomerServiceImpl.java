package com.example.demoStripe.services.impl.stripe;

import com.example.demoStripe.services.ICustomerService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeCustomerServiceImpl implements ICustomerService {
    @Override
    public Customer createCustomer(String email, String name) throws StripeException {
        CustomerCreateParams.Builder builder = CustomerCreateParams.builder()
                .setEmail(email)
                .setName(name);
        return Customer.create(builder.build());
    }
}
