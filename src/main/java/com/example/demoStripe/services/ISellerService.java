package com.example.demoStripe.services;

import java.util.Map;

public interface ISellerService {
    Map<String, String> createAccount(String email, String country);
    Map<String, Object> accountStatus(String sellerAccountId);
}
