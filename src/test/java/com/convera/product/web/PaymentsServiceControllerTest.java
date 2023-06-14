package com.convera.product.web;

import com.convera.avro.schemas.PaymentEvent;
import com.convera.payments.data.model.PaymentDetailsAndPayeeesResponseBean;
import com.convera.product.ReadJsonUtil;
import com.convera.product.configuration.KafkaCustomConsumer;
import com.convera.product.configuration.PaymentEventService;
import com.convera.product.configuration.PaymentEventStatus;
import com.convera.product.service.PaymentsService;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentsServiceController.class)
public class PaymentsServiceControllerTest {


    private static final String BASE_PATH = "/convera";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PaymentsService paymentsService;

    @MockBean
    ConsumerFactory<String, PaymentEvent> consumerFactory;
    @MockBean
    Consumer<String,PaymentEvent> consumer;
    @MockBean
    PaymentEventService paymentEventService;

    @MockBean
    @Qualifier("fixedThreadPool")
    private ExecutorService executorService;

    String startIndex = "1";
    String pageSize = "100";

    @Test
    void getPayments_success() throws Exception {
        PaymentDetailsAndPayeeesResponseBean paymentDetailsAndPayeeesResponseBean = ReadJsonUtil.getFileContent("/ResponsePayment_200.json");
        String json = "{\n" +
                "  \"transactionAccountId\": \"85\"\n" +
                "}";

        when(paymentsService.getPaymentDetails(any(), anyInt(), anyInt(), any()))
                .thenReturn(paymentDetailsAndPayeeesResponseBean);

        MvcResult mvcResult = this.mockMvc.perform(
                        post(BASE_PATH + "/payments/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("startIndex",startIndex)
                                .queryParam("pageSize",pageSize)
                                .content(json))
                .andExpect(status().isOk()).andReturn();
        int statusCode = mvcResult.getResponse().getStatus();

        assertEquals(200, statusCode);
        assertNotNull(mvcResult.getResponse());
    }


        @Test
        void shouldThrowNotFound() throws Exception {
            String r = ReadJsonUtil.getFileContentString("/ResponsePayment_404.json");
            when(paymentsService.getPaymentDetails(any(), anyInt(), anyInt(), any()))
                    .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "", null, r.getBytes(StandardCharsets.UTF_8), null));

            String json = "{\n" +
                    "  \"transactionAccountId\": \"5\"\n" +
                    "}";

