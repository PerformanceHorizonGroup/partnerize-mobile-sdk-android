package com.partnerize.tracking.Mocks;

import android.content.Context;

import com.partnerize.tracking.ClickManager.IClickStorage;
import com.partnerize.tracking.ClickManager.VirtualClickManager;
import com.partnerize.tracking.Partnerize;

public class MockPartnerize extends Partnerize {

    private VirtualClickManager clickManager;

    public MockPartnerize(Context context, IClickStorage prefs, VirtualClickManager clickManager) {
        super(context);
        this.clickManager = clickManager;
        this.prefs = prefs;
    }

    @Override
    protected IClickStorage createPrefs(Context context) {
        return prefs;
    }

    @Override
    protected VirtualClickManager createClickManager() {
        return clickManager;
    }
}
