package com.convera.product.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorConstants {
    // Exceptions
    public static final String PRODUCT_DATA_ERROR = "Failed to get a product data by id %s";

    // Logs
    public static final String PRODUCT_NOT_FOUND = "Record not found with product id: {}";

    public static final String PAYMENT_DATA_ERROR = "Failed to get a payment data %s";

    // Logs
    public static final String PAYMENT_BAD_REQUEST = "Bad Request";
    public static final String PAYMENT_NOT_FOUND = "Record not found";

    public static final String DASHBOARD_DATA_ERROR = "Failed to get a dashboard data by user Id %s";

    // Logs
    public static final String DASHBOARD_NOT_FOUND = "Record not found with user Id : {}";

    public static final String DASHBOARD_SAVE_ERROR = "Failed to save dashboard data";


    public static final String QUOTES_DATA_ERROR = "Failed to get a QuickQuotes data by customer Id %s";

    public static final String QUOTES_NOT_FOUND = "Record not found with customer Id : {}";

    public static final String QUOTE_DATA_ERROR = "Failed to get a QuickQuote data by quote Id %s";

    public static final String QUOTE_NOT_FOUND = "Record not found with quote Id : {}";

    public static final String QUOTE_SAVE_ERROR = "Failed to save dashboard data";

}
