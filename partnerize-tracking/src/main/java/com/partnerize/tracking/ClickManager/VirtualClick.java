package com.partnerize.tracking.ClickManager;

import android.text.TextUtils;

import com.partnerize.tracking.Utility;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class VirtualClick {
    private String clickref;
    private String destination;

    private VirtualClick(String clickref, String destination) {
        this.clickref = clickref;
        this.destination = destination;
    }

    public String getClickref() {
        return clickref;
    }

    public String getDestination() {
        return destination;
    }

    static class VirtualClickBuilder {

        VirtualClick buildWithJSON(String json) throws ClickException {
            if(json == null || json.equals("")) {
                throw new ClickException("Invalid JSON. JSON Missing");
            }

            Map<String, Object> jsonData;

            try {
                jsonData = Utility.jsonToMap(json);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new ClickException("Invalid JSON.", e);
            }

            if(jsonData == null) {
                throw new ClickException("Invalid JSON. Failed to read JSON: " + json);
            }

            List<String> validationResult = this.validateJSONResponse(jsonData);

            if(validationResult.size() > 0) {
                throw new ClickException(String.format("%s %s","Invalid JSON. Missing Keys:", TextUtils.join(", ", validationResult)));
            }

            String clickref = (String) jsonData.get(ClickConstants.JSON_CLICKREF_KEY);
            String destination = (String) jsonData.get(ClickConstants.JSON_DESTINATION_KEY);

            return new VirtualClick(clickref, destination);
        }

        private List<String> validateJSONResponse(Map<String, Object> jsonData) {
            List<String> missingKeys = new ArrayList<>();

            if(!jsonData.containsKey(ClickConstants.JSON_CLICKREF_KEY)) {
                missingKeys.add(ClickConstants.JSON_CLICKREF_KEY);
            }

            if(!jsonData.containsKey(ClickConstants.JSON_DESTINATION_KEY)) {
                missingKeys.add(ClickConstants.JSON_DESTINATION_KEY);
            }

            return missingKeys;
        }
    }
}
