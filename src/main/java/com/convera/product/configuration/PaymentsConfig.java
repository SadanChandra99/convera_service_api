package com.convera.product.configuration;

import com.convera.payments.data.api.PaymentApi;
import com.convera.payments.data.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.Duration;

@Component
public class PaymentsConfig {

    @Autowired
    private PaymentsApiPropertiesConfiguration paymentsApiPropertiesConfiguration;

    private RestTemplate getRestTemplateProps() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(paymentsApiPropertiesConfiguration.getConnectionTimeout()))
                .setReadTimeout(Duration.ofMillis(paymentsApiPropertiesConfiguration.getReadTimeout())).build();

    }

    @Bean
    public PaymentApi getPaymentApi() {
        ApiClient apiClient = new ApiClient(getRestTemplateProps());
        apiClient.setBasePath(paymentsApiPropertiesConfiguration.getUrl());
        return new PaymentApi(apiClient);
    }
}
