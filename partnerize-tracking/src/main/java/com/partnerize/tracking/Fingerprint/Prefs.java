package com.partnerize.tracking.Fingerprint;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class used to persist generated fingerprints to prevent unnesessary regeneration.
 */
public class Prefs {

    private final static String PREFS_KEY = "PARTERNIZE_SDK";
    private final static String REFERRER_KEY = "INSTALL_REFERRER_KEY";
    private final static String REFERRER_SUPPORTED_KEY = "INSTALL_REFERRER_SUPPORTED_KEY";
    private final static String FINGERPRINT_RESULT_KEY = "FINGERPRINT_RESULT";
    private final static String FINGERPRINT_SEND_COMPLETE_KEY = "FINGERPRINT_SEND_COMPLETE";

    private static SharedPreferences settings;

    public Prefs(final Context context) {
        settings = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
    }

    private void ensureInitialized() {
        if(settings == null) {
            throw new ExceptionInInitializerError("Prefs has not been initialized");
        }
    }

    public boolean hasFingerprintSent() {
        ensureInitialized();

        return settings.contains(FINGERPRINT_SEND_COMPLETE_KEY) && settings.getBoolean(FINGERPRINT_SEND_COMPLETE_KEY, false);
    }

    public void setFingerprintSent(boolean sent) {
        ensureInitialized();

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(FINGERPRINT_SEND_COMPLETE_KEY, sent);
        editor.apply();
    }

    public boolean isFingerprintSet() {
        ensureInitialized();
        return settings.contains(FINGERPRINT_RESULT_KEY);
    }

    public String getFingerprint() {
        ensureInitialized();
        return settings.getString(FINGERPRINT_RESULT_KEY, "");
    }

    public void setFingerprint(String json) {
        ensureInitialized();

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(FINGERPRINT_RESULT_KEY, json);
        editor.apply();
    }

    public boolean isInstallReferrerSet() {
        ensureInitialized();
        return settings.contains(REFERRER_KEY);
    }

    public String getInstallReferrer() {
        ensureInitialized();
        return settings.getString(REFERRER_KEY, "");
    }

    public int getInstallReferrerStatus() {
        ensureInitialized();
        return settings.getInt(REFERRER_SUPPORTED_KEY, 1);
    }

    public void setInstallReferrer(String installReferrer) {
        ensureInitialized();

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(REFERRER_KEY, installReferrer);
        editor.putInt(REFERRER_SUPPORTED_KEY, 0);
        editor.apply();
    }

    public void setInstallReferrerNotOk(int status) {
        ensureInitialized();

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(REFERRER_KEY, "");
        editor.putInt(REFERRER_SUPPORTED_KEY, status);
        editor.apply();
    }


}
