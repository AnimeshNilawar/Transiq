package com.moddynerd.transiq.payment.gateway;

import com.moddynerd.transiq.payment.gateway.acquirer.*;
import com.moddynerd.transiq.payment.gateway.bank.Bank;
import com.moddynerd.transiq.payment.gateway.bank.decision.BankDecisionEngine;
import com.moddynerd.transiq.payment.gateway.bank.implementation.AxisBank;
import com.moddynerd.transiq.payment.gateway.bank.implementation.HdfcBank;
import com.moddynerd.transiq.payment.gateway.bank.implementation.IciciBank;
import com.moddynerd.transiq.payment.gateway.bank.implementation.SbiBank;
import com.moddynerd.transiq.payment.gateway.bank.simulator.AuthorizationSimulator;
import com.moddynerd.transiq.payment.gateway.common.AuthorizationCodeGenerator;
import com.moddynerd.transiq.payment.gateway.common.RandomAuthorizationCodeGenerator;
import com.moddynerd.transiq.payment.gateway.payment.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Bean
    public AuthorizationCodeGenerator authorizationCodeGenerator() {
        return new RandomAuthorizationCodeGenerator();
    }

    @Bean
    public BankDecisionEngine bankDecisionEngine() {
        return new BankDecisionEngine();
    }

    @Bean
    public AuthorizationSimulator authorizationSimulator(
            BankDecisionEngine bankDecisionEngine,
            AuthorizationCodeGenerator authorizationCodeGenerator
    ) {
        return new AuthorizationSimulator(
                bankDecisionEngine,
                authorizationCodeGenerator
        );
    }

    @Bean
    public Bank hdfcBank(AuthorizationSimulator simulator) {
        return new HdfcBank(simulator);
    }

    @Bean
    public Bank iciciBank(AuthorizationSimulator simulator) {
        return new IciciBank(simulator);
    }

    @Bean
    public Bank axisBank(AuthorizationSimulator simulator) {
        return new AxisBank(simulator);
    }

    @Bean
    public Bank sbiBank(AuthorizationSimulator simulator) {
        return new SbiBank(simulator);
    }

    @Bean
    public AcquiringBank hdfcAcquiringBank() {
        return new HdfcAcquiringBank();
    }

    @Bean
    public AcquiringBank iciciAcquiringBank() {
        return new IciciAcquiringBank();
    }

    @Bean
    public AcquiringBank axisAcquiringBank() {
        return new AxisAcquiringBank();
    }

    @Bean
    public AcquiringBank sbiAcquiringBank() {
        return new SbiAcquiringBank();
    }

    @Bean
    public AcquiringBank kotakAcquiringBank() {
        return new KotakAcquiringBank();
    }

    @Bean
    public PaymentHandler upiPaymentHandler(AuthorizationCodeGenerator codeGenerator) {
        return new UpiPaymentHandler(codeGenerator);
    }

    @Bean
    public PaymentHandler netBankingPaymentHandler(AuthorizationCodeGenerator codeGenerator) {
        return new NetBankingPaymentHandler(codeGenerator);
    }

    @Bean
    public PaymentHandler walletPaymentHandler(AuthorizationCodeGenerator codeGenerator) {
        return new WalletPaymentHandler(codeGenerator);
    }

}
