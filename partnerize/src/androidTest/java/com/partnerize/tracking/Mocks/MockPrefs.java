package com.partnerize.tracking.Mocks;

import com.partnerize.tracking.ClickManager.IClickStorage;

public class MockPrefs implements IClickStorage {

    private String mockClickRef;

    public MockPrefs() {
    }

    @Override
    public String getClickRef() {
        return mockClickRef;
    }

    @Override
    public void setClickRef(String clickRef) {
        mockClickRef = clickRef;
    }
}
