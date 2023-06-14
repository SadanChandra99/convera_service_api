package com.convera.product.web;

import com.convera.common.template.CommonResponse;
import com.convera.common.template.response.error.constants.ResponseErrorCode400;
import com.convera.common.template.response.error.constants.ResponseErrorCode404;
import com.convera.common.template.response.error.constants.ResponseErrorCode500;
import com.convera.common.template.response.util.CommonResponseUtil;
import com.convera.product.constants.QuotesApiConstants;
import com.convera.product.data.model.Quote;
import com.convera.product.data.model.QuoteRequest;
import com.convera.product.exception.DataNotFoundException;
import com.convera.product.service.QuickQuotesService;
import datadog.trace.api.Trace;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("convera/quick-quotes")
@Tag(name = "the quick-quotes service api")
public class QuotesServiceController {

    @Autowired
    QuickQuotesService quickQuotesService;

    @Operation(
            operationId = "getQuickQuotes",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Quotes response", content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = QuotesResponse.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = CommonResponse.class))
                    })
            }
    )
    @Trace
    @GetMapping
    public ResponseEntity<CommonResponse<Map<String, Quote>>> getQuickQuotes(
            @Parameter(description = "customer Id ", example = "1068914")
            @RequestParam(required = true) String customerId,
            @RequestHeader(value = "correlationId", required = false) String correlationId) {

    	Map<String, Quote> quotes = new HashMap<String, Quote>();
        try {
            com.convera.product.data.model.QuotesResponse quotesResponse = quickQuotesService.getQuickQuotesByCustomerId(customerId, correlationId);

            return ResponseEntity.ok(CommonResponseUtil.createResponse200(quotesResponse.getData(),"convera/quick-quotes/",
                    correlationId));

        }catch (HttpClientErrorException.NotFound | DataNotFoundException ex ) {
            log.error("quick-quotes Not Found", ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponseUtil.createResponse404(quotes, QuotesApiConstants.ORDER_BASE_VALUE, correlationId, Collections
                            .singletonList(ResponseErrorCode404.ERR_40400.build("quick-quotes", "Record for given id not found."))));

        }catch (Exception ex) {
            log.error("Something went wrong", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponseUtil.createResponse500(quotes, QuotesApiConstants.ORDER_BASE_VALUE, correlationId,
                            Collections.singletonList(ResponseErrorCode500.ERR_50000.build("quick-quotes-service",
                                    "Error in getting records. Message: " + ex.getMessage()))));
        }
    }

    @Operation(
            operationId = "getQuickQuote",
            responses = {
                    @ApiResponse(responseCode = "200", description = "QuoteResponse", content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = QuoteResponse.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Not found", content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = CommonResponse.class))
                    })
            }
    )
    @Trace
    @GetMapping("/{quoteId}")
    public ResponseEntity<CommonResponse<Quote>> getQuickQuote(
            @Parameter(description = "quoteId", example = "21cb32a7-cb11-421f-84f5-c245578a1406")
            @PathVariable(required = true, name = "quoteId") String quoteId,
            @RequestHeader(value = "correlationID", required = false) String correlationId) {

    //	Quote q = new Quote("", "", "", "", null, "", "", null, null, "", null, null, null, null, null, null) ; 	
        try {
            com.convera.product.data.model.QuoteResponse quoteResponse = quickQuotesService.getQuickQuoteByQuoteId(quoteId, correlationId);

            return ResponseEntity.ok(CommonResponseUtil.createResponse200(quoteResponse.getData(),"convera/quick-quotes/",
                    correlationId));

        }catch (HttpClientErrorException.NotFound | DataNotFoundException ex ) {
            log.error("quick-quotes Not Found", ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CommonResponseUtil.createResponse404(new Quote(), QuotesApiConstants.ORDER_BASE_VALUE, correlationId, Collections
                            .singletonList(ResponseErrorCode404.ERR_40400.build("quick-quotes", "Record for given id not found."))));

        }catch (Exception ex) {
            log.error("Something went wrong", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponseUtil.createResponse500(new Quote(), QuotesApiConstants.ORDER_BASE_VALUE, correlationId,
                            Collections.singletonList(ResponseErrorCode500.ERR_50000.build("quick-quotes-service",
                                    "Error in getting records. Message: " + ex.getMessage()))));
        }
    }


    @Operation(
            operationId = "saveQuickQuote",
            responses = {
                    @ApiResponse(responseCode = "200", description = "quote response", content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = QuoteResponse.class))
                    }),
                    @ApiResponse(description = "unexpected error", content = {
                            @Content(mediaType = "application/json", schema =
                            @Schema(implementation = CommonResponse.class))
                    })
            }
    )
    @Trace
    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<Quote>> saveQuickQuote(
            @Valid @RequestBody(required = true) QuoteRequest request,
            @RequestHeader(value = "correlationID", required = false) String correlationId,
            HttpServletRequest servletRequest) {

        try {
            com.convera.product.data.model.QuoteResponse quoteResponse = quickQuotesService.saveQuickQuote(request, correlationId);

            return ResponseEntity.ok(CommonResponseUtil.createResponse200(quoteResponse.getData(),"convera/dashboard/",
                    correlationId));

        } catch (HttpClientErrorException.BadRequest ex) {
            log.error("Something went wrong", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponseUtil.createResponse400(new Quote(), servletRequest.getRequestURI() , correlationId, Collections
                            .singletonList(ResponseErrorCode400.ERR_40000.build("quick-quotes-service", "Record for given id not found."))));
        } catch (Exception ex) {
            log.error("Something went wrong", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponseUtil.createResponse500(null, QuotesApiConstants.ORDER_BASE_VALUE, correlationId,
                            Collections.singletonList(ResponseErrorCode500.ERR_50000.build("quick-quotes-service",
                                    "Error saving record. Message: " + ex.getMessage()))));
        }
    }
    private class QuotesResponse extends CommonResponse<Map<String, Quote>> {
    }

    private class QuoteResponse extends CommonResponse<Quote> {
    }

}
