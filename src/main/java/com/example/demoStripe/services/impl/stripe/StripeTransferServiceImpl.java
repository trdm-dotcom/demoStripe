package com.example.demoStripe.services.impl.stripe;

import com.example.demoStripe.services.ITransferService;
import com.stripe.model.Transfer;
import com.stripe.net.RequestOptions;
import com.stripe.param.TransferCreateParams;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class StripeTransferServiceImpl implements ITransferService {
    @Override
    public Map<String, String> createTransfer(String sellerAccountId, Long amount, String currency) {
        try {
            String idempotencyKey = generateIdempotencyKey(sellerAccountId, amount, currency);

            RequestOptions requestOptions = RequestOptions.builder()
                    .setIdempotencyKey(idempotencyKey)
                    .build();

            TransferCreateParams params =
                    TransferCreateParams.builder()
                            .setAmount(amount)
                            .setCurrency(currency.toLowerCase())
                            .setDestination(sellerAccountId)
                            .setDescription("Platform bonus")
                            .putMetadata("seller_id", sellerAccountId)
                            .putMetadata("payment_type", "bonus")
                            .putMetadata("timestamp", String.valueOf(System.currentTimeMillis()))
                            .build();
            Transfer transfer = Transfer.create(params, requestOptions);
            return Map.of("transferId", transfer.getId());
        } catch (Exception e) {
            throw new RuntimeException("Transfer failed");
        }
    }

    private String generateIdempotencyKey(String accountId, Long amount, String currency) {
        String data = accountId + amount + currency + System.currentTimeMillis();
        return UUID.nameUUIDFromBytes(data.getBytes()).toString();
    }
}
