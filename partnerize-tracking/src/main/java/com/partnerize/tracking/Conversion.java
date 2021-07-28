/*
 * Copyright 2019 Performance Horizon Group LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.partnerize.tracking;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Describes attributes and items within a conversion.
 */
public class Conversion implements Parcelable {

    private static final String CLICK_REF = "app_clickref";

    public static final Creator<Conversion> CREATOR = new Creator<Conversion>() {
        @Override
        public Conversion createFromParcel(Parcel in) {
            return new Conversion(in);
        }

        @Override
        public Conversion[] newArray(int size) {
            return new Conversion[size];
        }
    };

    private String mClickRef;
    private final String mConversionRef;
    private final String mPublisherRef;
    private final String mAdvertiserRef;
    private final String mCustomerRef;
    private final String mCurrency;
    private final String mCountry;
    private final String mVoucher;
    private final String mTrafficSource;
    private final CustomerType mCustomerType;
    private final String mConversionMetric;

    private final HashMap<String, String> mMetadata = new HashMap<>();
    private final ConversionItem[] mConversionItems;

    /**
     * @deprecated Conversion(Intent) constructor has been deprecated and may not function as expected on Android API 25 or higher.
     * Please use Conversion(Intent, clickRef) instead in combination with Partnerize.beginConversion(Uri, CompletableClick).
     */
    @Deprecated()
    public Conversion(Intent intent) {
        String clickRef = "";
        Uri uri = intent.getData();

        if (uri != null) {
            clickRef = uri.getQueryParameter(CLICK_REF);
        }

        mClickRef = clickRef;
        mConversionRef = null;
        mPublisherRef = null;
        mAdvertiserRef = null;
        mCustomerRef = null;
        mCurrency = null;
        mCountry = null;
        mVoucher = null;
        mTrafficSource = null;
        mCustomerType = null;
        mConversionMetric = null;
        mConversionItems = new ConversionItem[0];
    }


    public Conversion(Intent intent, String clickRef) {
        Uri uri = intent.getData();

        mClickRef = clickRef;
        mConversionRef = null;
        mPublisherRef = null;
        mAdvertiserRef = null;
        mCustomerRef = null;
        mCurrency = null;
        mCountry = null;
        mVoucher = null;
        mTrafficSource = null;
        mCustomerType = null;
        mConversionMetric = null;
        mConversionItems = new ConversionItem[0];
    }

    private Conversion(Builder builder) {
        mClickRef = builder.mClickRef;
        mConversionRef = builder.mConversionRef;
        mPublisherRef = builder.mPublisherRef;
        mAdvertiserRef = builder.mAdvertiserRef;
        mCustomerRef = builder.mCustomerRef;
        mCurrency = builder.mCurrency;
        mCountry = builder.mCountry;
        mVoucher = builder.mVoucher;
        mTrafficSource = builder.mTrafficSource;
        mCustomerType = builder.mCustomerType;
        mConversionMetric = builder.mConversionMetric;

        for (String key: builder.mMetadata.keySet()) {
            mMetadata.put(key, builder.mMetadata.get(key));
        }

        mConversionItems = builder.mConversionItems.toArray(
                new ConversionItem[builder.mConversionItems.size()]
        );
    }

    protected Conversion(Parcel in) {
        mClickRef = in.readString();
        mConversionRef = in.readString();
        mPublisherRef = in.readString();
        mAdvertiserRef = in.readString();
        mCustomerRef = in.readString();
        mCurrency = in.readString();
        mCountry = in.readString();
        mVoucher = in.readString();
        mTrafficSource = in.readString();
        mConversionMetric = in.readString();

        String customerType = in.readString();
        mCustomerType = !customerType.isEmpty() ? CustomerType.valueOf(customerType) : null;

        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            mMetadata.put(in.readString(), in.readString());
        }

