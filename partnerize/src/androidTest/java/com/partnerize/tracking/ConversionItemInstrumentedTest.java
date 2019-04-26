package com.partnerize.tracking;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
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
