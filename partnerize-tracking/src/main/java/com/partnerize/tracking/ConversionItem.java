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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Describes attributes within a conversion item.
 */
public class ConversionItem implements Parcelable {

    public static final Creator<ConversionItem> CREATOR = new Creator<ConversionItem>() {
        @Override
        public ConversionItem createFromParcel(Parcel in) {
            return new ConversionItem(in);
        }

        @Override
        public ConversionItem[] newArray(int size) {
            return new ConversionItem[size];
        }
    };

    private final String mValue;
    private final String mCategory;
    private final int mQuantity;
    private final String mSku;
    private final HashMap<String, String> mMetadata = new HashMap<>();

    protected ConversionItem(Parcel in) {
        mValue = in.readString();
        mCategory = in.readString();
        mQuantity = in.readInt();
        mSku = in.readString();

        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            mMetadata.put(in.readString(), in.readString());
        }
    }

    private ConversionItem(Builder builder) {
        mValue = builder.mValue;
        mCategory  = builder.mCategory;
        mQuantity = builder.mQuantity;
        mSku = builder.mSku;

        for (String key: builder.mMetadata.keySet()) {
            mMetadata.put(key, builder.mMetadata.get(key));
        }
    }

    public String getValue() { return mValue; }

    public String getCategory() { return mCategory; }

    public int getQuantity() { return mQuantity; }

    public String getSku() { return mSku; }

    public HashMap<String, String> getMetadata() { return mMetadata; }

    /**
     * Constructs a new builder, copying the attributes from this ConversionItem.
     *
     * @return conversion item {@link Builder}
     */
    public Builder buildUpon() {
        return new Builder(mValue, mCategory)
                .setQuantity(mQuantity)
                .setSku(mSku)
                .setMetadata(mMetadata);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mValue);
        dest.writeString(mCategory);
        dest.writeInt(mQuantity);
        dest.writeString(mSku);

        dest.writeInt(mMetadata.size());
        for (String key: mMetadata.keySet()) {
            dest.writeString(key);
            dest.writeString(mMetadata.get(key));
        }
    }

    /**
     * Describes the builder to create a {@link ConversionItem}
     */
    public static class Builder {

        private String mValue;
        private String mCategory;
        private int mQuantity = 0;
        private String mSku;
        private HashMap<String, String> mMetadata = new HashMap<>();

        public Builder(String value, String category) {
            setValue(value);
            setCategory(category);
        }

        public Builder setValue(String value) {
            mValue = value;
            return this;
        }

        public Builder setCategory(String category) {
            mCategory = category;
            return this;
        }

        public Builder setQuantity(int quantity) {
            mQuantity = quantity;
            return this;
        }

        public Builder setSku(String sku) {
            mSku = sku;
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

        /**
         * Returns the {@link ConversionItem}
         * @return the {@link ConversionItem}
         */
        public ConversionItem build() {
            return new ConversionItem(this);
        }
    }
}