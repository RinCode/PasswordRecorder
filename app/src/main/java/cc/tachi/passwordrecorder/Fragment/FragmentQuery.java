package cc.tachi.passwordrecorder.Fragment;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cc.tachi.passwordrecorder.Crypt.AESHelper;
import cc.tachi.passwordrecorder.GeneratePasswd;
import cc.tachi.passwordrecorder.R;

/**
 * Created by m on 2016/9/17.
 */
public class FragmentQuery extends Fragment {
    private Button querybtn;
    private EditText querycontent;
    private EditText seed;
    private Spinner spinner;
    private SQLiteDatabase db;
    private ArrayList<Map<String, Object>> datalist;
    private SimpleAdapter adapter;
    private ListView lv;
    private FragmentManager fm;
    private android.support.v4.app.FragmentTransaction transaction;
    private FragmentDetail detail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_query, container, false);
        querybtn = (Button) view.findViewById(R.id.query);
        querycontent = (EditText) view.findViewById(R.id.querytype);
        seed = (EditText) view.findViewById(R.id.seed);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        db = getActivity().openOrCreateDatabase("tachi.db", getActivity().MODE_PRIVATE, null);
        datalist = new ArrayList<Map<String, Object>>();
        lv = (ListView) view.findViewById(R.id.listView);
        adapter = new SimpleAdapter(getActivity(), datalist, R.layout.item, new String[]{"id", "site"}, new int[]{R.id.showid, R.id.showsite});
        lv.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                HashMap content = (HashMap) adapterView.getItemAtPosition(i);
                if (!Objects.equals(content.get("id").toString(), "-1")) {
                    Bundle bundle = new Bundle();
                    Cursor c = db.rawQuery("select * from data where id =" + content.get("id").toString(), null);
                    if (c != null) {
                        while (c.moveToNext()) {
                            bundle.putString("id", c.getString(c.getColumnIndex("id")));
                            bundle.putString("site", c.getString(c.getColumnIndex("site")));
                            bundle.putString("mail", c.getString(c.getColumnIndex("mail")));
                            bundle.putString("user", c.getString(c.getColumnIndex("user")));
                            String password = c.getString(c.getColumnIndex("pass"));
                            try {
                                password = AESHelper.decrypt(seed.getText().toString(), password);
                            } catch (Exception e) {
                                password = "秘钥错误";
                            }
                            bundle.putString("key",seed.getText().toString());
                            bundle.putString("pass", password);
                            bundle.putString("other", c.getString(c.getColumnIndex("other")));
                        }
                        c.close();
                    }
                    fm = getActivity().getSupportFragmentManager();
                    detail = new FragmentDetail();
                    detail.setArguments(bundle);
                    fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, detail).commit();
                } else {
                    Toast.makeText(getActivity(), "无项目", Toast.LENGTH_SHORT).show();
                }
            }
        });
        querybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datalist.clear();
                Cursor c;
                if(spinner.getSelectedItemPosition()==0){
                    c = db.rawQuery("select * from data where site like '%" + querycontent.getText().toString() + "%'", null);
                }
                else {
                    c = db.rawQuery("select * from data where mail like '%" + querycontent.getText().toString() + "%'", null);
                }
                if (c != null) {
                    while (c.moveToNext()) {
                        final HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("id", c.getString(c.getColumnIndex("id")));
                        map.put("site", c.getString(c.getColumnIndex("site")));
                        datalist.add(map);
                    }
                    c.close();
                }
                if (datalist.isEmpty()) {
                    final HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("id", -1);
                    map.put("site", "无结果");
                    datalist.add(map);
                }
                adapter.notifyDataSetChanged();
                querycontent.setEnabled(false);
                querycontent.setEnabled(true);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
