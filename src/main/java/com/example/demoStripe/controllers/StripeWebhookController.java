package com.example.demoStripe.controllers;

import com.example.demoStripe.configs.StripeConfig;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@Slf4j
public class StripeWebhookController {
    private final StripeConfig stripeConfig;

    public StripeWebhookController (
            StripeConfig config
    ) {
        this.stripeConfig = config;
    }

    @PostMapping("/stripe")
    public ResponseEntity<?> handle(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws Exception {
        Event event = null;

        // Replace this endpoint secret with your endpoint's unique secret
        // If you are testing with the CLI, find the secret by running 'stripe listen'
        // If you are using an endpoint defined with the API or dashboard, look in your webhook settings
        // at https://dashboard.stripe.com/webhooks

        try {
            event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    this.stripeConfig.getEndpointSecret()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("");
        }

        // Handle the event
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        if (deserializer.getObject().isEmpty()) {
            return ResponseEntity.ok("No object");
        }

        StripeObject stripeObject = deserializer.getObject().get();

        switch (event.getType()) {
            case "checkout.session.created": {
                Session session = (Session) stripeObject;
                log.info("[Checkout] Created: " + session.getId());
                break;
            }

            case "checkout.session.completed": {
                Session session = (Session) stripeObject;
                log.info("[Checkout] Completed: " + session.getId());
                break;
            }

            case "checkout.session.async_payment_succeeded": {
                Session session = (Session) stripeObject;
                log.info("[Checkout] Async succeeded: " + session.getId());
                break;
            }

            case "checkout.session.async_payment_failed": {
                Session session = (Session) stripeObject;
                log.info("[Checkout] Async failed: " + session.getId());
                break;
            }

            case "checkout.session.expired": {
                Session session = (Session) stripeObject;
                log.info("[Checkout] Expired: " + session.getId());
                break;
            }

            case "payment_intent.created": {
                PaymentIntent pi = (PaymentIntent) stripeObject;
                log.info("[PI] Created: " + pi.getId());
                break;
            }

            case "payment_intent.processing": {
                PaymentIntent pi = (PaymentIntent) stripeObject;
                log.info("[PI] Processing: " + pi.getId());
                break;
            }

            case "payment_intent.requires_action": {
                PaymentIntent pi = (PaymentIntent) stripeObject;
                log.info("[PI] Requires action: " + pi.getId());
                break;
            }

            case "payment_intent.succeeded": {
                PaymentIntent pi = (PaymentIntent) stripeObject;
                log.info("[PI] Succeeded: " + pi.getId());

                String type = pi.getMetadata().get("type");
                if ("TOP_UP".equals(type)) {
                    Long userId = Long.valueOf(pi.getMetadata().get("user_id"));
                    log.info("Top-up wallet user=" + userId + " amount=" + pi.getAmount());
                }
                break;
            }

            case "payment_intent.payment_failed": {
                PaymentIntent pi = (PaymentIntent) stripeObject;
                log.info("[PI] Failed: " + pi.getId());
                break;
            }

            case "payment_intent.canceled": {
                PaymentIntent pi = (PaymentIntent) stripeObject;
                log.info("[PI] Canceled: " + pi.getId());
                break;
            }

            case "charge.succeeded": {
                Charge charge = (Charge) stripeObject;
                log.info("[Charge] Succeeded: " + charge.getId());
                break;
            }

            case "charge.failed": {
                Charge charge = (Charge) stripeObject;
                log.info("[Charge] Failed: " + charge.getId());
                break;
            }

            case "charge.refunded": {
                Charge charge = (Charge) stripeObject;
                log.info("[Charge] Refunded: " + charge.getId());
                break;
            }

            case "charge.dispute.created": {
                Dispute dispute = (Dispute) stripeObject;
                log.info("[Dispute] Created: " + dispute.getId());
                break;
            }

            case "charge.dispute.closed": {
                Dispute dispute = (Dispute) stripeObject;
                log.info("[Dispute] Closed: " + dispute.getId());
                break;
            }

            case "refund.created": {
                Refund refund = (Refund) stripeObject;
                log.info("[Refund] Created: " + refund.getId());
                break;
            }

            case "refund.updated": {
                Refund refund = (Refund) stripeObject;
                log.info("[Refund] Status: " + refund.getStatus());
                break;
            }

            case "transfer.created": {
                Transfer transfer = (Transfer) stripeObject;
                log.info("[Transfer] Created: " + transfer.getId());
                break;
            }

            case "transfer.reversed": {
                Transfer transfer = (Transfer) stripeObject;
                log.info("[Transfer] Reversed: " + transfer.getId());
                break;
            }

            case "payout.created": {
                Payout payout = (Payout) stripeObject;
                log.info("[Payout] Created: " + payout.getId());
                break;
            }

            case "payout.paid": {
                Payout payout = (Payout) stripeObject;
                log.info("[Payout] Paid: " + payout.getId());
                break;
            }

            case "payout.failed": {
                Payout payout = (Payout) stripeObject;
                log.info("[Payout] Failed: " + payout.getId());
                break;
            }

            case "payout.canceled": {
                Payout payout = (Payout) stripeObject;
                log.info("[Payout] Canceled: " + payout.getId());
                break;
            }

            case "customer.subscription.created": {
                Subscription sub = (Subscription) stripeObject;
                log.info("[Sub] Created: " + sub.getId());
                break;
            }

            case "customer.subscription.updated": {
                Subscription sub = (Subscription) stripeObject;
                log.info("[Sub] Updated: " + sub.getId());
                break;
            }

            case "customer.subscription.deleted": {
                Subscription sub = (Subscription) stripeObject;
                log.info("[Sub] Deleted: " + sub.getId());
                break;
            }

            case "customer.subscription.trial_will_end": {
                Subscription sub = (Subscription) stripeObject;
                log.info("[Sub] Trial will end: " + sub.getId());
                break;
            }

            case "invoice.finalized": {
                Invoice invoice = (Invoice) stripeObject;
                log.info("[Invoice] Finalized: " + invoice.getId());
                break;
            }

            case "invoice.payment_succeeded": {
                Invoice invoice = (Invoice) stripeObject;
                log.info("[Invoice] Payment succeeded: " + invoice.getId());
                break;
            }

            case "invoice.payment_failed": {
                Invoice invoice = (Invoice) stripeObject;
                log.info("[Invoice] Payment failed: " + invoice.getId());
                break;
            }

            default:
                log.info("Unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok("ok");
    }
}
