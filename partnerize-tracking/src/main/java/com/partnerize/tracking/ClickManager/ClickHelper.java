package com.partnerize.tracking.ClickManager;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.partnerize.tracking.ClickManager.ClickConstants.*;

public class ClickHelper {
    private static List<String> validSchemes = new ArrayList<>(Arrays.asList("https", "http"));
    private static List<String> validTopLevelDomains = new ArrayList<>(Arrays.asList("prf.hn", "partnerize.tech"));


    static Uri addAPIModeToUri(Uri uri) throws ClickException {

        String clickComponent = String.format("%s/", CLICK_URI_COMPONENT);
        String modeJsonComponent = String.format("%s/", MODE_JSON_COMPONENT);
        String typeMobileComponent = String.format("%s/", TYPE_MOBILE_COMPONENT);

        if (uri.toString().contains("/l/")) {
            return Uri.withAppendedPath(uri, modeJsonComponent + typeMobileComponent);
        }



        final String empty = "";

        String modifiedPath = uri.getPath();

        if(modifiedPath == null) {
            throw new ClickException("Failed to add API mode to Uri. Missing path.");
        }

        try {
            modifiedPath = modifiedPath.replaceAll(clickComponent, empty);
            modifiedPath = modifiedPath.replaceAll(modeJsonComponent, empty);
            modifiedPath = modifiedPath.replaceAll(typeMobileComponent, empty);
        } catch (NullPointerException ex) {
            throw new ClickException("Failed to add API mode to Uri");
        }

        char first = modifiedPath.charAt(0);

        if(first == '/') {
            modifiedPath = modifiedPath.substring(1);
        }

        String baseUri = String.format("%s/%s/%s", CLICK_URI_COMPONENT, MODE_JSON_COMPONENT, TYPE_MOBILE_COMPONENT);

        String newUrl = String.format("%s/%s/%s", buildBaseUrl(uri), baseUri, modifiedPath);

        return Uri.parse(newUrl);
    }


    private static String buildBaseUrl(Uri uri) {
        return String.format("%s://%s", uri.getScheme(), uri.getHost());
    }

    static boolean isClickRequest(Uri uri) {
        if(!ClickHelper.isValidScheme(uri.getScheme())) {
            return false;
        }

        String host = uri.getHost();

        if(host == null) {
            return false;
        }

        if(!ClickHelper.isValidPartnerizeHostName(host)) {
            return false;
        }

        boolean valid = ClickHelper.isValidClickUrl(uri);

        return valid;
    }

    private static boolean isValidScheme(String scheme) {
        return validSchemes.contains(scheme);
    }

    private static boolean isValidPartnerizeHostName(String hostname) {
        String[] parts = hostname.split("\\.");


        if(parts.length < 2) {
            return false;
        }

        String topLevelDomain = String.format("%s.%s", parts[parts.length-2], parts[parts.length-1]);

        return validTopLevelDomains.contains(topLevelDomain);
    }

    private static boolean isValidClickUrl(Uri uri) {
        List<String> segments = uri.getPathSegments();

        if(segments.size() < 2) {
            return false;
        }

        String clickComponent = segments.get(0);
        return clickComponent.equals(CLICK_URI_COMPONENT) || clickComponent.equals(SHORTENED_CLICK_URI_COMPONENT);
    }
}
