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

import android.os.Parcel;

import com.partnerize.tracking.ConversionItem;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Describes instrumented tests for conversion items
 */
public class ConversionItemInstrumentedTest {

    @Test
    public void testParcel() {
        ConversionItem conversionItem = new ConversionItem.Builder("24.99", "Shoes")
                .setQuantity(2)
                .setSku("sku")
                .addMetadata("metadata1", "value1")
                .build();

        Parcel parcel = Parcel.obtain();
        conversionItem.writeToParcel(parcel, conversionItem.describeContents());
        parcel.setDataPosition(0);

        ConversionItem createdFromParcel = conversionItem.CREATOR.createFromParcel(parcel);
        assertEquals("24.99", createdFromParcel.getValue());
        assertEquals("Shoes", createdFromParcel.getCategory());
        assertEquals(2, createdFromParcel.getQuantity());
        assertEquals("sku", createdFromParcel.getSku());
        HashMap<String, String> metadata = createdFromParcel.getMetadata();
        assertEquals(1, metadata.size());
        assertEquals("value1", metadata.get("metadata1"));
    }
}