            this.mockMvc.perform(
                            post(BASE_PATH + "/payments/")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .queryParam("startIndex",startIndex)
                                    .queryParam("pageSize",pageSize)
                                    .content(json))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldThrowBadRequest() throws Exception {
            String r = ReadJsonUtil.getFileContentString("/ResponsePayment_400.json");

            when(paymentsService.getPaymentDetails(any(), anyInt(), anyInt(), any()))
                    .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "", null, r.getBytes(StandardCharsets.UTF_8), null));

            String json = "{\n" +
                    "  \"transactionAccountd\": \"5\"\n" +
                    "}";

            this.mockMvc.perform(
                            post(BASE_PATH + "/payments/")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .queryParam("startIndex",startIndex)
                                    .queryParam("pageSize",pageSize)
                                    .content(json))
                    .andExpect(status().isBadRequest());
        }

    @Test
    void shouldThrow500() throws Exception {
        when(paymentsService.getPaymentDetails(any(), anyInt(), anyInt(), any()))
                .thenThrow(HttpServerErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "", null, null, null));

        String json = "{\n" +
                "  \"transactionAccountId\": \"25\"\n" +
                "}";

        this.mockMvc.perform(
                        post(BASE_PATH + "/payments/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("startIndex",startIndex)
                                .queryParam("pageSize",pageSize)
                                .content(json))
                .andExpect(status().is5xxServerError());
    }




    @Test
    void getLastPayments_success() throws Exception {
        String validCorrelationId = UUID.randomUUID().toString();
        String validAccountId = "2e79bbaf-6e74-4e5b-b6de-499356caccf8";

        KafkaCustomConsumer kafkaCustomConsumer = Mockito.mock(KafkaCustomConsumer.class);

        when(consumerFactory.createConsumer())
                .thenReturn(consumer);

        ConsumerRecord<String, PaymentEvent> record = new ConsumerRecord<>("PaymentEvent",1,0l,"key1",getValidPaymentEvent(validAccountId));
        Map<TopicPartition, List<ConsumerRecord<String, PaymentEvent>>> recordsMap = new HashMap<>();
        TopicPartition topicPartition = new TopicPartition("PaymentEvent",1);
        recordsMap.put(topicPartition, List.of(record));

        ConsumerRecords<String,PaymentEvent> records = new ConsumerRecords<>(recordsMap);

        when(consumer.poll(any())).thenReturn(records);

        Set<PaymentEvent> eventSet = new HashSet<>();
        eventSet.add(getValidPaymentEvent(validAccountId));

        when(executorService.submit(any(Callable.class)))
                .thenReturn(ConcurrentUtils.constantFuture(eventSet));


        this.mockMvc.perform(get(BASE_PATH + "/payments/last")
                        .header("correlationId",validCorrelationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("account",validAccountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data.[0].eventId", Matchers.is(validAccountId)));
    }


    @Test
    void getLastPayments_exception() throws Exception {
        String validCorrelationId = UUID.randomUUID().toString();
        String validAccountId = "2e79bbaf-6e74-4e5b-b6de-499356caccf8";

        KafkaCustomConsumer kafkaCustomConsumer = Mockito.mock(KafkaCustomConsumer.class);

        when(consumerFactory.createConsumer())
                .thenReturn(consumer);

        ConsumerRecord<String, PaymentEvent> record = new ConsumerRecord<>("PaymentEvent",1,0l,"key1",getValidPaymentEvent(validAccountId));
        Map<TopicPartition, List<ConsumerRecord<String, PaymentEvent>>> recordsMap = new HashMap<>();
        TopicPartition topicPartition = new TopicPartition("PaymentEvent",1);
        recordsMap.put(topicPartition, List.of(record));

        ConsumerRecords<String,PaymentEvent> records = new ConsumerRecords<>(recordsMap);

        when(consumer.poll(any())).thenReturn(records);

        Set<PaymentEvent> eventSet = new HashSet<>();
        eventSet.add(getValidPaymentEvent(validAccountId));

        when(executorService.submit(any(Callable.class)))
                .thenThrow(new RuntimeException());


        this.mockMvc.perform(get(BASE_PATH + "/payments/last")
                        .header("correlationId",validCorrelationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("account",validAccountId))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getLastPayments_enablerOn() throws Exception {
        String validCorrelationId = UUID.randomUUID().toString();
        String validAccountId = "2e79bbaf-6e74-4e5b-b6de-499356caccf8";

        KafkaCustomConsumer kafkaCustomConsumer = Mockito.mock(KafkaCustomConsumer.class);

        when(consumerFactory.createConsumer())
                .thenReturn(consumer);

        ConsumerRecord<String, PaymentEvent> record = new ConsumerRecord<>("PaymentEvent",1,0l,"key1",getValidPaymentEvent(validAccountId));
        Map<TopicPartition, List<ConsumerRecord<String, PaymentEvent>>> recordsMap = new HashMap<>();
        TopicPartition topicPartition = new TopicPartition("PaymentEvent",1);
        recordsMap.put(topicPartition, List.of(record));

        ConsumerRecords<String,PaymentEvent> records = new ConsumerRecords<>(recordsMap);

        when(consumer.poll(any())).thenReturn(records);

        Set<PaymentEvent> eventSet = new HashSet<>();
        eventSet.add(getValidPaymentEvent(validAccountId));

        when(executorService.submit(any(Callable.class)))
                .thenReturn(ConcurrentUtils.constantFuture(eventSet));

        doNothing().when(paymentEventService).sendMessageWith_topicName(any());

        this.mockMvc.perform(get(BASE_PATH + "/payments/last")
                        .header("correlationId",validCorrelationId)
                        .header("enableProducer",true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("account",validAccountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.data.[0].eventId", Matchers.is(validAccountId)));

        verify(paymentEventService, times(1)).sendMessageWith_topicName(validAccountId);

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

