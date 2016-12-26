package cc.tachi.passwordrecorder.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Objects;

import cc.tachi.passwordrecorder.Other.FingerPrintAuth;
import cc.tachi.passwordrecorder.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tachi on 2016/12/26.
 */

public class FragmentSettingLogin extends Fragment {
    private ArrayAdapter adapter;
    private ListView lsetting;
    private FragmentChangePasswd fc;
    private FragmentManager fm;
    private Dialog fingerDialog;
    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_login, container, false);
        lsetting = (ListView) view.findViewById(R.id.content);
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);
        lsetting.setAdapter(adapter);
        getStatus();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        lsetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        fc = new FragmentChangePasswd();
                        fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, fc).commit();
                        break;
                    case 1:
                        if(adapterView.getItemAtPosition(i)=="启用指纹登录") {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                            } else {
                                Toast.makeText(getActivity(), "当前系统版本无法启用此功能。", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            try {
                                changeSetting("false");
                                adapter.clear();
                                adapter.add("修改登录密码");
                                adapter.add("启用指纹登录");
                                adapter.notifyDataSetChanged();
                            }catch (Exception e){
                                Toast.makeText(getActivity(),"关闭失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
    private Handler fingerHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    fingerDialog.dismiss();
                    try {
                        changeSetting("true");
                        Toast.makeText(getActivity(),"启用成功",Toast.LENGTH_SHORT).show();
                        adapter.clear();
                        adapter.add("修改登录密码");
                        adapter.add("关闭指纹登录");
                        adapter.notifyDataSetChanged();
                    }catch (Exception e){
                        Toast.makeText(getActivity(),"启用失败，未知原因",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0:
                    Toast.makeText(getActivity(),"验证失败",Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    fingerDialog.dismiss();
                    Toast.makeText(getActivity(),"无指纹传感器或当前未设置指纹，无法启用",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void changeSetting(String status){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("setting", getActivity().MODE_PRIVATE).edit();
        editor.putString("finger", status);
        editor.apply();
    }

    private void getStatus(){
        preferences = getActivity().getSharedPreferences("setting", MODE_PRIVATE);
        String status = preferences.getString("finger", "");
        if (Objects.equals(status, "true")) {
            adapter.clear();
            adapter.add("修改登录密码");
            adapter.add("关闭指纹登录");
            adapter.notifyDataSetChanged();
        } else {
            adapter.clear();
            adapter.add("修改登录密码");
            adapter.add("启用指纹登录");
            adapter.notifyDataSetChanged();
        }
    }
}