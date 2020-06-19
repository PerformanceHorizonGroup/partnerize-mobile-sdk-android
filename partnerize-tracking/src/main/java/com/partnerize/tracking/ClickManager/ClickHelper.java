package com.partnerize.tracking.ClickManager;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.partnerize.tracking.ClickManager.ClickConstants.*;

public class ClickHelper {
    private static List<String> validSchemes = new ArrayList<>(Arrays.asList("https", "http"));
    private static List<String> validTopLevelDomains = new ArrayList<>(Arrays.asList("prf.hn", "partnerize.tech"));


    static URL addAPIModeToUri(URL url) throws ClickException {
        String clickComponent = String.format("%s/", CLICK_URI_COMPONENT);
        String modeJsonComponent = String.format("%s/", MODE_JSON_COMPONENT);
        String typeMobileComponent = String.format("%s/", TYPE_MOBILE_COMPONENT);
        final String empty = "";

        String modifiedPath = url.getPath();

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
        String baseUrl = String.format("%s://%s", url.getProtocol(), url.getHost());
        String newUrl = String.format("%s/%s/%s", baseUrl, baseUri, modifiedPath);

        try {
            return new URL(newUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new ClickException("Failed to add API mode to Uri. Malformed URL.", e);
        }
    }

    public static boolean isClickRequest(Uri uri) {

        if(!ClickHelper.isValidScheme(uri.getScheme())) {
            return false;
        }

        final URL url;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }

        String host = url.getHost();

        if(host == null) {
            return false;
        }

        if(!ClickHelper.isValidPartnerizeHostName(url.getHost())) {
            return false;
        }

        return ClickHelper.isValidClickUrl(url);
    }

    public static boolean isClickRequest(URL url) {

        if(!ClickHelper.isValidScheme(url.getProtocol())) {
            return false;
        }

        String host = url.getHost();

        if(host == null) {
            return false;
        }

        if(!ClickHelper.isValidPartnerizeHostName(url.getHost())) {
            return false;
        }

        return ClickHelper.isValidClickUrl(url);
    }

    private static boolean isValidScheme(String scheme) {
        return validSchemes.contains(scheme);
    }

    private static boolean isValidPartnerizeHostName(String hostname) {
        String[] parts = hostname.split(".");

        if(parts.length < 2) {
            return false;
        }

        String topLevelDomain = String.format("%s.%s", parts[parts.length-2], parts[parts.length-1]);

        return validTopLevelDomains.contains(topLevelDomain);
    }

    private static boolean isValidClickUrl(URL url) {
        Uri uri = Uri.parse(url.toString());

        List<String> segments = uri.getPathSegments();

        if(segments.size() < 2) {
            return false;
        }

        return segments.get(1).equals(CLICK_URI_COMPONENT);
    }
}
