/*
 * Copyright 2019 Performance Horizon Group LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.partnerize.tracking;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Describes unit tests for conversions
 */
public class ConversionTest {

    @Test
    public void testBuilder() {
        Conversion.Builder builder1 = new Conversion.Builder("click_reference");
        builder1.setConversionRef("conversion_reference");
        builder1.setAdvertiserRef("advertiser_reference");
        builder1.setPublisherRef("publisher_reference");
        builder1.setCustomerRef("customer_reference");
        builder1.setCountry("GB");
        builder1.setCurrency("GBP");
        builder1.setVoucher("voucher");
        builder1.addMetadata("metadata1", "value1");
        builder1.addMetadata("metadata2", "value2");
        builder1.addConversionItem(new ConversionItem.Builder("19.99", "Shoes").build());
        builder1.addConversionItem(new ConversionItem.Builder("52.99", "Clothes").build());
        Conversion conversion1 = builder1.build();

        assertEquals("click_reference", conversion1.getClickRef());
        assertEquals("conversion_reference", conversion1.getConversionRef());
        assertEquals("advertiser_reference", conversion1.getAdvertiserRef());
        assertEquals("publisher_reference", conversion1.getPublisherRef());
        assertEquals("customer_reference", conversion1.getCustomerRef());
        assertEquals("GB", conversion1.getCountry());
        assertEquals("GBP", conversion1.getCurrency());
        assertEquals("voucher", conversion1.getVoucher());
        HashMap<String, String> metadata1 = conversion1.getMetadata();
        assertEquals(2, metadata1.size());
        assertEquals("value1", metadata1.get("metadata1"));
        assertEquals("value2", metadata1.get("metadata2"));
        ConversionItem[] conversionItems1 = conversion1.getConversionItems();
        assertEquals(2, conversionItems1.length);
        assertEquals("19.99", conversionItems1[0].getValue());
        assertEquals("Shoes", conversionItems1[0].getCategory());
        assertEquals("52.99", conversionItems1[1].getValue());
        assertEquals("Clothes", conversionItems1[1].getCategory());

        Conversion.Builder builder2 = conversion1.buildUpon();
        builder2.setClickRef("foo_bar");
        builder2.setCountry("US");
        builder2.setCurrency("USD");
        builder2.removeMetadata("metadata2");
        builder2.removeConversionItem(conversion1.getConversionItems()[0]);
        Conversion conversion2 = builder2.build();

        assertEquals("foo_bar", conversion2.getClickRef());
        assertEquals("conversion_reference", conversion2.getConversionRef());
        assertEquals("advertiser_reference", conversion2.getAdvertiserRef());
        assertEquals("publisher_reference", conversion2.getPublisherRef());
        assertEquals("customer_reference", conversion2.getCustomerRef());
        assertEquals("US", conversion2.getCountry());
        assertEquals("USD", conversion2.getCurrency());
        assertEquals("voucher", conversion2.getVoucher());
        HashMap<String, String> metadata2 = conversion2.getMetadata();
        assertEquals(1, metadata2.size());
        assertEquals("value1", metadata2.get("metadata1"));
        ConversionItem[] conversionItems2 = conversion2.getConversionItems();
        assertEquals(1, conversionItems2.length);
        assertEquals("52.99", conversionItems2[0].getValue());
        assertEquals("Clothes", conversionItems2[0].getCategory());
    }

    @Test
    public void testUrlBuilder() {
        Conversion conversion1 = new Conversion.Builder("click_reference")
                .build();

        Conversion.Url url1 = new Conversion.Url.Builder(conversion1)
                .build();

        assertEquals("https", url1.getScheme());
        assertEquals("prf.hn", url1.getAuthority());
        assertEquals("conversion/tracking_mode:api/device:mobile/context:m_app", url1.getBasePath());
        assertEquals(conversion1.getClickRef(), url1.getConversion().getClickRef());

        Conversion.Url url2 = url1.buildUpon()
                .setScheme("http")
                .build();

        assertEquals("http", url2.getScheme());
        assertEquals("prf.hn", url2.getAuthority());
        assertEquals("conversion/tracking_mode:api/device:mobile/context:m_app", url2.getBasePath());
        assertEquals(conversion1.getClickRef(), url2.getConversion().getClickRef());

        Conversion.Url url3 = url2.buildUpon()
                .setAuthority("localhost")
                .build();

        assertEquals("http", url3.getScheme());
        assertEquals("localhost", url3.getAuthority());
        assertEquals("conversion/tracking_mode:api/device:mobile/context:m_app", url3.getBasePath());
        assertEquals(conversion1.getClickRef(), url3.getConversion().getClickRef());

        Conversion.Url url4 = url3.buildUpon()
                .setBasePath("sandbox")
                .build();

        assertEquals("http", url4.getScheme());
        assertEquals("localhost", url4.getAuthority());
        assertEquals("sandbox", url4.getBasePath());
        assertEquals(conversion1.getClickRef(), url4.getConversion().getClickRef());

        Conversion conversion2 = new Conversion.Builder("foo_bar").build();

        Conversion.Url url5 = url4.buildUpon()
                .setConversion(conversion2)
                .build();

        assertEquals("http", url5.getScheme());
        assertEquals("localhost", url5.getAuthority());
        assertEquals("sandbox", url5.getBasePath());
        assertEquals(conversion2.getClickRef(), url5.getConversion().getClickRef());
    }

    @Test
    public void testClearClickref() {
        Conversion.Builder builder = new Conversion.Builder("click_reference");
        builder.setConversionRef("conversion_reference");
        builder.setAdvertiserRef("advertiser_reference");
        builder.setPublisherRef("publisher_reference");
        builder.setCustomerRef("customer_reference");
        builder.setCountry("GB");
        builder.setCurrency("GBP");
        builder.setVoucher("voucher");
        builder.addMetadata("metadata1", "value1");
        builder.addMetadata("metadata2", "value2");
        builder.addConversionItem(new ConversionItem.Builder("19.99", "Shoes").build());
        builder.addConversionItem(new ConversionItem.Builder("52.99", "Clothes").build());

        Conversion conversion = builder.build();

        conversion.clearClickref();

        assertEquals("", conversion.getClickRef());
    }
}