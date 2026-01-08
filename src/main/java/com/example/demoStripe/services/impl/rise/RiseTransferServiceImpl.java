package com.example.demoStripe.services.impl.rise;

import com.example.demoStripe.services.ITransferService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RiseTransferServiceImpl implements ITransferService {
    @Override
    public Map<String, String> createTransfer(String sellerAccountId, Long amount, String currency) {
        return Map.of();
    }
}
