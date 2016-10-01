package cc.tachi.passwordrecorder;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_detail, container, false);
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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(pass.getText().toString(), "秘钥错误")) {
                    Toast.makeText(getActivity(), "秘钥错误，无权更改", Toast.LENGTH_SHORT).show();
                } else {
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
                                    site.setEnabled(false);
                                    mail.setEnabled(false);
                                    user.setEnabled(false);
                                    pass.setEnabled(false);
                                    other.setEnabled(false);
                                    seed.setVisibility(View.GONE);
                                    seedlab.setVisibility(View.GONE);
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
        super.onActivityCreated(savedInstanceState);
    }
}
