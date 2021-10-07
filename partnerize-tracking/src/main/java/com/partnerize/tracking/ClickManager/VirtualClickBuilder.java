package com.partnerize.tracking.ClickManager;

import android.text.TextUtils;

import com.partnerize.tracking.Utility;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class VirtualClickBuilder {

    VirtualClick buildWithJSON(String json) throws ClickException {
        if (json == null || json.equals("")) {
            throw new ClickException("Invalid JSON. JSON Missing");
        }

        Map<String, Object> jsonData;

        try {
            jsonData = Utility.jsonToMap(json);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new ClickException("Invalid JSON.", e);
        }

        if (jsonData == null) {
            throw new ClickException("Invalid JSON. Failed to read JSON: " + json);
        }

        List<String> validationResult = this.validateJSONResponse(jsonData);

        if (validationResult.size() > 0) {
            throw new ClickException(String.format("%s %s", "Invalid JSON. Missing Keys:", TextUtils.join(", ", validationResult)));
        }

        String clickRef = (String) jsonData.get(ClickConstants.JSON_CLICKREF_KEY);
        String camRef = (String) jsonData.get(ClickConstants.JSON_CAMREF_KEY);
        String destination =  (String) jsonData.get(ClickConstants.JSON_DESTINATION_KEY);


        String destinationMobileValue = jsonData.get(ClickConstants.JSON_DESTINATION_MOBILE_KEY).toString();
        String destinationMobile = destinationMobileValue.equals("null") ? null : destinationMobileValue;
        Map<String, String> utmParams = parseUtmValues(jsonData, new UtmParameterFilter());
        Map<String, String> metaValues = parseUtmValues(jsonData, new MetaClickParameterFilter());

        return new VirtualClick.Builder()
                .setClickref(clickRef)
                .setCamRef(camRef)
                .setDestinationMobile(destinationMobile)
                .setDestination(destination)
                .setUtmParams(utmParams)
                .setMetaParams(metaValues)
                .build();
    }

    private Map<String, String> parseUtmValues(Map<String, Object> jsonData, ClickParameterFilter filter) {
        Map<String, String> filteredValues = new HashMap<>();
        Object[] keyObjects = jsonData.keySet().toArray();
        String[] keys = Arrays.copyOf(keyObjects, keyObjects.length, String[].class);

        for (String key : keys) {
            if (filter.shouldInclude(key)) {
                String value = (String) jsonData.get(key);
                filteredValues.put(key, value);
            }
        }
        return filteredValues;
    }

    private List<String> validateJSONResponse(Map<String, Object> jsonData) {
        List<String> missingKeys = new ArrayList<>();

        if (!jsonData.containsKey(ClickConstants.JSON_CLICKREF_KEY)) {
            missingKeys.add(ClickConstants.JSON_CLICKREF_KEY);
        }

        if (!jsonData.containsKey(ClickConstants.JSON_DESTINATION_KEY)) {
            missingKeys.add(ClickConstants.JSON_DESTINATION_KEY);
        }

        return missingKeys;
    }

    interface ClickParameterFilter {

        boolean shouldInclude(String key);
    }

    class UtmParameterFilter implements ClickParameterFilter {

        @Override
        public boolean shouldInclude(String key) {
            return key.startsWith("utm");
        }
    }

    class MetaClickParameterFilter implements ClickParameterFilter {

        @Override
        public boolean shouldInclude(String key) {
            return !key.startsWith("utm") &&
                    !key.equals(ClickConstants.JSON_CLICKREF_KEY) &&
                    !key.equals(ClickConstants.JSON_CAMREF_KEY) &&
                    !key.equals(ClickConstants.JSON_DESTINATION_KEY) &&
                    !key.equals(ClickConstants.JSON_DESTINATION_MOBILE_KEY);
        }
    }
}
