package com.convera.product.configuration;

import com.convera.product.data.api.PaymentsApi;

import com.convera.product.data.api.ProductsApi;
import com.convera.product.data.invoker.ApiClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ProductDataApiConfiguration {

    private final ProductDataApiPropertiesConfiguration productDataApiProperties;

    public ProductDataApiConfiguration(ProductDataApiPropertiesConfiguration productDataApiProperties) {
        this.productDataApiProperties = productDataApiProperties;
    }

    private RestTemplate getRestTemplateProps() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplateBuilder.rootUri(productDataApiProperties.getBaseUrl());
        restTemplateBuilder.setConnectTimeout(Duration.ofMillis(productDataApiProperties.getConnectionTimeout()));
        restTemplateBuilder.setReadTimeout(Duration.ofMillis(productDataApiProperties.getReadTimeout()));
        return restTemplateBuilder.build();
    }


    @Bean("ProductsApi")
    @Primary
    public ProductsApi getProductsApi() {
        ProductsApi productsApi = new ProductsApi();
        ApiClient apiClient = new ApiClient(getRestTemplateProps());
        productsApi.setApiClient(apiClient);
        return productsApi;
    }

    @Bean("PaymentsApi")
    @Primary
    public PaymentsApi getPaymentsApi() {
        PaymentsApi paymentsApi = new PaymentsApi();
        ApiClient apiClient = new ApiClient(getRestTemplateProps());
        paymentsApi.setApiClient(apiClient);
        return paymentsApi;
    }
}
