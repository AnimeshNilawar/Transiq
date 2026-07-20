package com.moddynerd.transiq.payment.gateway.routing;

public enum RoutingReason {
    PRIMARY_NETWORK,
    MANUAL_OVERRIDE,
    NETWORK_FAILOVER,
    BANK_FAILOVER,
    MERCHANT_POLICY,
    HEALTH_POLICY
}
