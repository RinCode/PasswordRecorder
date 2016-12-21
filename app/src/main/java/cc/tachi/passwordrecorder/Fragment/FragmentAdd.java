package cc.tachi.passwordrecorder.Fragment;

import android.annotation.TargetApi;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.Objects;

import cc.tachi.passwordrecorder.Crypt.AESHelper;
import cc.tachi.passwordrecorder.Other.GeneratePasswd;
import cc.tachi.passwordrecorder.Other.PasswdStrength;
import cc.tachi.passwordrecorder.R;

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
    private Button generate;
    private SQLiteDatabase db;
    private TableRow g1;
    private TableRow g2;
    private CheckBox num;
    private CheckBox lletter;
    private CheckBox hletter;
    private CheckBox symbol;
    private ProgressBar strength;
    private PasswdStrength passwdStrength;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_add, container, false);
        getActivity().setTitle("添加");
        seed = (EditText) view.findViewById(R.id.seed);
        site = (EditText) view.findViewById(R.id.site);
        mail = (EditText) view.findViewById(R.id.mail);
        user = (EditText) view.findViewById(R.id.uname);
        pass = (EditText) view.findViewById(R.id.upass);
        other = (EditText) view.findViewById(R.id.other);
        submit = (Button) view.findViewById(R.id.additem);
        generate = (Button) view.findViewById(R.id.generate);
        g1 = (TableRow) view.findViewById(R.id.g1);
        g2 = (TableRow) view.findViewById(R.id.g2);
        num = (CheckBox) view.findViewById(R.id.checkBoxNum);
        lletter = (CheckBox) view.findViewById(R.id.checkBoxLLetter);
        hletter = (CheckBox) view.findViewById(R.id.checkBoxHLetter);
        symbol = (CheckBox) view.findViewById(R.id.checkBoxSymbol);
        strength = (ProgressBar) view.findViewById(R.id.strength);
        strength.setMax(100);
        strength.setProgress(0);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        GeneratePasswd generatePasswd = new GeneratePasswd();
        String result = generatePasswd.generate(8, 7);
        pass.setText(result);
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwdStrength = new PasswdStrength(pass.getText().toString());
                int result = passwdStrength.score();
                strength.setProgress((result>=0)?result:0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                        pass.setTextColor(user.getTextColors());
                        pass.setHintTextColor(user.getHintTextColors());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "添加失败", Toast.LENGTH_SHORT).show();
                    }
                }
                db.close();
            }
        });
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (generate.getText().equals("生成")) {
                    g1.setVisibility(View.VISIBLE);
                    g2.setVisibility(View.VISIBLE);
                    pass.setTextColor(Color.BLUE);
                    pass.setHintTextColor(Color.BLUE);
                    pass.setHint("填入长度");
                    generate.setText("确定");
                } else {
                    int method = 0;
                    if (num.isChecked())
                        method += 1;
                    if (lletter.isChecked())
                        method += 2;
                    if (hletter.isChecked())
                        method += 4;
                    if (symbol.isChecked())
                        method += 8;
                    GeneratePasswd generatePasswd = new GeneratePasswd();
                    String result;
                    try {
                        int newlength = Integer.parseInt(pass.getText().toString());
                        if (newlength < 20) {
                            result = generatePasswd.generate(newlength, method);
                            pass.setText(result);
                        } else {
                            pass.setText("");
                            pass.setHint("长度太长");
                        }
                    } catch (Exception e) {
                        pass.setText("");
                        pass.setHint("填入长度");
                    }
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
