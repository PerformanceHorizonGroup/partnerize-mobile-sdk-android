package com.partnerize.tracking;

public class TestClickConsts {
    public final static String emptyUri = "";
    public final static String invalidUri = "ui://fasdhfalsdhlfsdfas/dfasd/f/asd/%20asdasd/#asdasd";
    public final static String completedUri = "https://molimo.partnerize.com/product/999999999?clickref=9g9g9g9g9g9g&app_clickref=9g9g9g9g9g9g&adref=";
    public final static String uncompletedUri = "https://molimo.prf.hn/click/camref:1a1a1a1a/destination:https://molimo.partnerize.com/product/999999999";
    public final static String unknownSchemeUri = "unknown://molimo.partnerize.com/product/999999999?clickref=9g9g9g9g9g9g&app_clickref=9g9g9g9g9g9g&adref=";
    public final static String missingClickRefUri = "https://molimo.partnerize.com/product/999999999?clickref=9g9g9g9g9g9g&adref=";
    public final static String invalidHostnameUri = "https://molimo.example.com/click/camref:1a1a1a1a/destination:https://molimo.partnerize.com/product/999999999";
    public final static String invalidClickUri = "https://molimo.partnerize.com/product/4999999999?clickref=9g9g9g9g9g9g&app_clickref=9g9g9g9g9g9g&adref=";

    public final static String missingSchemeUri = "/molimo.prf.hn/click/camref:1a1a1a1a/destination:https://molimo.partnerize.com/product/999999999";
    public final static String validJsonResponse = "{\"clickref\":\"9g9g9g9g9g9g\",\"camref\":\"1a1a1a1a\",\"destination\":\"https:\\/\\/molimo.partnerize.com\\/product\\/999999999\",\"destination_mobile\":null}";
    public final static String invalidJsonResponse = "{\"clickref\":\"9g9g9g9g9g9g\",\"camref:1a1a1a1a\",\"destination\":\"https:\\/\\/molimo.partnerize.com\\/product\\/999999999\",\"destination_mobile\":null}";
}
