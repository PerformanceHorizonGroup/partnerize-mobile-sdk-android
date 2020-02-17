package com.partnerize.tracking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;

import java.util.Map;

/**
 * Used to collect WebView based information.
 */
public final class Fingerprint {

    private interface JsonCompletable {
        void complete(String json) throws JSONException;
        void fail(Exception ex);
    }

    public interface FingerpintCompletable {
        void complete();
        void fail(FingerprintException ex);
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

    //keep hold of the WebView, in case the JVM tries to dispose the WebView while we're waiting for the JS to callback
    @SuppressLint({"StaticFieldLeak"})
    private static WebView webView;
    private final static Object sync = new Object();

    /**
     * Collect this devices fingerprint.
     * @param context The context for fingerprinting.
     * @param googleAdvertisingId The Google Advertising ID to append.
     * @param completable The completable to be called when finished.
     */
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public static void collect(Context context, final String googleAdvertisingId, final FingerpintCompletable completable) {

        synchronized (sync) {
            webView = new WebView(context);
            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new WebViewClient());
            webView.addJavascriptInterface(new FingerprintJavaScriptInterface(new Fingerprint.JsonCompletable() {
                @Override
                public void complete(String json) {
                    try {
                        Map<String, Object> map = Utility.jsonToMap(json);

                        if (map != null) {
                            map.put("gaid", googleAdvertisingId == null ? "" : googleAdvertisingId);
                        }

                        // todo: upload to API

                        completable.complete();
                    }
                    catch (JSONException ex) {
                        completable.fail(new FingerprintException("Invalid response", ex));
                    }
                    finally {
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
     * Collect this devices fingerprint.
     * @param context The application context used for collection.
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void collect(Context context, final FingerpintCompletable completable) {
        collect(context, null, completable);
    }
}