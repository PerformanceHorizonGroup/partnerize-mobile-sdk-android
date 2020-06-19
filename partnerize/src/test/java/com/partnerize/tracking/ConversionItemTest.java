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

import com.partnerize.tracking.Conversion.ConversionItem;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Describes unit tests for conversion items
 */
public class ConversionItemTest {

    @Test
    public void testBuilder() {
        ConversionItem.Builder builder1 = new ConversionItem.Builder("19.99", "Shoes");
        builder1.setQuantity(3);
        builder1.setSku("sku");
        builder1.addMetadata("metadata1", "value1");
        builder1.addMetadata("metadata2", "value2");
        builder1.addMetadata("metadata3", "value3");
        ConversionItem conversionItem1 = builder1.build();

        assertEquals("19.99", conversionItem1.getValue());
        assertEquals("Shoes", conversionItem1.getCategory());
        assertEquals(3, conversionItem1.getQuantity());
        assertEquals("sku", conversionItem1.getSku());
        HashMap<String, String> metadata1 = conversionItem1.getMetadata();
        assertEquals(3, metadata1.size());
        assertEquals("value1", metadata1.get("metadata1"));
        assertEquals("value2", metadata1.get("metadata2"));
        assertEquals("value3", metadata1.get("metadata3"));

        ConversionItem.Builder builder2 = conversionItem1.buildUpon();
        builder2.setValue("17.99");
        builder2.setQuantity(1);
        builder2.removeMetadata("metadata2");
        ConversionItem conversionItem2 = builder2.build();

        assertEquals("17.99", conversionItem2.getValue());
        assertEquals("Shoes", conversionItem2.getCategory());
        assertEquals(1, conversionItem2.getQuantity());
        assertEquals("sku", conversionItem2.getSku());
        HashMap<String, String> metadata2 = conversionItem2.getMetadata();
        assertEquals(2, metadata2.size());
        assertEquals("value1", metadata2.get("metadata1"));
        assertEquals("value3", metadata2.get("metadata3"));

        HashMap<String, String> metadata3 = new HashMap<>();
        metadata3.put("metadata4", "value4");
        ConversionItem.Builder builder3 = conversionItem2.buildUpon();
        builder3.setMetadata(metadata3);
        ConversionItem conversionItem3 = builder3.build();

        HashMap<String, String> metadata4 = conversionItem3.getMetadata();
        assertEquals(1, metadata4.size());
        assertEquals("value4", metadata4.get("metadata4"));
    }
}