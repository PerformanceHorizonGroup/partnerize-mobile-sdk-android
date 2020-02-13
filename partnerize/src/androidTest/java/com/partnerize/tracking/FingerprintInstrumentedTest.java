package com.partnerize.tracking;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;

import com.partnerize.all.TestActivity;

public class FingerprintInstrumentedTest {

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule(TestActivity.class);

    @Test
    public void testFingerprintWithContext() throws Throwable {

        final CompletableFuture<String> expectation = new CompletableFuture<>();

        final TestActivity activity = (TestActivity) activityRule.getActivity();


        activityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fingerprint.collect(activity, new Runnable() {
                    @Override
                    public void run() {
                        expectation.complete("success");
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

        assertEquals("success", actual);
    }
}

