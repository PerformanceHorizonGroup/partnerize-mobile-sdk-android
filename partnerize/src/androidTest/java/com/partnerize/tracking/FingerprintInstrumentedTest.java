package com.partnerize.tracking;

import android.content.Context;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.partnerize.all.TestActivity;
import com.partnerize.tracking.Fingerprint.FingerprintCollector;
import com.partnerize.tracking.Fingerprint.FingerprintCompletable;
import com.partnerize.tracking.Fingerprint.FingerprintException;
import com.partnerize.tracking.Fingerprint.Prefs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FingerprintInstrumentedTest {

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule(TestActivity.class);

    private FingerprintCollector collector;

    @Test
    public void testFingerprintWithContextAndSuccessfulAPIRequest() throws Throwable {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "success";

        final TestActivity activity = (TestActivity) activityRule.getActivity();

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        URL url = new URL(BuildConfig.FINGERPRINT_API_URL);

        RequestBuilder builder = mock(RequestBuilder.class);
        when(builder.buildPostRequest(url)).thenReturn(new MockRequest(200));

        MockPrefs prefs = new MockPrefs(activity);

        collector = new MockFingerprintCollector(activity, builder, prefs);

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
    public void testFingerprintWithContextAdvertisingIDAndSuccessfulAPIRequest() throws Throwable {
        final CompletableFuture<String> expectation = new CompletableFuture<>();
        final String expectedResult = "success";

        final TestActivity activity = (TestActivity) activityRule.getActivity();

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        URL url = new URL(BuildConfig.FINGERPRINT_API_URL);

        RequestBuilder builder = mock(RequestBuilder.class);
        when(builder.buildPostRequest(url)).thenReturn(new MockRequest(200));

        MockPrefs prefs = new MockPrefs(activity);

        collector = new MockFingerprintCollector(activity, "Test Advertising ID", builder, prefs);

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

        RequestBuilder builder = mock(RequestBuilder.class);
        when(builder.buildPostRequest(url)).thenReturn(new MockRequest(404));

        MockPrefs prefs = new MockPrefs(activity);

        collector = new MockFingerprintCollector(activity, builder, prefs);

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

        MockRequest(int mockStatus) {
            this.mockStatus = mockStatus;
        }

        @Override
        public void send(String body, CompletableRequest completableRequest) {
            completableRequest.complete(mockStatus);
        }
    }

    class MockFingerprintCollector extends FingerprintCollector {

        public MockFingerprintCollector(Context context, String googleAdvertisingId, RequestBuilder requestBuilder, MockPrefs prefs) {
            super(context, googleAdvertisingId);
            this.requestBuilder = requestBuilder;
            this.prefs = prefs;
        }

        public MockFingerprintCollector(Context context, RequestBuilder requestBuilder, MockPrefs prefs) {
            super(context, null);
            this.requestBuilder = requestBuilder;
            this.prefs = prefs;
        }
    }

    private class MockPrefs extends Prefs {

        MockPrefs(Context context) {
            super(context);
        }

        @Override
        public void setFingerprintSent(boolean sent) {
            //do nothing
        }

        @Override
        public void setFingerprint(String json) {
            //do nothing
        }

        @Override
        public void setInstallReferrer(String installReferrer) {
            //do nothing
        }

        @Override
        public void setInstallReferrerNotOk(int status) {
            //do nothing
        }

        @Override
        public int getInstallReferrerStatus() {
            return 0;
        }

        @Override
        public String getFingerprint() {
            return "";
        }

        @Override
        public String getInstallReferrer() {
            return "";
        }

        @Override
        public boolean isFingerprintSet() {
            return false;
        }

        @Override
        public boolean isInstallReferrerSet() {
            return false;
        }

        @Override
        public boolean hasFingerprintSent() {
            return false;
        }
    }
}

