package cc.tachi.passwordrecorder;

import android.annotation.TargetApi;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;

import java.util.Objects;

/**
 * Created by m on 2016/9/19.
 */
public class FragmentAdd extends Fragment {
    private EditText site;
    private EditText mail;
    private EditText user;
    private EditText pass;
    private EditText other;
    private EditText seed;
    private Button submit;
    private SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_add, container, false);
        seed = (EditText) view.findViewById(R.id.seed);
        site = (EditText) view.findViewById(R.id.site);
        mail = (EditText) view.findViewById(R.id.mail);
        user = (EditText) view.findViewById(R.id.uname);
        pass = (EditText) view.findViewById(R.id.upass);
        other = (EditText) view.findViewById(R.id.other);
        submit = (Button) view.findViewById(R.id.additem);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        submit.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                db = getActivity().openOrCreateDatabase("tachi.db", getActivity().MODE_PRIVATE, null);
                if (Objects.equals(site.getText().toString(), "") || Objects.equals(mail.getText().toString(), "")) {
                    Toast.makeText(getActivity(), "请至少填入网址或邮箱", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        db.execSQL("insert into data(site,mail,user,pass,other) values('" + site.getText().toString() + "','" + mail.getText().toString() + "','" + user.getText().toString() + "','" + AESHelper.encrypt(seed.getText().toString(), pass.getText().toString()) + "','" + other.getText().toString() + "')");
                        Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
                        site.setText("");
                        mail.setText("");
                        user.setText("");
                        pass.setText("");
                        other.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "添加失败", Toast.LENGTH_SHORT).show();
                    }
                }
                db.close();
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
