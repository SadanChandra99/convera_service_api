package com.convera.product.service;

import com.convera.product.data.model.QuoteRequest;
import com.convera.product.data.model.QuoteResponse;
import com.convera.product.data.model.QuotesResponse;
import datadog.trace.api.Trace;

public interface QuickQuotesService {
    @Trace
    QuotesResponse getQuickQuotesByCustomerId(String customerId, String correlationId);

    @Trace
    QuoteResponse getQuickQuoteByQuoteId(String quoteId, String correlationId);

    @Trace
    QuoteResponse saveQuickQuote(QuoteRequest request, String correlationId);
}
