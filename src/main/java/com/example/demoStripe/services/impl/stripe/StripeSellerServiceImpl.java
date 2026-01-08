package com.example.demoStripe.services.impl.stripe;

import com.example.demoStripe.configs.StripeConfig;
import com.example.demoStripe.services.ISellerService;
import com.stripe.StripeClient;
import com.stripe.model.v2.core.Account;
import com.stripe.model.v2.core.AccountLink;
import com.stripe.param.v2.core.AccountCreateParams;
import com.stripe.param.v2.core.AccountLinkCreateParams;
import com.stripe.param.v2.core.AccountRetrieveParams;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StripeSellerServiceImpl implements ISellerService {
    private final StripeClient v2Client;

    public StripeSellerServiceImpl(StripeConfig config) {
        this.v2Client = new StripeClient(config.getSecretKey());
    }

    @Override
    public Map<String, String> createAccount(String email, String country) {
        try {
            AccountCreateParams params = AccountCreateParams.builder()
                    .setContactEmail(email)
                    .setDisplayName(email)
                    .setIdentity(
                            AccountCreateParams.Identity.builder()
                                    .setCountry(country)
                                    .setEntityType(AccountCreateParams.Identity.EntityType.COMPANY)
                                    .build()
                    )
                    .setConfiguration(
                            AccountCreateParams.Configuration.builder()
                                    .setRecipient(
                                            AccountCreateParams.Configuration.Recipient.builder()
                                                    .setCapabilities(
                                                            AccountCreateParams.Configuration.Recipient.Capabilities.builder()
                                                                    .setStripeBalance(
                                                                            AccountCreateParams.Configuration.Recipient.Capabilities.StripeBalance.builder()
                                                                                    .setStripeTransfers(
                                                                                            AccountCreateParams.Configuration.Recipient.Capabilities.StripeBalance.StripeTransfers.builder()
                                                                                                    .setRequested(true)
                                                                                                    .build()
                                                                                    )
                                                                                    .build()
                                                                    )
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .setDefaults(
                            AccountCreateParams.Defaults.builder()
                                    .setResponsibilities(
                                            AccountCreateParams.Defaults.Responsibilities.builder()
                                                    .setFeesCollector(AccountCreateParams.Defaults.Responsibilities.FeesCollector.APPLICATION)
                                                    .setLossesCollector(AccountCreateParams.Defaults.Responsibilities.LossesCollector.APPLICATION)
                                                    .build()
                                    )
                                    .build()
                    )
                    .setDashboard(AccountCreateParams.Dashboard.EXPRESS).build();
            Account account = this.v2Client.v2().core().accounts().create(params);

            AccountLinkCreateParams linkParams = AccountLinkCreateParams.builder()
                    .setAccount(account.getId())
                    .setUseCase(
                            AccountLinkCreateParams.UseCase.builder()
                                    .setType(AccountLinkCreateParams.UseCase.Type.ACCOUNT_ONBOARDING)
                                    .setAccountOnboarding(
                                            AccountLinkCreateParams.UseCase.AccountOnboarding.builder()
                                                    .addConfiguration(AccountLinkCreateParams.UseCase.AccountOnboarding.Configuration.RECIPIENT)
                                                    .addConfiguration(AccountLinkCreateParams.UseCase.AccountOnboarding.Configuration.MERCHANT)
                                                    .setRefreshUrl("http://localhost:8080")
                                                    .setReturnUrl("http://localhost:8080?accountId=" + account.getId())
                                                    .build()
                                    )
                                    .build()
                    ).build();
            AccountLink accountLink = this.v2Client.v2().core().accountLinks().create(linkParams);
            return Map.of(
                    "stripeAccountId", account.getId(),
                    "onboardingUrl", accountLink.getUrl()
            );
        } catch (Exception e) {
            throw new RuntimeException("Create account failed");
        }
    }

    @Override
    public Map<String, Object> accountStatus(String sellerAccountId) {
        try {
            AccountRetrieveParams retrieveParams =
                    AccountRetrieveParams.builder()
                            .addInclude(AccountRetrieveParams.Include.REQUIREMENTS)
                            .addInclude(AccountRetrieveParams.Include.CONFIGURATION__RECIPIENT)
                            .build();
            Account account = v2Client.v2().core().accounts().retrieve(sellerAccountId, retrieveParams);
            boolean payoutsEnabled = false;
            boolean chargesEnabled = false;
            try {
                payoutsEnabled =
                        account.getConfiguration().getRecipient().getCapabilities().getStripeBalance().getPayouts().getStatus().equals("active");
            } catch (Exception ignored) {
            }
            try {
                chargesEnabled =
                        account.getConfiguration().getRecipient().getCapabilities().getStripeBalance().getStripeTransfers().getStatus().equals("active");
            } catch (Exception ignored) {
            }
            String summaryStatus = null;
            try {
                summaryStatus = account.getRequirements().getSummary().getMinimumDeadline().getStatus();
            } catch (Exception ignored) {
            }
            boolean detailsSubmitted = (summaryStatus == null) || summaryStatus.equals("eventually_due");
            return Map.of(
                    "id", account.getId(),
                    "payoutsEnabled", payoutsEnabled,
                    "chargesEnabled", chargesEnabled,
                    "detailsSubmitted", detailsSubmitted,
                    "requirements", account.getRequirements() != null ? account.getRequirements().getEntries() : null
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
