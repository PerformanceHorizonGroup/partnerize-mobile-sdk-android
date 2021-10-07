package com.partnerize.tracking.ClickManager;

import android.net.Uri;

import java.net.URI;
import java.util.Map;

public class VirtualClick {
    private final String clickref;
    private final String destination;
    private final String camRef;
    private final String destinationMobile;
    private final Map<String, String> utmParams;
    private final Map<String, String> metaParams;

    VirtualClick(String clickref, String camRef, String destination, String destinationMobile, Map<String, String> utmParams, Map<String, String> metaParams) {
        this.clickref = clickref;
        this.camRef = camRef;
        this.destination = destination;
        this.destinationMobile = destinationMobile;
        this.utmParams = utmParams;
        this.metaParams = metaParams;
    }

    public String getClickref() {
        return clickref;
    }

    public String getDestination() {
        return destination;
    }

    public String getCamRef() {
        return camRef;
    }

    public String getDestinationMobile() {
        return destinationMobile;
    }

    public Map<String, String> getUtmParams() {
        return utmParams;
    }

    public Map<String, String> getMetaParams() {
        return metaParams;
    }

    public Uri getDestinationUri() {
        return Uri.parse(destination);
    }

    public Uri getDestinationMobileUri() {
        return destinationMobile == null ? null : Uri.parse(destinationMobile);
    }

    static class Builder {
        private String clickref;
        private String destination;
        private String camRef;
        private String destinationMobile;
        private Map<String, String> utmParams;
        private Map<String, String> metaParams;

        public Builder setClickref(String clickref) {
            this.clickref = clickref;
            return this;
        }

        public Builder setDestination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder setCamRef(String camRef) {
            this.camRef = camRef;
            return this;
        }

        public Builder setDestinationMobile(String destinationMobile) {
            this.destinationMobile = destinationMobile;
            return this;
        }

        public Builder setUtmParams(Map<String, String> utmParams) {
            this.utmParams = utmParams;
            return this;
        }

        public Builder setMetaParams(Map<String, String> metaParams) {
            this.metaParams = metaParams;
            return this;
        }

        public VirtualClick build() {
            return new VirtualClick(
                    clickref,
                    camRef,
                    destination,
                    destinationMobile,
                    utmParams,
                    metaParams);
        }
    }
}
