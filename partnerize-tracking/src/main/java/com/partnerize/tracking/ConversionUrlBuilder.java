package com.partnerize.tracking;

import android.net.Uri;
import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

class ConversionUrlBuilder {

    private static final String ENCODING = "UTF-8";
    private static final String SEPARATOR = ":";
    private static final String CLICK_REF = "clickref";
    private static final String CONVERSION_REF = "conversionref";
    private static final String PUBLISHER_REF = "publisher_reference";
    private static final String ADVERTISER_REF = "advertiser_reference";
    private static final String CUSTOMER_REFERENCE = "customer_reference";
    private static final String CURRENCY = "currency";
    private static final String COUNTRY = "country";
    private static final String VOUCHER = "voucher";
    private static final String TRAFFIC_SOURCE = "tsource";
    private static final String CONVERSION_METRIC = "tmetric";
    private static final String CUSTOMER_TYPE = "customertype";
    private static final String VALUE = "value";
    private static final String CATEGORY = "category";
    private static final String QUANTITY = "quantity";
    private static final String SKU = "sku";
    private static final String START_BRACKET = "[";
    private static final String END_BRACKET = "]";


    String toStringInner(Uri.Builder builder, Conversion conversion) {
        // Adoption analytics.
        builder.appendEncodedPath("app_sdk:true");
        builder.appendEncodedPath("app_os_device:android");
        builder.appendEncodedPath("app_os_device_version:" + Build.VERSION.RELEASE);
        builder.appendEncodedPath("app_sdk_version:" + BuildConfig.VERSION_NAME);

        builder.appendEncodedPath(CLICK_REF + SEPARATOR + encode(conversion.getClickRef()));

        // Conversion Reference
        if (conversion.getConversionRef() != null) {
            builder.appendEncodedPath(CONVERSION_REF + SEPARATOR + encode(conversion.getConversionRef()));
        }

        // Publisher Reference
        if (conversion.getPublisherRef() != null) {
            builder.appendEncodedPath(PUBLISHER_REF + SEPARATOR + encode(conversion.getPublisherRef()));
        }

        // Advertiser Reference
        if (conversion.getAdvertiserRef() != null) {
            builder.appendEncodedPath(ADVERTISER_REF + SEPARATOR + encode(conversion.getAdvertiserRef()));
        }

        // Customer Reference
        if (conversion.getCustomerRef() != null) {
            builder.appendEncodedPath(CUSTOMER_REFERENCE + SEPARATOR + encode(conversion.getCustomerRef()));
        }

        // Currency
        if (conversion.getCurrency() != null) {
            builder.appendEncodedPath(CURRENCY + SEPARATOR + encode(conversion.getCurrency()));
        }

        // Country
        if (conversion.getCountry() != null) {
            builder.appendEncodedPath(COUNTRY + SEPARATOR + encode(conversion.getCountry()));
        }

        // Voucher
        if (conversion.getVoucher() != null) {
            builder.appendEncodedPath(VOUCHER + SEPARATOR + encode(conversion.getVoucher()));
        }

        if (conversion.getTrafficSource() != null) {
            builder.appendEncodedPath(TRAFFIC_SOURCE + SEPARATOR + encode(conversion.getTrafficSource()));
        }

        if (conversion.getCustomerType() != null) {
            builder.appendEncodedPath(CUSTOMER_TYPE + SEPARATOR + encode(conversion.getCustomerType().name().toLowerCase()));
        }

        if (conversion.getConversionMetric() != null) {
            builder.appendEncodedPath(CONVERSION_METRIC + SEPARATOR + encode(conversion.getConversionMetric()));
        }

        // Conversion Metadata
        for (String key: conversion.getMetadata().keySet()) {
            builder.appendEncodedPath(encode(key) + SEPARATOR + encode(conversion.getMetadata().get(key)));
        }

        // Conversion Items
        ConversionItem[] conversionItems = conversion.getConversionItems();
        StringBuilder stringBuilder = new StringBuilder();

        for (ConversionItem conversionItem : conversionItems) {

            String value = conversionItem.getValue();
            String category = conversionItem.getCategory();
            int quantity = conversionItem.getQuantity();
            String sku = conversionItem.getSku();
            HashMap<String, String> metadata = conversionItem.getMetadata();

            Uri.Builder innerBuilder = new Uri.Builder();
            innerBuilder.appendEncodedPath(VALUE + SEPARATOR + encode(value));
            innerBuilder.appendEncodedPath(CATEGORY + SEPARATOR + encode(category));

            // Quantity
            if (quantity > 0) {
                innerBuilder.appendEncodedPath(QUANTITY + SEPARATOR + encode(String.valueOf(quantity)));
            }

            // SKU
            if (sku != null) {
                innerBuilder.appendEncodedPath(SKU + SEPARATOR + encode(sku));
            }

            // Conversion Item Metadata
            for (String key : metadata.keySet()) {
                innerBuilder.appendEncodedPath(encode(key) + SEPARATOR + encode(metadata.get(key)));
            }

            stringBuilder.append(START_BRACKET)
                    .append(innerBuilder.toString().substring(1))
                    .append(END_BRACKET);
        }


        return builder.toString() + stringBuilder.toString();
    }

    private String encode(String string) {
        try {
            string = URLEncoder.encode(string, ENCODING);
        } catch (UnsupportedEncodingException e) {
            string = string.replace("/", "%2F");
        }

        return string;
    }
}
