package com.example.demoStripe.services.impl.stripe;

import com.example.demoStripe.services.IPaymentService;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PayoutCreateParams;
import com.stripe.param.RefundCreateParams;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class StripePaymentServiceImpl implements IPaymentService {
    @Override
    public Map<String, String> createPayment(Long amount, String currency, String sellerAccountId, String customerId, List<String> paymentMethod) {
        try {
            Long platformFee = (long) (amount * 0.05);
            Long sellerAmount = amount - platformFee;
            RequestOptions requestOptions = RequestOptions.builder()
                    .setIdempotencyKey(generateIdempotencyKey(sellerAccountId, amount, currency))
                    .build();

            PaymentIntentCreateParams.Builder builder =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amount)
                            .setApplicationFeeAmount(platformFee)
                            .setCurrency(currency.toLowerCase())
                            .setTransferData(
                                    PaymentIntentCreateParams.TransferData.builder()
                                            .setDestination(sellerAccountId)
                                            .setAmount(sellerAmount)
                                            .build())
                            .addAllPaymentMethodType(paymentMethod)
                            .putMetadata("seller_account_id", sellerAccountId)
                            .putMetadata("platform_fee", String.valueOf(platformFee))
                            .putMetadata("seller_amount", String.valueOf(sellerAmount))
                            .putMetadata("payment_type", "marketplace_payment")
                            .putMetadata("timestamp", String.valueOf(System.currentTimeMillis()))
                            .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                            .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.AUTOMATIC)
                            .setDescription("Marketplace payment to seller: " + sellerAccountId)
                            .setStatementDescriptor("MARKETPLACE PURCHASE")
                            .setStatementDescriptorSuffix(sellerAccountId.substring(0, Math.min(10, sellerAccountId.length())));
            if (customerId != null && !customerId.isBlank()) {
                builder.setCustomer(customerId);
                builder.putMetadata("customer_id", customerId);
            }
            PaymentIntent intent = PaymentIntent.create(builder.build(), requestOptions);
            return Map.of("paymentIntentId", intent.getId(), "clientSecret", intent.getClientSecret());
        } catch (Exception e) {
            throw new RuntimeException("Payment failed");
        }
    }

    @Override
    public Map<String, String> payout(String sellerAccountId, Long amount, String currency) {
        try {
            PayoutCreateParams params =
                    PayoutCreateParams.builder()
                            .setAmount(amount)
                            .setCurrency(currency.toLowerCase())
                            .setDescription("Seller payout")
                            .putMetadata("seller_id", sellerAccountId)
                            .putMetadata("timestamp", String.valueOf(System.currentTimeMillis()))
                            .build();
            RequestOptions options =
                    RequestOptions.builder()
                            .setStripeAccount(sellerAccountId)
                            .setIdempotencyKey(generateIdempotencyKey(sellerAccountId, amount, currency))
                            .build();
            Payout payout = Payout.create(params, options);
            return Map.of("payoutId", payout.getId());
        } catch (Exception e) {
            throw new RuntimeException("Payout failed");
        }
    }

    @Override
    public Map<String, String> refund(String paymentIntentId, Long amount, String currency, String customerId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            String status = paymentIntent.getStatus();
            if ("succeeded".equals(status) || "requires_capture".equals(status)) {
                throw new IllegalStateException(
                        "Payment cannot be refunded. Status: " + paymentIntent.getStatus()
                );
            }
            Long refundAmount = (amount != null) ? amount : paymentIntent.getAmount();
            Long amountRefunded = paymentIntent.getAmountReceived() -
                    (paymentIntent.getAmountReceived() != null ?
                            paymentIntent.getAmountReceived() : 0L);
            if (refundAmount > amountRefunded) {
                throw new IllegalArgumentException(
                        "Refund amount exceeds available amount. Available: " + amountRefunded
                );
            }
            RequestOptions requestOptions = RequestOptions.builder()
                    .setIdempotencyKey(generateIdempotencyKey(paymentIntentId, amount, currency))
                    .build();
            RefundCreateParams.Builder builder =
                    RefundCreateParams.builder()
                            .setPaymentIntent(paymentIntentId)
                            .setAmount(amount)
                            .setCurrency(currency)
                            // Hoàn lại phí platform cho seller
                            .setRefundApplicationFee(true)
                            // Đảo ngược transfer về seller (lấy tiền lại từ seller)
                            .setReverseTransfer(true)
                            .putMetadata("original_payment_intent", paymentIntentId)
                            .putMetadata("refund_amount", String.valueOf(refundAmount))
                            .putMetadata("refund_type", amount == null ? "full" : "partial")
                            .putMetadata("timestamp", String.valueOf(System.currentTimeMillis()))
                            .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER);
            if (customerId != null && !customerId.isBlank()) {
                builder.setCustomer(customerId);
                builder.putMetadata("customer_id", customerId);
            }
            if (paymentIntent.getMetadata() != null &&
                    paymentIntent.getMetadata().containsKey("seller_account_id")) {
                builder.putMetadata("seller_account_id",
                        paymentIntent.getMetadata().get("seller_account_id"));
            }
            Refund refund = Refund.create(builder.build(), requestOptions);
            return Map.of("refundId", refund.getId());
        } catch (Exception e) {
            throw new RuntimeException("Refund failed");
        }
    }

    private String generateIdempotencyKey(String accountId, Long amount, String currency) {
        String data = accountId + amount + currency + System.currentTimeMillis();
        return UUID.nameUUIDFromBytes(data.getBytes()).toString();
    }
}
