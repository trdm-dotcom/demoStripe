package com.example.demoStripe.configs;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class StripeConfig {
    @Value("${app.payment.stripe.secret-key}")
    private String secretKey;

    @Value("${app.payment.stripe.webhook-secret}")
    private String endpointSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = this.secretKey;
    }
}
