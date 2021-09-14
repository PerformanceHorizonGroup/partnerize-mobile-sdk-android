package com.partnerize.tracking.ClickManager;

import android.net.Uri;

import com.partnerize.tracking.Mocks.MockGetRequest;
import com.partnerize.tracking.Mocks.MockRequestBuilder;
import com.partnerize.tracking.Mocks.MockVirtualClickManager;
import com.partnerize.tracking.TestClickConsts;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class VirtualClickManagerTest {

    @Test
    public void testVirtualClickManagerHandlesMissingClickRequestUri() {
        final Uri uri = Uri.parse(TestClickConsts.emptyUri);

        VirtualClickManager manager = new VirtualClickManager();
        final boolean result = manager.isClickRequest(uri);

        assertFalse(result);
    }

    @Test
    public void testVirtualClickManagerHandlesNullClickRequestUri() {
        VirtualClickManager manager = new VirtualClickManager();
        final boolean result = manager.isClickRequest(null);

        assertFalse(result);
    }

    @Test
    public void testVirtualClickManagerDetectsValidClickRequestUri() {
        final Uri uri = Uri.parse(TestClickConsts.uncompletedUri);

        VirtualClickManager manager = new VirtualClickManager();
        final boolean result = manager.isClickRequest(uri);

        assertTrue(result);
    }

    @Test
    public void testVirtualClickManagerHandlesValidUri() {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "success";
        final Uri uri = Uri.parse(TestClickConsts.uncompletedUri);

        final String response = TestClickConsts.validJsonResponse;

        MockRequestBuilder builder = new MockRequestBuilder();
        builder.setGetRequest(new MockGetRequest(200, response));

        VirtualClickManager manager = new MockVirtualClickManager(builder);
        manager.createVirtualClick(uri, new CompletableClick() {
            @Override
            public void complete(VirtualClick click) {
                assertEquals("9g9g9g9g9g9g",click.getClickref());
                assertEquals("1a1a1a1a",click.getCamRef());
                assertEquals("https://molimo.partnerize.com/product/999999999",click.getDestination());
                assertNull(click.getDestinationMobile());
                assertEquals("test", click.getUtmParams().get("utm_source"));
                assertEquals("testParam1", click.getMetaParams().get("testParam1"));
                assertEquals(2, click.getMetaParams().values().size());
                assertEquals(1, click.getUtmParams().values().size());
                expectation.complete("success");
            }

            @Override
            public void error(ClickException ex) {
                fail(ex.getMessage());
                expectation.complete("failed");
            }
        });

        String actual = null;

        try {
            actual = expectation.get();
        } catch (ExecutionException e) {
            fail("beginConversion failed: " + e.getLocalizedMessage());
        } catch (InterruptedException e) {
            fail("beginConversion failed: " + e.getLocalizedMessage());
        }

        Assert.assertEquals(expectedResult, actual);
    }


    @Test
    public void testVirtualClickManagerHandlesValidUriWithInvalidResponse() {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "failed";
        final Uri uri = Uri.parse(TestClickConsts.uncompletedUri);

        final String response = "";

        MockRequestBuilder builder = new MockRequestBuilder();
        builder.setGetRequest(new MockGetRequest(400, response));

        VirtualClickManager manager = new MockVirtualClickManager(builder);
        manager.createVirtualClick(uri, new CompletableClick() {
            @Override
            public void complete(VirtualClick click) {
                expectation.complete("success");
            }

            @Override
            public void error(ClickException ex) {
                expectation.complete("failed");
            }
        });

        String actual = null;

        try {
            actual = expectation.get();
        } catch (ExecutionException e) {
            fail("beginConversion failed: " + e.getLocalizedMessage());
        } catch (InterruptedException e) {
            fail("beginConversion failed: " + e.getLocalizedMessage());
        }

        Assert.assertEquals(expectedResult, actual);
    }



}

