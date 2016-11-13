package cc.tachi.passwordrecorder.Fragment;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import cc.tachi.passwordrecorder.Crypt.AESHelper;
import cc.tachi.passwordrecorder.Other.GeneratePasswd;
import cc.tachi.passwordrecorder.Other.PasswdStrength;
import cc.tachi.passwordrecorder.R;

/**
 * Created by m on 2016/9/19.
 *
 */
public class FragmentDetail extends Fragment {
    private Button edit;
    private Button submit;
    private Button delete;
    private EditText site;
    private EditText mail;
    private EditText user;
    private EditText pass;
    private EditText other;
    private EditText seed;
    private TextView seedlab;
    private Bundle bundle;
    private SQLiteDatabase db;
    private TableRow g1;
    private TableRow g2;
    private TableRow strengthRow;
    private CheckBox num;
    private CheckBox lletter;
    private CheckBox hletter;
    private CheckBox symbol;
    private Button generate;
    private ProgressBar strength;
    private PasswdStrength passwdStrength;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_detail, container, false);
        getActivity().setTitle("详细信息");
        db = getActivity().openOrCreateDatabase("tachi.db", getActivity().MODE_PRIVATE, null);
        edit = (Button) view.findViewById(R.id.edititem);
        submit = (Button) view.findViewById(R.id.submitedit);
        delete = (Button) view.findViewById(R.id.deleteitem);
        site = (EditText) view.findViewById(R.id.site);
        mail = (EditText) view.findViewById(R.id.mail);
        user = (EditText) view.findViewById(R.id.uname);
        pass = (EditText) view.findViewById(R.id.upass);
        other = (EditText) view.findViewById(R.id.other);
        seed = (EditText) view.findViewById(R.id.seed);
        seedlab = (TextView) view.findViewById(R.id.seedlab);
        bundle = getArguments();
        site.setEnabled(false);
        mail.setEnabled(false);
        user.setEnabled(false);
        pass.setEnabled(false);
        other.setEnabled(false);
        site.setText(bundle.getString("site"));
        mail.setText(bundle.getString("mail"));
        user.setText(bundle.getString("user"));
        pass.setText(bundle.getString("pass"));
        other.setText(bundle.getString("other"));
        generate = (Button) view.findViewById(R.id.generate);
        g1 = (TableRow) view.findViewById(R.id.g1);
        g2 = (TableRow) view.findViewById(R.id.g2);
        strengthRow = (TableRow) view.findViewById(R.id.strengthRow);
        num = (CheckBox) view.findViewById(R.id.checkBoxNum);
        lletter = (CheckBox) view.findViewById(R.id.checkBoxLLetter);
        hletter = (CheckBox) view.findViewById(R.id.checkBoxHLetter);
        symbol = (CheckBox) view.findViewById(R.id.checkBoxSymbol);
        strength = (ProgressBar) view.findViewById(R.id.strength);
        generate.setVisibility(View.GONE);
        strength.setMax(100);
        strength.setProgress(0);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(pass.getText().toString(), "秘钥错误")) {
                    Toast.makeText(getActivity(), "秘钥错误，无权更改", Toast.LENGTH_SHORT).show();
                } else {
                    seed.setText(bundle.getString("key"));
                    site.setEnabled(true);
                    mail.setEnabled(true);
                    user.setEnabled(true);
                    pass.setEnabled(true);
                    other.setEnabled(true);
                    submit.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    seedlab.setVisibility(View.VISIBLE);
                    seed.setVisibility(View.VISIBLE);
                    generate.setVisibility(View.VISIBLE);
                    strengthRow.setVisibility(View.VISIBLE);
                    passwdStrength = new PasswdStrength(pass.getText().toString());
                    int result = passwdStrength.score();
                    strength.setProgress((result>=0)?result:0);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity()).setTitle("确认修改？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    db.execSQL("UPDATE data SET site = '" + site.getText().toString() + "', mail='" + mail.getText().toString() + "',user = '" + user.getText().toString() + "',pass='" + AESHelper.encrypt(seed.getText().toString(), pass.getText().toString()) + "',other='" + other.getText().toString() + "' WHERE id=" + bundle.getString("id") + ";");
                                    Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                                    submit.setVisibility(View.GONE);
                                    delete.setVisibility(View.VISIBLE);
                                    edit.setVisibility(View.VISIBLE);
                                    generate.setVisibility(View.GONE);
                                    site.setEnabled(false);
                                    mail.setEnabled(false);
                                    user.setEnabled(false);
                                    pass.setEnabled(false);
                                    other.setEnabled(false);
                                    g1.setVisibility(View.GONE);
                                    g2.setVisibility(View.GONE);
                                    seed.setVisibility(View.GONE);
                                    seedlab.setVisibility(View.GONE);
                                    strengthRow.setVisibility(View.GONE);
                                    pass.setTextColor(user.getTextColors());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "已取消", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(pass.getText().toString(), "秘钥错误")) {
                    Toast.makeText(getActivity(), "秘钥错误，无权更改", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getActivity()).setTitle("确认删除？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        db.execSQL("delete from data where id = '" + bundle.getString("id") + "'");
                                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                        delete.setVisibility(View.GONE);
                                        edit.setVisibility(View.GONE);
                                        generate.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(), "已取消", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
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
