package com.convera.product;

import com.convera.product.data.model.Quote;
import com.convera.product.data.model.QuoteRequest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuickQuotesHelper {


    public static Quote getQuickQuoteByQuoteId(String quoteId) {
        Quote q = getQuickQuote(UUID.randomUUID().toString());
        q.setQuoteId(quoteId);
        return q;
    }
    public static Quote getQuickQuote(QuoteRequest quoteRequest) {
        Quote q = getQuickQuote(quoteRequest.getCustomerId());
        q.setPayCurrency(quoteRequest.getPayCurrency());
        q.setReceiveCurrency(q.getReceiveCurrency());
        q.setSpecifiedCurrency(q.getSpecifiedCurrency());
        q.setSpecifiedAmount(q.getSpecifiedAmount());
        return q;
    }

    public static Map<String,Quote> getQuickQuotes(String customerId, Integer elements) {
        Map<String,Quote> response = new HashMap<>();
        for (int i = 0; i < elements; i++) {
            Quote q = getQuickQuote(customerId);
            response.put(q.getQuoteId(), q);
        }
        return response;
    }

    public static Quote getQuickQuote(String customerId){
        Quote quote = new Quote();
        quote.customerId(customerId);
        quote.quoteId(UUID.randomUUID().toString());
        quote.receiveCurrency("USD");
        quote.payCurrency("CAD");
        quote.specifiedAmount(new BigDecimal(2));
        quote.specifiedCurrency("CAD");
        quote.valueDate("20220825");
        quote.customerRate(new BigDecimal(2));
        quote.customerRateInverted(random(new BigDecimal(2),7));
        quote.baseCurrency("USD");
        quote.costRate(random(new BigDecimal(2),2));
        quote.receiveAmount(random(new BigDecimal(100),1));
        quote.payAmount(random(new BigDecimal(100),1));
        quote.expirationInterval(2);
        quote.forwardPoints(random(new BigDecimal(10),0));
        quote.salesMargin(random(new BigDecimal(1),7));
        return quote;
    }

    static BigDecimal random(BigDecimal range, int scale) {
        return new BigDecimal(Math.random()).multiply(range).setScale(scale, BigDecimal.ROUND_DOWN);
    }

}
