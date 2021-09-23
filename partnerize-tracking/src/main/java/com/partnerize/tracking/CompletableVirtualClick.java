package com.partnerize.tracking;

import com.partnerize.tracking.ClickManager.VirtualClick;


public interface CompletableVirtualClick {
    void complete(VirtualClick click);
    void error(PartnerizeException exception);
}
