package com.convera.product.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("com.convera.payments")
public class PaymentsApiPropertiesConfiguration {

    private String url;
    private Long connectionTimeout;
    private Long readTimeout;

}
