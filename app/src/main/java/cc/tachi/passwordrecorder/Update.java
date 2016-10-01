package cc.tachi.passwordrecorder;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.style.SubscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by m on 2016/9/24.
 */
public class Update {


    private ProgressBar mProgressBar;
    private Dialog mDownloadDialog;

    private String savePath;
    private int mProgress;

    private boolean mIsCancel = false;

    private static final int DOWNLOADING = 1;
    private static final int DOWNLOAD_FINISH = 2;

    private static final String PATH = "http://app.tachi.cc/version.html";

    private String mVersion_code;
    private String mVersion_name;
    private String mVersion_desc;
    private String mVersion_path;

    private Context mContext;

    public Update(Context context) {
        mContext = context;
    }

    private Handler mGetVersionHandler = new Handler() {
        public void handleMessage(Message msg) {
            JSONObject jsonObject = (JSONObject) msg.obj;
            try {
                mVersion_code = jsonObject.getString("version_code");
                mVersion_name = jsonObject.getString("version_name");
                mVersion_desc = jsonObject.getString("version_desc");
                mVersion_path = jsonObject.getString("version_path");

                if (isUpdate()) {
                    showNoticeDialog();
                } else {
                    Toast.makeText(mContext, "已是最新版本", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ;
    };

    private Handler mUpdateProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOADING:
                    // 设置进度条
                    mProgressBar.setProgress(mProgress);
                    break;
                case DOWNLOAD_FINISH:
                    // 隐藏当前下载对话框
                    mDownloadDialog.dismiss();
                    // 安装 APK 文件
                    installAPK();
                    break;
            }
        }

        ;
    };

    /*
     * 检测软件是否需要更新
     */
    public void checkUpdate() {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest request = new JsonObjectRequest(PATH, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Message msg = Message.obtain();
                msg.obj = jsonObject;
                mGetVersionHandler.sendMessage(msg);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                System.out.println(arg0.toString());
            }
        });
        requestQueue.add(request);
    }

    /*
     * 与本地版本比较判断是否需要更新
     */
    protected boolean isUpdate() {
        int serverVersion = Integer.parseInt(mVersion_code);
        int localVersion = 1;

        try {
            localVersion = mContext.getPackageManager().getPackageInfo("cc.tachi.passwordrecorder", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return serverVersion > localVersion;
    }

    /*
     * 有更新时显示提示对话框
     */
    protected void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("检测到新版本");
        String message = "更新内容：\n" + mVersion_desc;
        builder.setMessage(message);

        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 隐藏当前对话框
                dialog.dismiss();
                // 显示下载对话框
                showDownloadDialog();
            }
        });

        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 隐藏当前对话框
                dialog.dismiss();
                System.exit(0);
            }
        });

        builder.create().show();
    }

    private void showDownloadDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("下载中");
        View view = LayoutInflater.from(mContext).inflate(R.layout.update, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.update);
        builder.setView(view);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                System.exit(0);
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        downloadApk();
    }

    private void downloadApk() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        String sdPath = Environment.getExternalStorageDirectory() + "/";
                        savePath = sdPath + "tachicc";
                        File dir = new File(savePath);
                        if (!dir.exists()) {
                                dir.mkdirs();
                        }
                        HttpURLConnection conn = (HttpURLConnection) new URL(mVersion_path).openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        int length = conn.getContentLength();

                        File aplFile = new File(savePath, mVersion_name+".apk");
                        FileOutputStream fos = new FileOutputStream(aplFile);
                        int count = 0;
                        byte[] buffer = new byte[1024];
                        while (true) {
                            int numread = is.read(buffer);
                            count += numread;
                            mProgress = (int) (((float) count / length) * 100);
                            mUpdateProgressHandler.sendEmptyMessage(DOWNLOADING);
                            if (numread < 0) {
                                mUpdateProgressHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                                break;
                            }
                            fos.write(buffer, 0, numread);
                        }
                        fos.close();
                        is.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void installAPK() {
        File apkFile = new File(savePath, mVersion_name+".apk");
        if (!apkFile.exists())
            return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + apkFile.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

}