package com.bialem.backend.store.service.payment;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PaymentProviderFactory {

    private final List<PaymentProvider> providers;

    public PaymentProviderFactory(List<PaymentProvider> providers) {
        this.providers = providers;
    }

    public PaymentProvider getProvider(String name) {
        return providers.stream().filter(p -> p.supports(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("Bilinmeyen ödeme sağlayıcısı: " + name));
    }
}
