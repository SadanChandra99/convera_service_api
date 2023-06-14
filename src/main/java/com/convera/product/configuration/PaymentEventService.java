package com.convera.product.configuration;

import com.convera.avro.schemas.PaymentEvent;
import com.convera.kafka.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class PaymentEventService {

  @Autowired
  private final EventPublisherService<PaymentEvent> eventPublisherOrderService;
  @Value("${spring.kafka.consumer.topic}")
  private String topicName;

  /**
   * send message providing only topicName
   * schema name should be resolved as same name of topicName
   * @param eventId
   */
  public void sendMessageWith_topicName(String eventId) {
    PaymentEvent event = getValidPaymentEvent( eventId);
    log.info(String.format("sending message providing just Topic: %s", topicName));
    eventPublisherOrderService.sendMessage(topicName, event);
  }

  private PaymentEvent getValidPaymentEvent(String eventId) {
    return PaymentEvent.newBuilder().setEventId(eventId)
            .setPaymentConfirmationNumber(UUID.randomUUID().toString())
            .setEventType("Payment")
            .setTransactionAccountId(UUID.randomUUID().toString())
            .setParentAccountId(UUID.randomUUID().toString())
            .setOccurredOn(Instant.now())
            .setExternalPaymentId(UUID.randomUUID().toString())
            .setPaymentTransactionStatus(PaymentEventStatus.random().name())
            .setPaymentTransactionStatusReason("NO COMMENT")
            .setReceiveAmount(new BigDecimal(100L))
            .setReceiveCurrencyCode("CAD")
            .setPayeeId(UUID.randomUUID().toString())
            .setPayeeName("John Doe")
            .setPaymentConfirmationNumber(UUID.randomUUID().toString())
            .build();
  }

}