        mConversionItems = in.createTypedArray(ConversionItem.CREATOR);
    }

    public String getClickRef() { return mClickRef; }

    public String getConversionRef() { return mConversionRef; }

    public String getPublisherRef() { return mPublisherRef; }

    public String getAdvertiserRef() { return mAdvertiserRef; }

    public String getCustomerRef() { return mCustomerRef; }

    public String getCurrency() { return mCurrency; }

    public String getCountry() { return mCountry; }

    public String getVoucher() { return mVoucher; }

    public String getTrafficSource() { return mTrafficSource; }

    public CustomerType getCustomerType() { return mCustomerType; }

    public String getConversionMetric() { return mConversionMetric; }

    public HashMap<String, String> getMetadata() { return mMetadata; }

    public ConversionItem[] getConversionItems() { return mConversionItems; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mClickRef);
        dest.writeString(mConversionRef);
        dest.writeString(mPublisherRef);
        dest.writeString(mAdvertiserRef);
        dest.writeString(mCustomerRef);
        dest.writeString(mCurrency);
        dest.writeString(mCountry);
        dest.writeString(mVoucher);
        dest.writeString(mTrafficSource);
        if (mCustomerType != null) {
            dest.writeString(mCustomerType.name());
        }
        dest.writeString(mConversionMetric);
        dest.writeInt(mMetadata.size());
        for (String key: mMetadata.keySet()) {
            dest.writeString(key);
            dest.writeString(mMetadata.get(key));
        }

        dest.writeTypedArray(mConversionItems, flags);
    }

    @Override
    public String toString() {
        return toUrl().toString();
    }

    /**
     * Returns conversion represented by a {@link Conversion.Url}
     *
     * @return conversion represented by a {@link Conversion.Url}
     */
    public Url toUrl() {
        return new Url.Builder(this)
                .build();
    }

    /**
     * Clear a clickref from a conversion by setting a blank string.
     *
     * @return conversion
     */
    public Conversion clearClickref() {
        mClickRef = "";
        return this;
    }

    /**
     * Constructs a new builder, copying the attributes from this Conversion.
     *
     * @return conversion {@link Builder}
     */
    public Builder buildUpon() {
        return new Builder(mClickRef)
                .setConversionRef(mConversionRef)
                .setPublisherRef(mPublisherRef)
                .setAdvertiserRef(mAdvertiserRef)
                .setCustomerRef(mCustomerRef)
                .setCurrency(mCurrency)
                .setCountry(mCountry)
                .setVoucher(mVoucher)
                .setTrafficSource(mTrafficSource)
                .setCustomerType(mCustomerType)
                .setConversionMetric(mConversionMetric)
                .setMetadata(mMetadata)
                .setConversionItems(mConversionItems);
    }

    /**
     * Describes the builder to create a {@link Conversion}
     */
    public static class Builder {

        private String mClickRef;
        private String mConversionRef;
        private String mPublisherRef;
        private String mAdvertiserRef;
        private String mCustomerRef;
        private String mCurrency;
        private String mCountry;
        private String mVoucher;
        private String mTrafficSource;
        private CustomerType mCustomerType;
        private String mConversionMetric;

        private HashMap<String, String> mMetadata = new HashMap<>();
        private ArrayList<ConversionItem> mConversionItems = new ArrayList<>();

        public Builder(String clickRef) {
            setClickRef(clickRef);
        }

        public Builder setClickRef(String clickRef) {
            mClickRef = clickRef;
            return this;
        }

        public Builder setConversionRef(String conversionRef) {
            mConversionRef = conversionRef;
            return this;
        }

        public Builder setPublisherRef(String publisherRef) {
            mPublisherRef = publisherRef;
            return this;
        }

        public Builder setAdvertiserRef(String advertiserRef) {
            mAdvertiserRef = advertiserRef;
            return this;
        }

        public Builder setCustomerRef(String customerRef) {
            mCustomerRef = customerRef;
            return this;
        }

        public Builder setCurrency(String currency) {
            mCurrency = currency;
            return this;
        }

        public Builder setCountry(String country) {
            mCountry = country;
            return this;
        }

        public Builder setVoucher(String voucher) {
            mVoucher = voucher;
            return this;
        }

        public Builder setTrafficSource(String trafficSource) {
            mTrafficSource = trafficSource;
            return this;
        }

        public Builder setCustomerType(CustomerType customerType) {
            mCustomerType = customerType;
            return this;
        }

        public Builder setConversionMetric(String conversionMetric) {
            mConversionMetric = conversionMetric;
            return this;
        }

        public Builder setMetadata(HashMap<String, String> metadata) {
            mMetadata = metadata;
            return this;
        }

        public Builder addMetadata(String key, String value) {
            mMetadata.put(key, value);
            return this;
        }

        public Builder removeMetadata(String key) {
            mMetadata.remove(key);
            return this;
        }

        public Builder setConversionItems(ConversionItem[] conversionItems) {
            mConversionItems = new ArrayList<>(Arrays.asList(conversionItems));
            return this;
        }

        public Builder addConversionItem(ConversionItem conversionItem) {
            mConversionItems.add(conversionItem);
            return this;
        }

        public Builder removeConversionItem(ConversionItem conversionItem) {
            mConversionItems.remove(conversionItem);
            return this;
        }

        public String toString() {
            return build().toString();
        }

        /**
         * Returns the {@link Conversion}
         * @return the {@link Conversion}
         */
        public Conversion build() {
            return new Conversion(this);
        }
    }

    /**
     * Describes an attributes within a conversion's URL.
     */
    public static class Url {

        private String mScheme;
        private String mAuthority;
        private String mBasePath;
        private Conversion mConversion;

        private Url(Builder builder) {
            mScheme = builder.mScheme;
            mAuthority = builder.mAuthority;
            mBasePath = builder.mBasePath;
            mConversion = builder.mConversion;
        }

        public String getScheme() { return mScheme; }

        public String getAuthority() { return mAuthority; }

        public String getBasePath() { return mBasePath; }

        public Conversion getConversion() { return mConversion; }

        /**
         * Constructs a new builder, copying the attributes from this Url.
         *
         * @return the {@link Builder}
         */
        public Builder buildUpon() {
            return new Builder(mConversion)
                    .setScheme(mScheme)
                    .setAuthority(mAuthority)
                    .setBasePath(mBasePath);
        }

        public String toString() {
            Uri.Builder builder = new Uri.Builder()
                    .scheme(mScheme)
                    .authority(mAuthority)
                    .appendEncodedPath(mBasePath);

            return new ConversionUrlBuilder().toStringInner(builder, mConversion);
        }

        /**
         * Describes the builder to create a {@link Conversion.Url}
         */
        public static class Builder {

            private static final String SCHEME = "https";
            private static final String AUTHORITY = "prf.hn";
            private static final String BASE_PATH = "conversion/tracking_mode:api/device:mobile/context:m_app";

            private String mScheme = SCHEME;
            private String mAuthority = AUTHORITY;
            private String mBasePath = BASE_PATH;
            private Conversion mConversion;

            public Builder(Conversion conversion) {
                setConversion(conversion);
            }

            public Builder setScheme(String scheme) {
                mScheme = scheme;
                return this;
            }

            public Builder setAuthority(String authority) {
                mAuthority = authority;
                return this;
            }

            public Builder setBasePath(String basePath) {
                mBasePath = basePath;
                return this;
            }

            public Builder setConversion(Conversion conversion) {
                mConversion = conversion;
                return this;
            }

            public String toString() {
                return build().toString();
            }

            /**
             * Returns the {@link Conversion.Url}
             * @return the {@link Conversion.Url}
             */
            public Url build() {
                return new Url(this);
            }
        }
    }
}