package com.convera.product.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("convera.event.publisher")
public class EventPublisherKafkaProps {

    private String bootstrapUrl;
    private String region;
    private String registryName;
}
