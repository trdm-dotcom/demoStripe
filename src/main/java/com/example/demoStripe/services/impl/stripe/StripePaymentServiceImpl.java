package com.example.demoStripe.services.impl.stripe;

import com.example.demoStripe.services.IPaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PayoutCreateParams;
import com.stripe.param.RefundCreateParams;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripePaymentServiceImpl implements IPaymentService {
    @Override
    public PaymentIntent createPayment(Long amount, String currency, String sellerAccountId, String customerId, List<String> paymentMethod) throws StripeException {
        PaymentIntentCreateParams.Builder builder =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setApplicationFeeAmount((long) (amount * 0.05))
                        .setCurrency(currency)
                        .setApplicationFeeAmount(1L)
                        .setTransferData(
                                PaymentIntentCreateParams.TransferData.builder()
                                        .setDestination(sellerAccountId)
                                        .build())
                        .addAllPaymentMethodType(paymentMethod);
        if (customerId != null && !customerId.isBlank()) {
            builder.setCustomer(customerId);
        }
        return PaymentIntent.create(builder.build());
    }

    @Override
    public Payout payout(String sellerAccountId, Long amount, String currency) throws StripeException {
        PayoutCreateParams params =
                PayoutCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .build();
        RequestOptions options =
                RequestOptions.builder()
                        .setStripeAccount(sellerAccountId)
                        .build();
        return Payout.create(params, options);
    }

    @Override
    public Refund refund(String paymentIntentId, Long amount, String currency, String customerId) throws StripeException {
        RefundCreateParams.Builder builder =
                RefundCreateParams.builder()
                        .setPaymentIntent(paymentIntentId)
                        .setAmount(amount)
                        .setCurrency(currency)
                        .setRefundApplicationFee(true)
                        .setReverseTransfer(true);
        if (customerId != null && !customerId.isBlank()) {
            builder.setCustomer(customerId);
        }

        return Refund.create(builder.build());
    }
}
