package com.example.demoStripe.services;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;
import com.stripe.model.Refund;

import java.util.List;

public interface IPaymentService {
    PaymentIntent createPayment(Long amount, String currency, String sellerAccountId, String customerId, List<String> paymentMethod) throws StripeException;
    Payout payout(String sellerAccountId, Long amount, String currency) throws StripeException;
    Refund refund(String paymentIntentId, Long amount, String currency, String customerId) throws StripeException;
}
