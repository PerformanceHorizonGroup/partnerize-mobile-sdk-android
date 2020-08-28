package com.partnerize.tracking.Storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.partnerize.tracking.ClickManager.IClickStorage;

public class PartnerizePreferences implements IClickStorage {
    private static final String PREFS = "PartnerizePrefs";
    private static final String CLICK_REF_KEY = "PNG_CLICK_REF";

    private SharedPreferences prefs;

    public PartnerizePreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public String getClickRef() {
        return prefs.getString(CLICK_REF_KEY, "");
    }

    @SuppressLint("ApplySharedPref")
    public void setClickRef(String clickRef) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CLICK_REF_KEY, clickRef);
        editor.commit();
    }
}
