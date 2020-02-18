package com.partnerize.tracking.Fingerprint;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

/**
 * Used to collect the Google Play Installer Referrer.
 */
class FingerprintReferrer
{
    public enum ReferrerServiceStatus {
        OK,
        ERROR,
        NOT_SUPPORTED,
        PERMISSION_DENIED
    }

    static final class ReferrerResult {
        ReferrerServiceStatus serviceStatus;
        String referrer;
    }

    private static boolean isReferrerAvailable() {
        boolean success = false;
        try {
            Class.forName("com.android.installreferrer.api.InstallReferrerStateListener");
            success = true;
        }
        catch (Exception ex) {
            //do nothing
        }

        return success;
    }

    private static boolean isPermissionGranted(Context context) {
        int value = context.checkCallingOrSelfPermission("com.android.vending.INSTALL_REFERRER");
        if (value == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        return true;
    }

    /**
     * Query Google Play service for the install referrer
     * @param context The application context.
     * @param completable Callback with the result.
     */
    static void getReferrer(Context context, final CompletableResult<FingerprintReferrer.ReferrerResult> completable) {

        if(!isPermissionGranted(context)) {
            ReferrerResult result = new ReferrerResult();
            result.serviceStatus = ReferrerServiceStatus.PERMISSION_DENIED;
            completable.complete(result);
        }

        if(!isReferrerAvailable()) {
            ReferrerResult result = new ReferrerResult();
            result.serviceStatus = ReferrerServiceStatus.NOT_SUPPORTED;
            completable.complete(result);
        }

        final InstallReferrerClient client = InstallReferrerClient.newBuilder(context).build();
        client.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {

                ReferrerResult result = new ReferrerResult();

                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        try {
                            ReferrerDetails details = client.getInstallReferrer();
                            String referrerUrl = details.getInstallReferrer();

                            result.referrer = referrerUrl;
                            result.serviceStatus = ReferrerServiceStatus.OK;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            result.serviceStatus = ReferrerServiceStatus.ERROR;
                        }

                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        result.serviceStatus = ReferrerServiceStatus.NOT_SUPPORTED;
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        result.serviceStatus = ReferrerServiceStatus.ERROR;
                        break;
                }

                completable.complete(result);
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {

                ReferrerResult result = new ReferrerResult();
                result.serviceStatus = ReferrerServiceStatus.ERROR;
                completable.complete(result);
            }
        });
    }
}
