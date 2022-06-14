package com.partnerize.tracking;

import android.content.Context;
import android.net.Uri;

import com.partnerize.tracking.Mocks.MockPartnerize;
import com.partnerize.tracking.Mocks.MockPrefs;
import com.partnerize.tracking.Mocks.MockRequestBuilder;
import com.partnerize.tracking.Mocks.MockVirtualClickManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClickValidationTest {

    @Mock
    private Context mockContext;

    private MockPrefs prefs;
    private MockVirtualClickManager clickManager;
    private MockPartnerize partnerize;

    @Before
    public void setup() {
        this.prefs = new MockPrefs();
        this.clickManager = new MockVirtualClickManager(new MockRequestBuilder());
        partnerize = new MockPartnerize(mockContext, prefs, clickManager);
    }

    @Test
    public void testPartnerizeNullUriIsNotValid() {
        final boolean result = partnerize.isClickRequest(null);

        assertFalse(result);
    }

    @Test
    public void testPartnerizeEmptyUriIsNotValid() {
        final Uri uri = Uri.parse(TestClickConsts.emptyUri);

        final boolean result = partnerize.isClickRequest(uri);

        assertFalse(result);
    }

    @Test
    public void testPartnerizeInvalidUriIsNotValid() {
        final Uri uri = Uri.parse(TestClickConsts.invalidClickUri);

        final boolean result = partnerize.isClickRequest(uri);

        assertFalse(result);
    }

    @Test
    public void testPartnerizeShortUriIsValid() {
        final Uri uri = Uri.parse(TestClickConsts.shortUrl);

        final boolean result = partnerize.isClickRequest(uri);

        assertTrue(result);
    }

    @Test
    public void testPartnerizeRegularUriIsValid() {
        final Uri uri = Uri.parse(TestClickConsts.trackingUri);

        final boolean result = partnerize.isClickRequest(uri);

        assertTrue(result);
    }
}
