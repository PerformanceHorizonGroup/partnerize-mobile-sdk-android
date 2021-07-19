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

package com.partnerize.tracking.Conversions;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;

import com.partnerize.tracking.BuildConfig;
import com.partnerize.tracking.Conversion;
import com.partnerize.tracking.ConversionItem;
import com.partnerize.tracking.TrafficSource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Describes instrumented tests for conversions
 */
public class ConversionInstrumentedTest {

    @Test
    public void testIntent() {
        String clickReference = "click_reference";

        Intent intent = new Intent();
        intent.setData(new Uri.Builder()
                .scheme("https")
                .authority("example.com")
                .appendQueryParameter("app_clickref", clickReference)
                .build());

        Conversion conversion = new Conversion(intent);

        assertEquals(clickReference, conversion.getClickRef());
    }

    @Test
    public void testIntentAndClickRef() {
        String clickReference = "click_reference";

        Intent intent = new Intent();
        intent.setData(new Uri.Builder()
                .scheme("https")
                .authority("example.com")
                .build());

        Conversion conversion = new Conversion(intent, clickReference);

        assertEquals(clickReference, conversion.getClickRef());
    }

    @Test
    public void testParcel() {
        Conversion conversion = new Conversion.Builder("click_reference")
                .addConversionItem(new ConversionItem.Builder("19.99", "Shoes").build())
                .build();

        Parcel parcel = Parcel.obtain();
        conversion.writeToParcel(parcel, conversion.describeContents());
        parcel.setDataPosition(0);

        Conversion createdFromParcel = Conversion.CREATOR.createFromParcel(parcel);
        assertEquals("click_reference", createdFromParcel.getClickRef());
    }

    @Test
    public void testConversionUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://prf.hn/conversion/tracking_mode:api/device:mobile/context:m_app/app_sdk:true/app_os_device:android/app_os_device_version:");
        stringBuilder.append(Build.VERSION.RELEASE);
        stringBuilder.append("/app_sdk_version:" + BuildConfig.VERSION_NAME);
        stringBuilder.append("/clickref:click_reference");
        String url1 = new Conversion.Builder("click_reference")
                .toString();
        assertEquals(stringBuilder.toString(), url1);

        stringBuilder.append("/conversionref:conversion_ref");
        String url2 = new Conversion.Builder("click_reference")
                .setConversionRef("conversion_ref")
                .toString();
        assertEquals(stringBuilder.toString(), url2);

        stringBuilder.append("/publisher_reference:publisher_ref");
        String url3 = new Conversion.Builder("click_reference")
                .setConversionRef("conversion_ref")
                .setPublisherRef("publisher_ref")
                .toString();
        assertEquals(stringBuilder.toString(), url3);

        stringBuilder.append("/advertiser_reference:advertiser_ref");
        String url4 = new Conversion.Builder("click_reference")
                .setConversionRef("conversion_ref")
                .setPublisherRef("publisher_ref")
                .setAdvertiserRef("advertiser_ref")
                .toString();
        assertEquals(stringBuilder.toString(), url4);

        stringBuilder.append("/customer_reference:customer_ref");
        String url5 = new Conversion.Builder("click_reference")
                .setConversionRef("conversion_ref")
                .setPublisherRef("publisher_ref")
                .setAdvertiserRef("advertiser_ref")
                .setCustomerRef("customer_ref")
                .toString();
        assertEquals(stringBuilder.toString(), url5);

        stringBuilder.append("/currency:currency");
        String url6 = new Conversion.Builder("click_reference")
                .setConversionRef("conversion_ref")
                .setPublisherRef("publisher_ref")
                .setAdvertiserRef("advertiser_ref")
                .setCustomerRef("customer_ref")
                .setCurrency("currency")
                .toString();
        assertEquals(stringBuilder.toString(), url6);

        stringBuilder.append("/country:country");
        String url7 = new Conversion.Builder("click_reference")
                .setConversionRef("conversion_ref")
                .setPublisherRef("publisher_ref")
                .setAdvertiserRef("advertiser_ref")
                .setCustomerRef("customer_ref")
                .setCurrency("currency")
                .setCountry("country")
                .toString();
        assertEquals(stringBuilder.toString(), url7);

        stringBuilder.append("/voucher:voucher");
        String url8 = new Conversion.Builder("click_reference")
                .setConversionRef("conversion_ref")
                .setPublisherRef("publisher_ref")
                .setAdvertiserRef("advertiser_ref")
                .setCustomerRef("customer_ref")
                .setCurrency("currency")
                .setCountry("country")
                .setVoucher("voucher")
                .toString();
        assertEquals(stringBuilder.toString(), url8);

        stringBuilder.append("/payment_type:value");
        String url9 = new Conversion.Builder("click_reference")
                .setConversionRef("conversion_ref")
                .setPublisherRef("publisher_ref")
                .setAdvertiserRef("advertiser_ref")
                .setCustomerRef("customer_ref")
                .setCurrency("currency")
                .setCountry("country")
                .setVoucher("voucher")
                .addMetadata("payment_type", "value")
                .toString();
        assertEquals(stringBuilder.toString(), url9);

        stringBuilder.append("[value:9.99/category:Accessories]");
        String url10 = new Conversion.Builder("click_reference")
                .setConversionRef("conversion_ref")
                .setPublisherRef("publisher_ref")
                .setAdvertiserRef("advertiser_ref")
                .setCustomerRef("customer_ref")
                .setCurrency("currency")
                .setCountry("country")
                .setVoucher("voucher")
                .addMetadata("payment_type", "value")
                .addConversionItem(new ConversionItem.Builder("9.99", "Accessories").build())
                .toString();
        assertEquals(stringBuilder.toString(), url10);
    }

    @Test
    public void testConversionUrlWithClearedClickref() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://prf.hn/conversion/tracking_mode:api/device:mobile/context:m_app/app_sdk:true/app_os_device:android/app_os_device_version:");
        stringBuilder.append(Build.VERSION.RELEASE);
        stringBuilder.append("/app_sdk_version:" + BuildConfig.VERSION_NAME);
        stringBuilder.append("/clickref:");

        Conversion.Builder builder = new Conversion.Builder("click_reference");
        Conversion conversion = builder.build();

        conversion.clearClickref();

        String url = conversion.toString();

        assertEquals(stringBuilder.toString(), url);
    }

    @Test
    public void shouldHaveAUrlWithTheVersionNumber() {
        String url = new Conversion.Builder("click_ref").build().toUrl().toString();

        assertTrue(url.contains("app_sdk_version:" + BuildConfig.VERSION_NAME));
    }

    @Test
    public void shouldHaveAUrlWithTrafficSource() {
        String url = new Conversion.Builder("click_ref").setTrafficSource(TrafficSource.PARTNER)
                .build().toUrl().toString();

        assertTrue(url.contains("tsource:Partner"));
    }

}
