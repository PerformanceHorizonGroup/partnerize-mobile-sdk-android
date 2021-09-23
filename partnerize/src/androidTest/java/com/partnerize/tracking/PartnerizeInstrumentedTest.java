package com.partnerize.tracking;

import android.content.Context;
import android.net.Uri;

import com.partnerize.tracking.ClickManager.VirtualClick;
import com.partnerize.tracking.Mocks.MockGetRequest;
import com.partnerize.tracking.Mocks.MockPartnerize;
import com.partnerize.tracking.Mocks.MockPrefs;
import com.partnerize.tracking.Mocks.MockRequestBuilder;
import com.partnerize.tracking.Mocks.MockVirtualClickManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PartnerizeInstrumentedTest {

    private MockPrefs prefs;
    private MockRequestBuilder builder;
    private MockVirtualClickManager clickManager;


    @Mock
    private Context mockContext;

    @Before
    public void setupPartnerize() {
        this.prefs = new MockPrefs();
        this.builder = new MockRequestBuilder();
        this.clickManager = new MockVirtualClickManager(builder);
    }

    @Test
    public void testPartnerizeEmptyUriPassedToBeginConversionThrows() {
        final Uri uri = Uri.parse(TestClickConsts.emptyUri);

        builder.setGetRequest(new MockGetRequest(0, null));

        Partnerize partnerize = new MockPartnerize(mockContext, prefs, clickManager);

        try {
            partnerize.beginConversion(uri, new CompletableClick() {
                @Override
                public void complete(Uri destination, String clickRef) {
                    fail();
                }

                @Override
                public void error(PartnerizeException exception) {
                    fail();
                }
            });
        } catch (IllegalArgumentException ex) {
            assertEquals("Parameter uri must not be null or empty", ex.getMessage());
        }
    }

    @Test
    public void testPartnerizeInvalidURIPassedToBeginConversionReturnsOriginalURL() {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "success";
        final Uri uri = Uri.parse(TestClickConsts.invalidUri);

        builder.setGetRequest(new MockGetRequest(0, null));

        Partnerize partnerize = new MockPartnerize(mockContext, prefs, clickManager);

        partnerize.beginConversion(uri, new CompletableClick() {
            @Override
            public void complete(Uri destination, String clickRef) {

                Assert.assertEquals(uri, destination);
                Assert.assertNull(clickRef);

                expectation.complete("success");
            }

            @Override
            public void error(PartnerizeException exception) {
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
    public void testPartnerizeValidUriPassedToBeginConversionReturnsFilteredURL() {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "success";
        final Uri uri = Uri.parse(TestClickConsts.completedUri);
        final Uri expected = Uri.parse("https://molimo.partnerize.com/product/999999999?adref=&clickref=9g9g9g9g9g9g");

        builder.setGetRequest(new MockGetRequest(0, null));

        Partnerize partnerize = new MockPartnerize(mockContext, prefs, clickManager);

        partnerize.beginConversion(uri, new CompletableClick() {
            @Override
            public void complete(Uri destination, String clickRef) {

                Assert.assertEquals(expected, destination);
                Assert.assertEquals("9g9g9g9g9g9g", clickRef);

                expectation.complete("success");
            }

            @Override
            public void error(PartnerizeException exception) {
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
    public void testPartnerizeValidUrlPassedToBeginConversionCompletesForValidUri() {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "success";
        final Uri uri = Uri.parse(TestClickConsts.uncompletedUri);
        final Uri expected = Uri.parse("https://molimo.partnerize.com/product/999999999");

        builder.setGetRequest(new MockGetRequest(200, TestClickConsts.validJsonResponse));

        Partnerize partnerize = new MockPartnerize(mockContext, prefs, clickManager);

        partnerize.beginConversion(uri, new CompletableClick() {
            @Override
            public void complete(Uri destination, String clickRef) {
                Assert.assertEquals("9g9g9g9g9g9g", prefs.getClickRef());
                Assert.assertEquals(expected, destination);
                expectation.complete("success");
            }

            @Override
            public void error(PartnerizeException exception) {
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
    public void testPartnerizeValidUrlPassedToBeginConversionCompletesForValidXlick() {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "success";
        final Uri uri = Uri.parse(TestClickConsts.uncompletedUri);
        final Uri expected = Uri.parse("https://molimo.partnerize.com/product/999999999");

        builder.setGetRequest(new MockGetRequest(200, TestClickConsts.validJsonResponse));

        Partnerize partnerize = new MockPartnerize(mockContext, prefs, clickManager);

        partnerize.beginConversion(uri, new CompletableVirtualClick() {
            @Override
            public void complete(VirtualClick click) {
                Assert.assertEquals("9g9g9g9g9g9g", prefs.getClickRef());
                Assert.assertEquals(expected, click.getDestinationUri());
                Assert.assertNull(click.getDestinationMobileUri());
                expectation.complete("success");
            }

            @Override
            public void error(PartnerizeException exception) {
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
    public void testPartnerizeValidUrlPassedToBeginConversionCompletesForValidUriWithUnknownSchema() {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "success";
        final Uri uri = Uri.parse(TestClickConsts.unknownSchemeUri);
        final Uri expected = Uri.parse("unknown://molimo.partnerize.com/product/999999999?adref=&clickref=9g9g9g9g9g9g");

        builder.setGetRequest(new MockGetRequest(200, TestClickConsts.validJsonResponse));

        Partnerize partnerize = new MockPartnerize(mockContext, prefs, clickManager);

        partnerize.beginConversion(uri, new CompletableClick() {
            @Override
            public void complete(Uri destination, String clickRef) {
                Assert.assertEquals("9g9g9g9g9g9g", prefs.getClickRef());
                Assert.assertEquals(expected, destination);
                expectation.complete("success");
            }

            @Override
            public void error(PartnerizeException exception) {
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
    public void testPartnerizeUrlPassedToBeginConversionMissingAppClickRefDoesNotStoreClickRef() {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "success";
        final Uri uri = Uri.parse(TestClickConsts.missingClickRefUri);
        final Uri expected = Uri.parse("https://molimo.partnerize.com/product/999999999?clickref=9g9g9g9g9g9g&adref=");

        builder.setGetRequest(new MockGetRequest(200, TestClickConsts.validJsonResponse));

        Partnerize partnerize = new MockPartnerize(mockContext, prefs, clickManager);

        partnerize.beginConversion(uri, new CompletableClick() {
            @Override
            public void complete(Uri destination, String clickRef) {
                Assert.assertNull(prefs.getClickRef());
                Assert.assertEquals(expected, destination);
                expectation.complete("success");
            }

            @Override
            public void error(PartnerizeException exception) {
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

