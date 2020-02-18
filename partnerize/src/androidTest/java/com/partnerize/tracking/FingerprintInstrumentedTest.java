package com.partnerize.tracking;

import android.app.Instrumentation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import androidx.test.annotation.UiThreadTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.partnerize.all.TestActivity;
import com.partnerize.tracking.Fingerprint.FingerprintCollector;
import com.partnerize.tracking.Fingerprint.FingerprintCompletable;
import com.partnerize.tracking.Fingerprint.FingerprintException;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FingerprintInstrumentedTest {

    @Spy
    RequestBuilder builder = new RequestBuilder();

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule(TestActivity.class);

    @InjectMocks
    FingerprintCollector collector;

    @Test
    public void testFingerprintWithContextAndSuccessfulAPIRequest() throws Throwable {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "success";

        final TestActivity activity = (TestActivity) activityRule.getActivity();

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        URL url = new URL(BuildConfig.FINGERPRINT_API_URL);
        builder = mock(RequestBuilder.class);

        when(builder.buildPostRequest(url)).thenReturn(new MockRequest(200));

        collector = new FingerprintCollector(activityRule.getActivity());
        collector.requestBuilder = builder;

        activityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            collector.collect(new FingerprintCompletable() {
                public void complete() {
                    expectation.complete("success");
                }

                @Override
                public void fail(FingerprintException ex) {
                    expectation.complete("fail");
                }
            });
            }
        });

        String actual = null;

        try {
            actual = expectation.get();
        } catch (ExecutionException e) {
            fail("Fingerprint complete failed: " + e.getLocalizedMessage());
        } catch (InterruptedException e) {
            fail("Fingerprint complete failed: " + e.getLocalizedMessage());
        }

        assertEquals(expectedResult, actual);
    }

    @Test
    public void testFingerprintWithContextAndNotFoundAPIRequest() throws Throwable {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "Failed to connect to Partnerize API (Status: 404)";

        final TestActivity activity = (TestActivity) activityRule.getActivity();

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        URL url = new URL(BuildConfig.FINGERPRINT_API_URL);
        builder = mock(RequestBuilder.class);

        when(builder.buildPostRequest(url)).thenReturn(new MockRequest(404));

        collector = new FingerprintCollector(activityRule.getActivity());
        collector.requestBuilder = builder;

        activityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                collector.collect(new FingerprintCompletable() {
                    public void complete() {
                        expectation.complete("success");
                    }

                    @Override
                    public void fail(FingerprintException ex) {
                        expectation.complete(ex.getMessage());
                    }
                });
            }
        });

        String actual = null;

        try {
            actual = expectation.get();
        } catch (ExecutionException e) {
            fail("Fingerprint complete failed: " + e.getLocalizedMessage());
        } catch (InterruptedException e) {
            fail("Fingerprint complete failed: " + e.getLocalizedMessage());
        }

        assertEquals(expectedResult, actual);
    }


    class MockRequest implements IRequest {

        private int mockStatus;

        public MockRequest(int mockStatus) {
            this.mockStatus = mockStatus;
        }

        @Override
        public void send(String body, CompletableRequest completableRequest) {
            completableRequest.complete(mockStatus);
        }
    }
}

