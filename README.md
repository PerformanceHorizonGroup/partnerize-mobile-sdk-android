# Partnerize Mobile SDK - Android

The Android Mobile SDK allows you to capture in-app referrals, and easily record any sales that occur on the back of these referrals within your Android application.

## Features

The SDK provides two model classes; `Conversion` and `ConversionItem`, the combination of these classes enables the following features within your Android app.

* Click reference retrieval from inbound requests to your Android app.
* Conversion creation with a range of attributes including custom metadata.
* Conversion item support for accurate shopping basket representation.
* Deep linking support for Web to App and App to App.

## Installation

The Partnerize Mobile SDK for Android has been published

```java
dependencies {
    // Partnerize App Tracking
    implementation 'com.partnerize.android:tracking:1.0'
}
```

## Usage

### `Conversion` class

The `Conversion` class describes attributes and items within a conversion.

The following code example demonstrates how to build a `Conversion` using the `Conversion.Builder`.

```java
Conversion conversion = new Conversion.Builder("My_Click_Reference")
        .setConversionRef("my_conversion_reference")
        .setPublisherRef("my_publisher_reference")
        .setAdvertiserRef("my_advertiser_reference")
        .setCustomerRef("my_customer_reference")
        .setCurrency("USD")
        .setCountry("US")
        .setVoucher("25OFF")

        // Conversion Metadata
        .addMetadata("payment_type", "crypto_currency")

        // Conversion Items
        .addConversionItem(new ConversionItem.Builder("52.99", "Shoes").build())
        .addConversionItem(new ConversionItem.Builder("84.98", "Shoes")
                .setSku("SHO-BLK-17")
                .setQuantity(2)
                .addMetadata("clearance", "true")
                .build())
        .build();
```

#### Retrieving and extending conversions

With a built `Conversion` instance each attribute can be retrieved by utilizing the `get` methods.

```java
String clickRef = conversion.getClickRef();
String conversionRef = conversion.getConversionRef();
String publisherRef = conversion.getPublisherRef();
String advertiserRef = conversion.getAdvertiserRef();
String customerRef = conversion.getCustomerRef();
String currency = conversion.getCurrency();
String country = conversion.getCountry();
String voucher = conversion.getVoucher();

// Payment type metadata
String paymentType = conversion.getMetadata().get("payment_type");

// Second conversion item's quantity
int quantity = conversion.getConversionItems()[1].getQuantity();
```

Each `Conversion` is immutable which means when a `Conversion` has been constructed, it's attributes and `ConversionItem`s cannot be modified, however the `buildUpon` method initiates a `Conversion.Builder` with the attributes and `ConversionItem`s from the `Conversion`.

```java
conversion = conversion.buildUpon()
        .setCustomerRef("other_customer_ref")
        .setVoucher("50OFF")

        // Conversion Metadata
        .addMetadata("guest", "false")

        // Conversion Item
        .addConversionItem(new ConversionItem.Builder("9.99", "Accessories")
                .build())
        .build();
```

#### Handling requests

When an inbound intent from a mobile web browser or Android app launches your Android app via deep links, the Conversion instance can be constructed from the intent, preserving the click reference.

Here’s a snippet that shows how to retrieve the click reference from an Intent

```java
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Intent intent = getIntent();
    Conversion conversion = new Conversion(intent);

    String clickRef = conversion.getClickRef();
}
```

#### Sending to Partnerize

Once a conversion has been constructed and is ready to be recorded, it be can be sent to Partnerize using any HTTP request library. The `Conversion` class has a `toString` method which returns the URL which can be passed to a HTTP library like `Volley` or `OkHttp`.

Here’s a snippet that shows send a conversion to Partnerize via Volley

```java
// Convert the conversion into a Url
String url = conversion.toString();

RequestQueue queue = Volley.newRequestQueue(this);

StringRequest request = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
    @Override
    public void onResponse(String response) {
        // Successful
    }
}, new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
        // Handle Error
    }
});

queue.add(request);
```

or alternatively with OkHttp

```java
// Convert the conversion into a Url
String url = conversion.toString();

OkHttpClient client = new OkHttpClient();

Request request = new Request.Builder()
  .url(url)
  .build();

Response response = client.newCall(request).execute();
```

