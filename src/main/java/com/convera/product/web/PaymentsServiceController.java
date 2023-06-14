package com.convera.product.web;

import com.convera.avro.schemas.PaymentEvent;
import com.convera.common.template.CommonResponse;
import com.convera.common.template.response.error.constants.ResponseErrorCode500;
import com.convera.common.template.response.util.CommonResponseUtil;
import com.convera.payments.data.model.PaymentDetailsAndPayeeesResponseBean;
import com.convera.payments.data.model.PaymentDetailsRequest;
import com.convera.product.configuration.KafkaCustomConsumer;
import com.convera.product.configuration.PaymentEventService;
import com.convera.product.constants.ErrorConstants;
import com.convera.product.service.PaymentsService;
import com.convera.product.utils.AvroPaymentEventMapper;
import com.convera.product.utils.CommonResponseCustomDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import datadog.trace.api.Trace;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@RestController
@Slf4j
@RequestMapping("convera")
@Tag(name = "the payments service api")
@CrossOrigin("http://localhost:3000")
public class PaymentsServiceController {

    @Autowired
    PaymentsService paymentsService;

    @Autowired
    @Qualifier("fixedThreadPool")
    private ExecutorService executorService;
    /**
     *
     * @param paymentDetailsRequest
     * @param correlationId
     * @param
     * @return
     */
    @Operation(
            operationId = "getPayments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "payment response", content = {
                            @Content(mediaType = "application/json", array =
                            @ArraySchema(schema = @Schema(implementation = PaymentDetailsAndPayeeesResponseBean.class)))
                    }),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json", array =
                            @ArraySchema(schema = @Schema(implementation = CommonResponse.class)))
                    }),
                    @ApiResponse(responseCode = "500", description = "unexpected error", content = {
                            @Content(mediaType = "application/json", array =
                            @ArraySchema(schema = @Schema(implementation = CommonResponse.class)))
                    })
            }
    )
    @PostMapping("payments")
    @Trace
    public ResponseEntity<?> getPayments(
            @Valid @NotNull @RequestBody(required = true) PaymentDetailsRequest paymentDetailsRequest,
            @RequestParam(required = false, defaultValue="1") int startIndex,
            @RequestParam(required = false, defaultValue="100") int pageSize,
            @RequestHeader(value = "correlationId", required = false
            ) String correlationId,
            HttpServletRequest httpServletRequest) {
        PaymentDetailsAndPayeeesResponseBean paymentDetails = PaymentDetailsAndPayeeesResponseBean.builder().build();
        try {
            paymentDetails = paymentsService.getPaymentDetails(paymentDetailsRequest, startIndex, pageSize, correlationId);

            return ResponseEntity.ok(CommonResponseUtil.createResponse200(paymentDetails.getData(), httpServletRequest.getRequestURI(),
                    correlationId));

        } catch (HttpClientErrorException ex) {
            log.error("Payments statusCode: {}, Uri: {} , ex: {}", ex.getStatusCode().value(), httpServletRequest.getRequestURI(), ex.getResponseBodyAsString());
            return clientErrorHandler(ex, httpServletRequest, correlationId);
        } catch (Exception ex) {
            log.error("Payments something went wrong", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponseUtil.createResponse500(null, httpServletRequest.getRequestURI(), correlationId,
                            Collections.singletonList(ResponseErrorCode500.ERR_50000.build("PaymentsService",
                                    ErrorConstants.PAYMENT_DATA_ERROR))));
        }
    }

    private ResponseEntity<?> clientErrorHandler(HttpClientErrorException ex, HttpServletRequest httpServletRequest, String correlationId) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(CommonResponse.class, new CommonResponseCustomDeserializer());
        objectMapper.registerModule(simpleModule);
        CommonResponse commonResponse = null;
        try {
            commonResponse = objectMapper.readValue(ex.getResponseBodyAsString(),
                    CommonResponse.class);
        } catch (JsonProcessingException e) {
            log.warn("Error while processing ResponseBody:", e);
        }

        if (ex instanceof HttpClientErrorException.NotFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponseUtil.createResponse404(null, httpServletRequest.getRequestURI(), correlationId, commonResponse.getMetadata().getErrors()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponseUtil.createResponse400(null, httpServletRequest.getRequestURI(), correlationId, commonResponse.getMetadata().getErrors()));
        }


    }

    @Autowired
    ConsumerFactory<String, PaymentEvent> consumerFactory;

    @Autowired
    PaymentEventService paymentEventService;

    @Operation(
            operationId = "getLastPayments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "payment response", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class))
                    }),
                    @ApiResponse(responseCode = "500", description = "unexpected error", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CommonResponse.class))
                    })
            }
    )
    @GetMapping("payments/last")
    @Trace
    public ResponseEntity<CommonResponse<?>> getLastPayments(
            @Parameter(description = "account account id", example = "63d372ef-3b68-4977-9602-f7a48c7756ac")
            @RequestParam(required = true) String account,
            @RequestHeader(value = "correlationId", required = false
            ) String correlationId,
            @RequestHeader(value = "enableProducer", required = false
            ) boolean enableProducer,
            HttpServletRequest request) {

        try {

            //this is to send a new message to the topic to test will be removed
            if(enableProducer){
                paymentEventService.sendMessageWith_topicName(account);
            }


            Callable<Set<PaymentEvent>> callable = new KafkaCustomConsumer(consumerFactory, "PaymentEvent" );


            Set<PaymentEvent> result = new HashSet<>();
            try {
                result = executorService.submit(callable).get();
                log.info("-----------fetch messages: " + new Date() + "::" + result);
            } catch (InterruptedException | ExecutionException ex) {
                log.error("Something went wrong", ex);
            }

            return ResponseEntity.ok(CommonResponseUtil.createResponse200(
                    AvroPaymentEventMapper.toPaymentEventLit(result), request.getRequestURI() + "?account="+account, correlationId
                    )
            );
        } catch (Exception ex) {
            log.error("Something went wrong", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponseUtil.createResponse500(List.of(), request.getRequestURI(), correlationId,
                            Collections.singletonList(ResponseErrorCode500.ERR_50000.build("payments-service",
                                    "Error in getting last records from the Topic. Message: " + ex.getMessage()))));
        }
    }

    private class PaymentEventResponse extends CommonResponse<List<com.convera.product.model.PaymentEvent>> {
    }
}
