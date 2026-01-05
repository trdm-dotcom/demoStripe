package com.example.demoStripe.services;

import com.stripe.exception.StripeException;
import com.stripe.model.v2.core.Account;
import com.stripe.model.v2.core.AccountLink;
import org.springframework.data.util.Pair;

import java.util.Map;

public interface ISellerService {
    Pair<Account, AccountLink> createAccount(String email, String country) throws StripeException;
    Map<String, Object> accountStatus(String sellerAccountId) throws StripeException;
}
