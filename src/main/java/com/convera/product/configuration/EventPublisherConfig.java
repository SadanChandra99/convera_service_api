package com.convera.product.configuration;

import com.amazonaws.services.schemaregistry.deserializers.GlueSchemaRegistryKafkaDeserializer;
import com.amazonaws.services.schemaregistry.utils.AWSSchemaRegistryConstants;
import com.amazonaws.services.schemaregistry.utils.AvroRecordType;
import com.convera.avro.schemas.PaymentEvent;
import com.convera.kafka.EventPublisherConfigBean;
import com.convera.kafka.EventPublisherService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Configuration
public class EventPublisherConfig {
  @Value("${spring.kafka.consumer.group-id}")
  private String groupId;
  private final EventPublisherKafkaProps eventPublisherKafkaProps;

  @Bean
  public EventPublisherService<PaymentEvent> eventPublisherOrderService() {
    EventPublisherConfigBean configBean =
        EventPublisherConfigBean.builder()
            .bootstrapServer(eventPublisherKafkaProps.getBootstrapUrl())
            .region(Objects.requireNonNullElse(eventPublisherKafkaProps.getRegion(), null))
            .registryName(eventPublisherKafkaProps.getRegistryName())
            .build();
    return new EventPublisherService<>(configBean);
  }

  @Bean
  public ConsumerFactory<String, PaymentEvent> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, eventPublisherKafkaProps.getBootstrapUrl());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        GlueSchemaRegistryKafkaDeserializer.class.getName());

    props.put(AWSSchemaRegistryConstants.AWS_REGION, eventPublisherKafkaProps.getRegion());
    props.put(AWSSchemaRegistryConstants.REGISTRY_NAME, eventPublisherKafkaProps.getRegistryName());
    props.put(
        AWSSchemaRegistryConstants.AVRO_RECORD_TYPE, AvroRecordType.SPECIFIC_RECORD.getName());
    props.put("security.protocol", "SASL_SSL");
    props.put("sasl.mechanism", "AWS_MSK_IAM");
    props.put(
        "sasl.jaas.config",
        "software.amazon.msk.auth.iam.IAMLoginModule required awsDebugCreds=true;");
    props.put(
        "sasl.client.callback.handler.class",
        "software.amazon.msk.auth.iam.IAMClientCallbackHandler");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);

    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean("fixedThreadPool")
  public ExecutorService fixedThreadPool() {
    return Executors.newFixedThreadPool(1);
  }
}
