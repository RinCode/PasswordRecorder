package cc.tachi.passwordrecorder.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.eftimoff.patternview.PatternView;

import java.util.Objects;

import cc.tachi.passwordrecorder.Crypt.MD5;
import cc.tachi.passwordrecorder.Other.FingerPrintAuth;
import cc.tachi.passwordrecorder.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by m on 2016/9/19.
 * <p>
 * https://github.com/geftimov/android-patternview/
 */
public class FragmentLogin extends Fragment {
    private SQLiteDatabase db;
    private PatternView patternView;
    private String patternString;
    private Dialog fingerDialog;
    private SharedPreferences preferences;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_login, container, false);
        getActivity().setTitle("登录");
        db = getActivity().openOrCreateDatabase("tachi.db", getActivity().MODE_PRIVATE, null);
        patternView = (PatternView) view.findViewById(R.id.patternView);
        patternView.setTactileFeedbackEnabled(false);
        Cursor c = db.rawQuery("select * from user", null);
        if (!c.moveToNext()) {
            Toast.makeText(getActivity(), "首次使用需设定解锁图案", Toast.LENGTH_SHORT).show();
        }
        bundle = getArguments();
        try {
            bundle.getString("finger");
        } catch (Exception e) {
            e.printStackTrace();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isSetFinger()) {
                fingerAuth();
            }
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        patternView.setOnPatternDetectedListener(new PatternView.OnPatternDetectedListener() {
            @Override
            public void onPatternDetected() {
                Cursor c = db.rawQuery("select * from user", null);
                if (!c.moveToNext()) {
                    if (patternString == null) {
                        patternString = patternView.getPatternString();
                        Toast.makeText(getActivity(), "请重复一次", Toast.LENGTH_SHORT).show();
                        patternView.clearPattern();
                        return;
                    }
                    if (patternString.equals(patternView.getPatternString())) {
                        patternView.clearPattern();
                        try {
                            db.execSQL("insert into user (user,password) values ('', '" + MD5.encrypt(patternString) + "');");
                            Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "两次不匹配", Toast.LENGTH_SHORT).show();
                        patternView.clearPattern();
                        patternString = null;
                    }
                } else {
                    c.close();
                    db = getActivity().openOrCreateDatabase("tachi.db", getActivity().MODE_PRIVATE, null);
                    c = db.rawQuery("select * from user", null);
                    if (c != null) {
                        while (c.moveToNext()) {
                            if (Objects.equals(MD5.encrypt(patternView.getPatternString()), c.getString(c.getColumnIndex("password")))) {
                                loginSuccess();
                            } else {
                                Toast.makeText(getActivity(), "图案错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                        c.close();
                    }
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    private void loginSuccess() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("logined", getActivity().MODE_PRIVATE).edit();
        String userlogined = "tachi";
        editor.putString("logined", userlogined);
        editor.apply();
        Toast.makeText(getActivity(), "登陆成功", Toast.LENGTH_SHORT).show();
        patternView.clearPattern();

        getActivity().setTitle("查询");
        FragmentQuery query = new FragmentQuery();
        FragmentManager fm;
        fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.id_content, query).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fingerAuth() {
        final FingerPrintAuth fingerPrintAuth = new FingerPrintAuth(getActivity(), fingerHandler);
        fingerPrintAuth.auth();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("指纹识别");
        builder.setMessage("正在识别");
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fingerPrintAuth.cancel();
            }
        });
        fingerDialog = builder.create();
        fingerDialog.show();
    }

    private Handler fingerHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    fingerDialog.dismiss();
                    loginSuccess();
                    break;
                case 0:
                    Toast.makeText(getActivity(), "验证失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private boolean isSetFinger() {
        try {
            preferences = getActivity().getSharedPreferences("setting", MODE_PRIVATE);
            String status = preferences.getString("finger", "");
            return Objects.equals(status, "true");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
