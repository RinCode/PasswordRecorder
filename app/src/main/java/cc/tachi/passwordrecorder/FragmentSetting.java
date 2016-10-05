package cc.tachi.passwordrecorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by m on 2016/10/5.
 */
public class FragmentSetting extends Fragment {
    private ListView setting;
    private ArrayAdapter adapter;
    private FragmentManager fm;
    private FragmentBar bar;
    private FragmentAbout about;
    private android.support.v4.app.FragmentTransaction transaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting,container,false);
        setting = (ListView) view.findViewById(R.id.main_setting);
        adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1);
        adapter.add("备份和还原");
        adapter.add("检查更新");
        adapter.add("关于");
        setting.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        getActivity().setTitle("备份和还原");
                        bar = new FragmentBar();
                        fm = getActivity().getSupportFragmentManager();
                        transaction = fm.beginTransaction();
                        transaction.replace(R.id.id_content, bar);
                        transaction.commit();
                        break;
                    case 1:
                        Update update = new Update(getActivity());
                        update.checkUpdate(true);
                        break;
                    case 2:
                        getActivity().setTitle("关于");
                        about = new FragmentAbout();
                        fm = getActivity().getSupportFragmentManager();
                        transaction = fm.beginTransaction();
                        transaction.replace(R.id.id_content, about);
                        transaction.commit();
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
