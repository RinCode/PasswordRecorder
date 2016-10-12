package cc.tachi.passwordrecorder.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import cc.tachi.passwordrecorder.R;

/**
 * Created by m on 2016/10/4.
 *
 * http://www.2cto.com/kf/201409/330644.html
 */
public class FragmentBar extends Fragment{
    private Button backup;
    private Button restore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_backupandrestore, container, false);
        backup= (Button) view.findViewById(R.id.backup);
        restore = (Button) view.findViewById(R.id.restore);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        final String DATABASE_NAME = "tachi.db";
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPath = "data/data/cc.tachi.passwordrecorder/databases/" + DATABASE_NAME;
                String newPath = Environment.getExternalStorageDirectory() + "/tachicc/" + DATABASE_NAME;
                copyFile(oldPath, newPath,1);
            }
        });
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity()).setTitle("确认要还原数据？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newPath = "data/data/cc.tachi.passwordrecorder/databases/" + DATABASE_NAME;
                                String oldPath = Environment.getExternalStorageDirectory() + "/tachicc/" + DATABASE_NAME;
                                copyFile(oldPath, newPath,0);
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
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath,int method) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if(method==1) {
                if (!newfile.exists()) {
                    newfile.createNewFile();
                }
                if (oldfile.exists()) { // 文件存在时
                    InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                    FileOutputStream fs = new FileOutputStream(newPath);
                    byte[] buffer = new byte[1444];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread; // 字节数 文件大小
                        fs.write(buffer, 0, byteread);
                    }
                    inStream.close();
                    Toast.makeText(getActivity(), "文件已保存到/tachicc/tachi.db", Toast.LENGTH_SHORT).show();
                }
            }else {
                if (!newfile.exists()) {
                    newfile.createNewFile();
                }
                if (oldfile.exists()) { // 文件存在时
                    InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                    FileOutputStream fs = new FileOutputStream(newPath);
                    byte[] buffer = new byte[1444];
                    while ((byteread = inStream.read(buffer)) != -1) {
                        bytesum += byteread; // 字节数 文件大小
                        fs.write(buffer, 0, byteread);
                    }
                    inStream.close();
                    Toast.makeText(getActivity(), "恢复成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "备份文件不存在", Toast.LENGTH_SHORT).show();

                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "发生错误", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }

    }
}
