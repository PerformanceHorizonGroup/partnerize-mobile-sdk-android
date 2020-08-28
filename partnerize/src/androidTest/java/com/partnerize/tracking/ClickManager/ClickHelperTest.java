package com.partnerize.tracking.ClickManager;

import android.net.Uri;

import com.partnerize.tracking.TestClickConsts;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ClickHelperTest {

    @Test
    public void testAddAPIModeToUriWithUncompletedUri() {
        final Uri uri = Uri.parse(TestClickConsts.uncompletedUri);
        final Uri expected = Uri.parse("https://molimo.prf.hn/click/mode:json/type:mobile/camref:1a1a1a1a/destination:https://molimo.partnerize.com/product/999999999");

        try {
            Uri result = ClickHelper.addAPIModeToUri(uri);
            assertEquals(expected, result);
        } catch (ClickException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testAddAPIModeToUriWithInvalidUri() {
        final Uri uri = Uri.parse(TestClickConsts.missingSchemeUri);
        final String expected = "Failed to add API mode to Uri. Missing path.";


        try {
            ClickHelper.addAPIModeToUri(uri);
            fail("Expected to throw");
        } catch (ClickException e) {
            assertEquals(expected, e.getMessage());
        }
    }

    @Test
    public void testIsClickRequestWithValidUri() {
        final Uri uri = Uri.parse(TestClickConsts.uncompletedUri);

        final boolean result = ClickHelper.isClickRequest(uri);

        assertTrue(result);
    }

    @Test
    public void testIsClickRequestWithInvalidScheme() {
        final Uri uri = Uri.parse(TestClickConsts.unknownSchemeUri);

        final boolean result = ClickHelper.isClickRequest(uri);

        assertFalse(result);
    }

    @Test
    public void testIsClickRequestWithInvalidHostname() {
        final Uri uri = Uri.parse(TestClickConsts.invalidHostnameUri);

        final boolean result = ClickHelper.isClickRequest(uri);

        assertFalse(result);
    }

    @Test
    public void testIsClickRequestWithInvalidClickUri() {
        final Uri uri = Uri.parse(TestClickConsts.invalidClickUri);

        final boolean result = ClickHelper.isClickRequest(uri);

        assertFalse(result);
    }
}
