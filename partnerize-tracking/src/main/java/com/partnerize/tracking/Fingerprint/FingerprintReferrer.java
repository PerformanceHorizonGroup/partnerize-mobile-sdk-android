package com.partnerize.tracking.Fingerprint;

import android.content.Context;
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
        OK(0),
        ERROR(1),
        NOT_SUPPORTED(2),
        PERMISSION_DENIED(3);

        private final int value;
        ReferrerServiceStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    static final class ReferrerResult {
        ReferrerServiceStatus serviceStatus;
        String referrer;
    }

    private static boolean isReferrerServiceAvailable() {
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

    private static ReferrerResult getHandledReferrer(final Prefs prefs) {
        if(prefs.isInstallReferrerSet()) {
            ReferrerServiceStatus status = ReferrerServiceStatus.values()[prefs.getInstallReferrerStatus()];

            ReferrerResult result = new ReferrerResult();
            result.serviceStatus = status;

            if(status == ReferrerServiceStatus.OK) {
                result.referrer = prefs.getInstallReferrer();
                return result;
            } else if(status == ReferrerServiceStatus.NOT_SUPPORTED) {
                result.referrer = "";
                return result;
            }

            // otherwise there was previously an error, continue as normal
        }

        return null;
    }
    /**
     * Query Google Play service for the install referrer
     * @param context The application context.
     * @param completable Callback with the result.
     */
    static void getReferrer(final Context context, final Prefs prefs, final CompletableResult<FingerprintReferrer.ReferrerResult> completable) {

        ReferrerResult existingResult = getHandledReferrer(prefs);

        if(existingResult != null) {
            completable.complete(existingResult);
            return;
        }

        if(!isReferrerServiceAvailable()) {
            existingResult = new ReferrerResult();
            existingResult.referrer = "";
            existingResult.serviceStatus = ReferrerServiceStatus.NOT_SUPPORTED;
            completable.complete(existingResult);
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

                            prefs.setInstallReferrer(referrerUrl);

                            result.referrer = referrerUrl;
                            result.serviceStatus = ReferrerServiceStatus.OK;
                        } catch (RemoteException e) {
                            result.serviceStatus = ReferrerServiceStatus.ERROR;
                            prefs.setInstallReferrerNotOk(ReferrerServiceStatus.ERROR.getValue());
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        prefs.setInstallReferrerNotOk(ReferrerServiceStatus.NOT_SUPPORTED.getValue());
                        result.serviceStatus = ReferrerServiceStatus.NOT_SUPPORTED;
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        prefs.setInstallReferrerNotOk(ReferrerServiceStatus.ERROR.getValue());
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
