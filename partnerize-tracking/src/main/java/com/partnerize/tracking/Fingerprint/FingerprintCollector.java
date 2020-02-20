package com.partnerize.tracking.Fingerprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.partnerize.tracking.BuildConfig;
import com.partnerize.tracking.CompletableRequest;
import com.partnerize.tracking.IRequest;
import com.partnerize.tracking.RequestBuilder;
import com.partnerize.tracking.Utility;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Used to collect WebView based information.
 */
public class FingerprintCollector {

    private interface JsonCompletable {
        void complete(String json) throws JSONException;
        void fail(Exception ex);
    }

    /**
     * JSI to handle postMessage and execute a callback delegate
     */
    private static class FingerprintJavaScriptInterface {

        private JsonCompletable delegate;

        FingerprintJavaScriptInterface(JsonCompletable delegate) {
            this.delegate = delegate;
        }

        /**
         * Callable in JavaScript via <<alias>>.postMessage(json)
         * @param json The JSON string sent from JavaScript.
         */
        @JavascriptInterface
        public void postMessage(String json, String transferList) {
            try {
                delegate.complete(json);
            } catch (Exception ex) {
                delegate.fail(ex);
            }
        }

        @JavascriptInterface
        public void postError(String error, String transferList) {
            Log.println(Log.ERROR, "PARNERIZE_SDK", error);
            delegate.fail(new FingerprintException("Fingerprint failed: " + error));
        }
    }

    private static class ReferrerJson {
        String serviceStatus;
        String referrer;
    }

    //keep hold of the WebView, in case the JVM tries to dispose the WebView while we're waiting for the JS to callback
    private WebView webView;

    private final Object sync = new Object();
    private final Context context;
    private final String googleAdvertisingId;

    protected RequestBuilder requestBuilder;
    protected Prefs prefs;
    /**
     * Create a new FingerprintCollector object.
     * @param context The context to use for collecting fingerprints.
     * @param googleAdvertisingId The Google Advertising Id.
     */
    public FingerprintCollector(Context context, String googleAdvertisingId) {
        this.context = context;
        this.googleAdvertisingId = googleAdvertisingId;
        requestBuilder = new RequestBuilder();
        prefs = new Prefs(context);
    }

    /**
     * Create a new FingerprintCollector object.
     * @param context The context to use for collecting fingerprints.
     */
    public FingerprintCollector(Context context) {
        this.context = context;
        this.googleAdvertisingId = null;
        requestBuilder = new RequestBuilder();
        prefs = new Prefs(context);
    }

    /**
     * Collect this devices fingerprint.
     * @param completable The completable to be called when finished.
     */
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public void collect(final FingerprintCompletable completable) {

        if(prefs.isFingerprintSet()) {
            if(prefs.hasFingerprintSent()) {
                // already sent, nothing left to do.
                completable.complete();
                return;
            }

            try {
                Map<String, Object> result = Utility.jsonToMap(prefs.getFingerprint());
                sendFingerprintsToAPI(result, new FingerprintCompletable() {
                    @Override
                    public void fail(FingerprintException ex) {
                        completable.fail(ex);
                    }

                    @Override
                    public void complete() {
                        completable.complete();
                        prefs.setFingerprintSent(true);
                    }
                });
            } catch (JSONException e) {
                prefs.setFingerprint("");
                prefs.setFingerprintSent(false);
            }

            return;
        }

        synchronized (sync) {
            webView = new WebView(context);
            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new WebViewClient());
            webView.addJavascriptInterface(new FingerprintJavaScriptInterface(new FingerprintCollector.JsonCompletable() {
                @Override
                public void complete(final String json) {
                    try {
                        //once the browser fingerprints are collected, add the native fingerprints and send to the API.

                        Map<String, Object> map = Utility.jsonToMap(json);

                        getNativeFingerprints(map, new CompletableResult<Map<String, Object>>() {
                            @Override
                            public void complete(Map<String, Object> result) {

                                try {
                                    prefs.setFingerprint(Utility.mapToJson(result));
                                } catch (JSONException ex) {
                                    FingerprintException exception = new FingerprintException("Error parsing fingerprint.", ex);
                                    completable.fail(exception);
                                    return;
                                }

                                sendFingerprintsToAPI(result, new FingerprintCompletable() {
                                    @Override
                                    public void fail(FingerprintException ex) {
                                        prefs.setFingerprintSent(false);
                                        completable.fail(ex);
                                    }

                                    @Override
                                    public void complete() {
                                        prefs.setFingerprintSent(true);
                                        completable.complete();
                                    }
                                });
                            }
                        });
                    } catch (JSONException ex) {
                        completable.fail(new FingerprintException("Invalid response", ex));
                    } finally {
                        webView = null;
                    }
                }

                @Override
                public void fail(Exception exception) {
                    webView = null;
                    completable.fail(new FingerprintException("Failed to fingerprint device", exception));
                }
            }), "partnerize");

            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
            settings.setDomStorageEnabled(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
            settings.setSupportZoom(true);
            settings.setDefaultTextEncodingName("utf-8");
            settings.setPluginState(WebSettings.PluginState.ON);

            webView.loadUrl("file:///android_asset/fingerprint.html");
        }
    }

    /**
     * Append native fingerprints to the Map.
     * @param fingerprints The current fingerprint Map.
     * @param completable Called when referrer retrieved.
     */
    private void getNativeFingerprints(final Map<String, Object> fingerprints, final CompletableResult<Map<String, Object>> completable) {
        FingerprintReferrer.getReferrer(context, prefs, new CompletableResult<FingerprintReferrer.ReferrerResult>() {
            @Override
            public void complete(FingerprintReferrer.ReferrerResult result) {
                ReferrerJson obj = new ReferrerJson();
                obj.serviceStatus = result.serviceStatus.name();
                obj.referrer = result.referrer;

                fingerprints.put("referrer", obj);
                fingerprints.put("gaid", googleAdvertisingId == null ? "" : googleAdvertisingId);

                completable.complete(fingerprints);
            }
        });
    }

    /**
     * POST the Fingerprints to Partnerize's API.
     * @param fingerprints The Fingerprint Map.
     * @param completable Called when request has completed or failed.
     */
    private void sendFingerprintsToAPI(Map<String, Object> fingerprints, final FingerprintCompletable completable) {
        try {
            String json = Utility.mapToJson(fingerprints);

            IRequest request = requestBuilder.buildPostRequest(new URL(BuildConfig.FINGERPRINT_API_URL));
            request.send(json, new CompletableRequest() {
                @SuppressLint("DefaultLocale")
                @Override
                public void complete(int status) {
                    if(status == 200) {
                        completable.complete();
                    } else {
                        completable.fail(new FingerprintException(String.format("Failed to connect to Partnerize API (Status: %d)", status)));
                    }
                }

                @Override
                public void error(Exception ex) {
                    completable.fail(new FingerprintException("Failed to connect to Parternize API", ex));
                }
            });
        } catch (JSONException ex) {
            completable.fail(new FingerprintException("Invalid JSON Response", ex));
        } catch (MalformedURLException ex) {
            completable.fail(new FingerprintException("Invalid API URL", ex));
        }
    }
}