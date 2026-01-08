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
    public Map<String, String> createCheckoutSession(String customerId, String sellerAccountId, List<Map<String, Object>> lineItem) {
        try {
            Long totalAmount = lineItem.stream()
                    .mapToLong(item -> {
                        Long quantity = (Long) item.get("quantity");
                        Long amount = (Long) item.get("amount");
                        return quantity * amount;
                    })
                    .sum();
            Long platformFee = (long) (totalAmount * 0.05);
            Long sellerAmount = totalAmount - platformFee;
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

                                        .putMetadata("platform_fee", String.valueOf(platformFee))
                                        .putMetadata("seller_amount", String.valueOf(sellerAmount))
                                        .putMetadata("total_amount", String.valueOf(totalAmount))
                                        .putMetadata("items_count", String.valueOf(lineItem.size()))
                                        .putMetadata("seller_account_id", sellerAccountId)
                                        .build();
                            }).toList()
                    )
                    .setSuccessUrl("http://localhost:8080/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:8080/cancel")
                    .putMetadata("type", "ORDER_PAYMENT")
                    .putMetadata("seller_account_id", sellerAccountId)
                    .putMetadata("platform_fee_percent", "5")
                    .putMetadata("timestamp", String.valueOf(System.currentTimeMillis()))
                    .setBillingAddressCollection(
                            SessionCreateParams.BillingAddressCollection.REQUIRED
                    )
                    .setShippingAddressCollection(
                            SessionCreateParams.ShippingAddressCollection.builder()
                                    .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.US)
                                    .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.VN)
                                    .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.GB)
                                    .build()
                    )
                    .setPhoneNumberCollection(
                            SessionCreateParams.PhoneNumberCollection.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    // Locale - ngôn ngữ hiển thị
                    .setLocale(SessionCreateParams.Locale.AUTO)
                    // Customer options
                    .setCustomerCreation(SessionCreateParams.CustomerCreation.IF_REQUIRED)
                    // Tax calculation (nếu dùng Stripe Tax)
                    .setAutomaticTax(
                            SessionCreateParams.AutomaticTax.builder()
                                    .setEnabled(true)
                                    .build()
                    );
            if (customerId != null && !customerId.isBlank()) {
                builder.putMetadata("customer_id", customerId)
                        .setCustomer(customerId);
            }
            Session session = Session.create(builder.build());
            return Map.of("url", session.getUrl());
        } catch (Exception e) {
            throw new RuntimeException("Checkout session failed");
        }
    }
}
