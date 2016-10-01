package cc.tachi.passwordrecorder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.util.Objects;

/**
 * Created by m on 2016/9/19.
 */
public class FragmentLogin extends Fragment {
    private SharedPreferences preferences;
    private EditText user;
    private EditText pass;
    private Button btnSubmit;
    private Button btnRegiste;
    private SQLiteDatabase db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_login, container, false);
        user = (EditText) view.findViewById(R.id.username);
        pass = (EditText) view.findViewById(R.id.password);
        btnSubmit = (Button) view.findViewById(R.id.loginbtn);
        btnRegiste = (Button) view.findViewById(R.id.registebtn);
        db = getActivity().openOrCreateDatabase("tachi.db", getActivity().MODE_PRIVATE, null);
        Cursor c = db.rawQuery("select * from user", null);
        if (!c.moveToNext()){
            btnRegiste.setEnabled(true);
        }else {
            btnRegiste.setEnabled(false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = getActivity().openOrCreateDatabase("tachi.db", getActivity().MODE_PRIVATE, null);
                Cursor c = db.rawQuery("select * from user", null);
                if (c != null) {
                    while (c.moveToNext()) {
                        if (Objects.equals(user.getText().toString(), c.getString(c.getColumnIndex("user"))) && Objects.equals(MD5.encrypt(pass.getText().toString()), c.getString(c.getColumnIndex("password")))) {
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("logined",getActivity().MODE_PRIVATE).edit();
                            String userlogined = "tachi";
                            editor.putString("logined", userlogined);
                            editor.apply();
                            Toast.makeText(getActivity(),"登陆成功",Toast.LENGTH_SHORT).show();
//                            nav_user.setText(user.getText().toString());
                            btnSubmit.setEnabled(false);

                            getActivity().setTitle("登录");
                            FragmentQuery query = new FragmentQuery();
                            FragmentManager fm;
                            android.support.v4.app.FragmentTransaction transaction;
                            fm = getActivity().getSupportFragmentManager();
                            transaction = fm.beginTransaction();
                            transaction.replace(R.id.id_content, query);
                            transaction.commit();
                        }
                    }
                    c.close();
                }
                db.close();
                Toast.makeText(getActivity(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegiste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    db.execSQL("insert into user (user,password) values ('"+ user.getText().toString()+"','"+MD5.encrypt(pass.getText().toString())+"');");
                    Toast.makeText(getActivity(),"注册成功",Toast.LENGTH_SHORT).show();
                    btnRegiste.setEnabled(false);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"注册失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
