package com.example.demoStripe.services.impl.stripe;

import com.example.demoStripe.services.ICheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class StripeCheckoutServiceImpl implements ICheckoutService {
    @Override
    public Session createCheckoutSession(String customerId, String sellerAccountId, List<Map<String, Object>> lineItem) throws StripeException {
        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addAllLineItem(
                        lineItem.stream().map(x -> {
                            return SessionCreateParams.LineItem.builder()
                                    .setQuantity((Long) x.get("quantity"))
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency((String) x.get("currency"))
                                                    .setUnitAmount((Long) x.get("amount"))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(UUID.randomUUID().toString())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build();
                        }).toList()
                )
                .setSuccessUrl("http://localhost:8080/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:8080/cancel")
                .putMetadata("type", "ORDER_PAYMENT")
                .putMetadata("seller_account_id", sellerAccountId);
        if (customerId != null && !customerId.isBlank()) {
            builder.putMetadata("customer_id", customerId)
                    .setCustomer(customerId);
        }
        return Session.create(builder.build());
    }
}
