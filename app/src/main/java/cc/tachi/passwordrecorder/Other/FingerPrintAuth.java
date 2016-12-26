package cc.tachi.passwordrecorder.Other;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cc.tachi.passwordrecorder.Crypt.FingerCrypto;
import cc.tachi.passwordrecorder.R;

/**
 * Created by tachi on 2016/12/26.
 */

public class FingerPrintAuth {
    private FingerprintManagerCompat fingerprintManager = null;
    private FingerPrintAuthCallback fingerPrintAuthCallback = null;
    private CancellationSignal cancellationSignal = null;
    private static final int MSG_AUTH_SUCCESS = 100;
    private static final int MSG_AUTH_FAILED = 101;
    private static final int MSG_AUTH_ERROR = 102;
    private static final int MSG_AUTH_HELP = 103;
    private Context context;
    private Handler outerHandler;

    private Handler handler = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public FingerPrintAuth(Context context,Handler ohandler){
        outerHandler = ohandler;
        this.context = context;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d("MSG", "msg: " + msg.what + " ,arg1: " + msg.arg1);
                switch (msg.what) {
                    case MSG_AUTH_SUCCESS:
                        outerHandler.sendEmptyMessage(1);
                        cancellationSignal = null;
                        break;
                    case MSG_AUTH_FAILED:
                        outerHandler.sendEmptyMessage(0);
                        break;
                    case MSG_AUTH_ERROR:
                        handleErrorCode(msg.arg1);
                        break;
                    case MSG_AUTH_HELP:
                        handleHelpCode(msg.arg1);
                        break;
                }
            }
        };
    }

    public void cancel(){
        cancellationSignal.cancel();
        cancellationSignal=null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void auth(){
        check();
        try {
            FingerCrypto fingerCrypto = new FingerCrypto();
            if (cancellationSignal == null) {
                cancellationSignal = new CancellationSignal();
            }
            fingerprintManager.authenticate(fingerCrypto.buildCryptoObject(), 0,
                    cancellationSignal, fingerPrintAuthCallback, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean check() {
        fingerprintManager = FingerprintManagerCompat.from(context);
        KeyguardManager keyguardManager =(KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure()) {
            // this device is secure.
            if (!fingerprintManager.isHardwareDetected()) {
                // no fingerprint sensor is detected, show dialog to tell user.
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("错误");
                builder.setMessage("未发现指纹传感器");
                builder.setCancelable(false);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        outerHandler.sendEmptyMessage(-1);
                    }
                });
                // show this dialog.
                builder.create().show();
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                // no fingerprint image has been enrolled.
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("错误");
                builder.setMessage("系统中无已注册指纹");
                builder.setCancelable(false);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        outerHandler.sendEmptyMessage(-1);
                    }
                });
                builder.create().show();
            }
            try {
                fingerPrintAuthCallback = new FingerPrintAuthCallback(handler);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void handleHelpCode(int code) {
        switch (code) {
            case FingerprintManager.FINGERPRINT_ACQUIRED_GOOD:
                Log.i("success","good");
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY:
                Log.i("success","dirty");
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT:
                Log.i("success","insufficient");
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL:
                Log.i("success","partial");
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST:
                Log.i("success","fast");
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW:
                Log.i("success","slow");
                break;
        }
    }

    private void handleErrorCode(int code) {
        switch (code) {
            case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
                Log.i("fail","cancel");
                break;
            case FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE:
                Log.i("fail","unavailable");
                break;
            case FingerprintManager.FINGERPRINT_ERROR_LOCKOUT:
                Log.i("fail","lockout");
                break;
            case FingerprintManager.FINGERPRINT_ERROR_NO_SPACE:
                Log.i("fail","nospace");
                break;
            case FingerprintManager.FINGERPRINT_ERROR_TIMEOUT:
                Log.i("fail","timeout");
                break;
            case FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS:
                Log.i("fail","unable");
                break;
        }
    }
}
