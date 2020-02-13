package com.partnerize.tracking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;

import java.util.Map;

/**
 * Used to collect WebView based information.
 */
public final class Fingerprint {


    private interface IFingerprintCompleteDelegate {
        void complete(String json) throws JSONException;
        void fail(JSONException exception);
    }

    /**
     * JSI to handle postMessage and execute a callback delegate
     */
    private static class FingerprintJavaScriptInterface {

        private IFingerprintCompleteDelegate delegate;

        public FingerprintJavaScriptInterface(IFingerprintCompleteDelegate delegate) {
            this.delegate = delegate;
        }

        /**
         * Callable in JavaScript via <<alias>>.postMessage(json)
         * @param json The JSON string sent from JavaScript.
         */
        @JavascriptInterface
        public void postMessage(String json) {
            try {
                delegate.complete(json);
            } catch (JSONException ex) {
                delegate.fail(ex);
            }
        }
    }


    /**
     * Collect this devices fingerprint.
     * @param context The application context used for collection.
     */
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public static void collect(Context context, final String googleAdvertisingId, final Runnable complete) {
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new FingerprintJavaScriptInterface(new IFingerprintCompleteDelegate() {
            @Override
            public void complete(String json) throws JSONException {
                Map<String, Object> map = Utility.jsonToMap(json);

                if (map != null) {
                    map.put("gaid", googleAdvertisingId == null ? "" : googleAdvertisingId);
                }

                // todo: upload to API

                complete.run();
            }

            @Override
            public void fail(JSONException exception) {
                complete.run();
            }
        }), "partnerize");

        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("file:///android_asset/fingerprint.html");
    }

    /**
     * Collect this devices fingerprint.
     * @param context The application context used for collection.
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void collect(Context context, final Runnable complete) {
        collect(context, null, complete);
    }
}
