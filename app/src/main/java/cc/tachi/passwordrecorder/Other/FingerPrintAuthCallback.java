package cc.tachi.passwordrecorder.Other;

/**
 * Created by tachi on 2016/12/26.
 */

import android.os.Handler;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

class FingerPrintAuthCallback extends FingerprintManagerCompat.AuthenticationCallback {
    private static final int MSG_AUTH_SUCCESS = 100;
    private static final int MSG_AUTH_FAILED = 101;
    private static final int MSG_AUTH_ERROR = 102;
    private static final int MSG_AUTH_HELP = 103;

    private Handler handler = null;

    public FingerPrintAuthCallback(Handler handler) {
        super();

        this.handler = handler;
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);

        if (handler != null) {
            handler.obtainMessage(MSG_AUTH_ERROR, errMsgId, 0).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        super.onAuthenticationHelp(helpMsgId, helpString);

        if (handler != null) {
            handler.obtainMessage(MSG_AUTH_HELP, helpMsgId, 0).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);

        if (handler != null) {
            handler.obtainMessage(MSG_AUTH_SUCCESS).sendToTarget();
        }
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();

        if (handler != null) {
            handler.obtainMessage(MSG_AUTH_FAILED).sendToTarget();
        }
    }
}