Since the `Conversion.Builder` class also has a `toString` method, a URL can be constructed without the `Conversion` being returned.

```java
String url = new Conversion.Builder("my_click_reference").toString();
```

##### Sandboxing

During development of your Android app you may need to sandbox the requests being made. The `Conversion` class has a `toUrl` method which allows the host and other aspects can be changed. For example:

```java
// Additional metadata to indicate development mode (Optional)
conversion.addMetadata("development_mode", "yes");

Conversion.Url url = conversion.toUrl();

Conversion.Uri.Builder builder = url.buildUpon()
        .setScheme("http")
        .setAuthority("localhost");

String url = builder.toString();
```

### Storing the click reference

Within your Android app you may have a user flow which spans over multi screens, meaning that the click reference needs to be persisted between activities.

`Conversion` instances implement the Android `Parcelable` interface, this allows the Conversion to be serialized by the Android platform and passed between activities within your Android app.

Here’s a snippet that shows how a `Conversion` can be passed to another activity

```java
Intent intent = new Intent(this, NextActivity.class);
intent.putExtra("conversion", conversion);

startActivity(intent);
```

And retrieving the Conversion from the subsequent activity.

```java
Conversion conversion = getIntent().getExtras().getParcelable("conversion");
```

#### Persistent click reference storage

Since the click reference is stored as a String it can be stored using Android String storage mechanics, should the need arise that the click reference needs to persist more permanently.

Here’s a snippet that shows how a click reference can be stored by SharedPerferences

```java
SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
SharedPreferences.Editor editor = preferences.edit();

// Put the click reference within SharedPreferences
editor.putString("clickRef", conversion.getClickRef());

editor.apply();
editor.commit();
```

### `ConversionItem` class

The `ConversionItem` class is a representation of an item within a Conversion to better represent a shopping basket.

A `ConversionItem` requires a `value` and `category` for each item however additional attributes can be associated; `quantity`, `sku` as well as custom metadata similar to conversions.

Using the following example shopping basket;

|Name   | Quantity   | Price   | SKU |
|---|---|---|---|
| Plain T-Shirt   | 3  |9.99   | TSH-PLN-MED |
| Coloured Vest  | 2  | 5.00  | VES-COL-SMA |
| Sports Trainers  | 1  | 19.99  | TRA-SPT-FIV |
| Coat  | 1  | 52.49  | COA-TAN-LRG |

It could be represented by the below conversion items:

```java
ConversionItem conversionItem1 = new ConversionItem.Builder("9.99", "Tops")
        .setQuantity(1)
        .setSku("TSH-PLN-MED")
        .build();

ConversionItem conversionItem2 = new ConversionItem.Builder("5.00", "Tops")
        .setQuantity(2)
        .setSku("VES-COL-SMA")
        .addMetadata("clearance", "yes")
        .build();

ConversionItem conversionItem3 = new ConversionItem.Builder("19.99", "Shoes")
        .setQuantity(1)
        .setSku("TRA-SPT-FIV")
        .build();

ConversionItem conversionItem4 = new ConversionItem.Builder("52.49", "Coats")
        .setQuantity(1)
        .setSku("COA-TAN-LRG")
        .addMetadata("season", "winter")
        .build();

// Add the conversion items to the conversion
conversion = conversion.buildUpon()
    .setConversionItems({
        conversionItem1,
        conversionItem2,
        conversionItem3,
        conversionItem4
    })
    .build();
```

#### Modifying and removing conversion items

Similar to `Conversion`, `ConversionItem`s are also immutable, therefore in order to modify them they must be fed into a `ConversionItem.Builder` using the `buildUpon` method.

When utilizing the `buildUpon` method for `ConversionItem`s, the newly built instance does not get added onto the `Conversion`, this is because the `ConversionItem.Builder` returns a new `ConversionItem`. This approach leads to more readable code without any assumptions being made on the state of the `Conversion`.

Here’s an example that shows how a `ConversionItem` can be removed, modified and added onto the `Conversion`.

```java
// Retrieve the ConversionItem being modified
ConversionItem conversionItem = conversion.getConversionItems()[0];

conversion = conversion.buildUpon()
        .removeConversionItem(conversionItem)
        .addConversionItem(conversionItem.buildUpon()
            .setQuantity(3)
            .build())
        .build();
```

## License
[Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0)
