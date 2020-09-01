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

        private static final String ENCODING = "UTF-8";
        private static final String SEPARATOR = ":";
        private static final String CLICK_REF = "clickref";
        private static final String CONVERSION_REF = "conversion_reference";
        private static final String PUBLISHER_REF = "publisher_reference";
        private static final String ADVERTISER_REF = "advertiser_reference";
        private static final String CUSTOMER_REFERENCE = "customer_reference";
        private static final String CURRENCY = "currency";
        private static final String COUNTRY = "country";
        private static final String VOUCHER = "voucher";
        private static final String VALUE = "value";
        private static final String CATEGORY = "category";
        private static final String QUANTITY = "quantity";
        private static final String SKU = "sku";
        private static final String START_BRACKET = "[";
        private static final String END_BRACKET = "]";

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

            // Adoption analytics.
            builder.appendEncodedPath("app_sdk:true");
            builder.appendEncodedPath("app_os_device:android");
            builder.appendEncodedPath("app_os_device_version:" + Build.VERSION.RELEASE);

            return toStringInner(builder);
        }

        /** */
        private String toStringInner(Uri.Builder builder) {
            builder.appendEncodedPath(CLICK_REF + SEPARATOR + encode(mConversion.mClickRef));

            // Conversion Reference
            if (mConversion.mConversionRef != null) {
                builder.appendEncodedPath(CONVERSION_REF + SEPARATOR + encode(mConversion.mConversionRef));
            }

            // Publisher Reference
            if (mConversion.mPublisherRef != null) {
                builder.appendEncodedPath(PUBLISHER_REF + SEPARATOR + encode(mConversion.mPublisherRef));
            }

            // Advertiser Reference
            if (mConversion.mAdvertiserRef != null) {
                builder.appendEncodedPath(ADVERTISER_REF + SEPARATOR + encode(mConversion.mAdvertiserRef));
            }

            // Customer Reference
            if (mConversion.mCustomerRef != null) {
                builder.appendEncodedPath(CUSTOMER_REFERENCE + SEPARATOR + encode(mConversion.mCustomerRef));
            }

            // Currency
            if (mConversion.mCurrency != null) {
                builder.appendEncodedPath(CURRENCY + SEPARATOR + encode(mConversion.mCurrency));
            }

            // Country
            if (mConversion.mCountry != null) {
                builder.appendEncodedPath(COUNTRY + SEPARATOR + encode(mConversion.mCountry));
            }

            // Voucher
            if (mConversion.mVoucher != null) {
                builder.appendEncodedPath(VOUCHER + SEPARATOR + encode(mConversion.mVoucher));
            }

            // Conversion Metadata
            for (String key: mConversion.mMetadata.keySet()) {
                builder.appendEncodedPath(encode(key) + SEPARATOR + encode(mConversion.mMetadata.get(key)));
            }

            // Conversion Items
            ConversionItem[] conversionItems = mConversion.getConversionItems();
            StringBuilder stringBuilder = new StringBuilder();

            for (ConversionItem conversionItem : conversionItems) {

                String value = conversionItem.getValue();
                String category = conversionItem.getCategory();
                int quantity = conversionItem.getQuantity();
                String sku = conversionItem.getSku();
                HashMap<String, String> metadata = conversionItem.getMetadata();

                Uri.Builder innerBuilder = new Uri.Builder();
                innerBuilder.appendEncodedPath(VALUE + SEPARATOR + encode(value));
                innerBuilder.appendEncodedPath(CATEGORY + SEPARATOR + encode(category));

                // Quantity
                if (quantity > 0) {
                    innerBuilder.appendEncodedPath(QUANTITY + SEPARATOR + encode(String.valueOf(quantity)));
                }

                // SKU
                if (sku != null) {
                    innerBuilder.appendEncodedPath(SKU + SEPARATOR + encode(sku));
                }

                // Conversion Item Metadata
                for (String key : metadata.keySet()) {
                    innerBuilder.appendEncodedPath(encode(key) + SEPARATOR + encode(metadata.get(key)));
                }

                stringBuilder.append(START_BRACKET)
                        .append(innerBuilder.toString().substring(1))
                        .append(END_BRACKET);
            }

            return builder.toString() + stringBuilder.toString();
        }

        /**
         * Translates a string into a format suitable for URI.
         *
         * @param string input string to encode
         *
         * @return Encoded string otherwise string with forward slash (/)
         *         replaced with %2F to prevent directory breaking values if
         *         UnsupportedEncodingException is thrown
         */
        private String encode(String string) {
            try {
                string = URLEncoder.encode(string, ENCODING);
            } catch (UnsupportedEncodingException e) {
                string = string.replace("/", "%2F");
            }

            return string;
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