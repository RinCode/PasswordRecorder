package cc.tachi.passwordrecorder.Fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eftimoff.patternview.PatternView;

import java.util.Objects;

import cc.tachi.passwordrecorder.Crypt.AESHelper;
import cc.tachi.passwordrecorder.Crypt.MD5;
import cc.tachi.passwordrecorder.R;

/**
 * Created by m on 2016/11/9.
 *
 */
public class FragmentChangePasswd extends Fragment{
    private SQLiteDatabase db;
    private PatternView patternView;
    private String patternString;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_login, container, false);
        getActivity().setTitle("更改密码");
        db = getActivity().openOrCreateDatabase("tachi.db", getActivity().MODE_PRIVATE, null);
        patternView = (PatternView) view.findViewById(R.id.patternView);
        patternView.setTactileFeedbackEnabled(false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        patternView.setOnPatternDetectedListener(new PatternView.OnPatternDetectedListener() {
            @Override
            public void onPatternDetected() {
                if (patternString == null) {
                    patternString = patternView.getPatternString();
                    Toast.makeText(getActivity(), "请重复一次", Toast.LENGTH_SHORT).show();
                    patternView.clearPattern();
                    return;
                }
                if (patternString.equals(patternView.getPatternString())) {
                    patternView.clearPattern();
                    try {
                        db.execSQL("UPDATE user SET password = '"+MD5.encrypt(patternString)+"' WHERE user='';");
                        Toast.makeText(getActivity(), "修改成功，请重新登陆", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("logined", getActivity().MODE_PRIVATE).edit();
                        editor.putString("logined", "");
                        editor.apply();
                        Fragment login = new FragmentLogin();
                        FragmentManager fm=getActivity().getSupportFragmentManager();//清除回退stack
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fm.beginTransaction().replace(R.id.id_content, login).commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "修改失败", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "两次不匹配", Toast.LENGTH_SHORT).show();
                    patternView.clearPattern();
                    patternString = null;
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
