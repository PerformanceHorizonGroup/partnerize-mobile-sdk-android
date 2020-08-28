package com.partnerize.tracking.ClickManager;

public interface CompletableClick {
    void complete(VirtualClick click);
    void error(ClickException ex);
}
