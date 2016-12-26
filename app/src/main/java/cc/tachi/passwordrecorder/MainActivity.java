package cc.tachi.passwordrecorder;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

import cc.tachi.passwordrecorder.Fragment.FragmentAdd;
import cc.tachi.passwordrecorder.Fragment.FragmentLogin;
import cc.tachi.passwordrecorder.Fragment.FragmentQuery;
import cc.tachi.passwordrecorder.Fragment.FragmentSetting;
import cc.tachi.passwordrecorder.Other.Update;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentLogin login;
    private FragmentQuery query;
    private FragmentAdd add;
    private FragmentSetting setting;
    private FragmentManager fm;
    private SharedPreferences preferences;
    private Context context;
    private ImageView myimage;
    private NavigationView navigationView;
    private final static String ONE = "query";
    private final static String TWO = "add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        fm = getSupportFragmentManager();
        query = new FragmentQuery();
        login = new FragmentLogin();
        add = new FragmentAdd();
        setting = new FragmentSetting();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences = getSharedPreferences("logined", MODE_PRIVATE);
                String name = preferences.getString("logined", "");
                if (!Objects.equals(name, "")) {
                    fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, query).commit();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        View header = navigationView.getHeaderView(0);
        myimage = (ImageView) header.findViewById(R.id.myimage);
        myimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        init();
        if (logined())
            switch (getIntent().getAction()) {
                case ONE:
                    fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, query).commit();
                    break;
                case TWO:
                    fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, add).commit();
                    break;
                default:
                    break;
            }
    }

    private void init() {

        //权限申请
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        //

        //检查更新
        Update update = new Update(context);
        update.checkUpdate();
        //


        //创建/读取数据库
        SQLiteDatabase db = openOrCreateDatabase("tachi.db", MODE_PRIVATE, null);

        db.execSQL("create table if not exists user (id integer primary key autoincrement, user text not null , password text not null )");
        db.execSQL("create table if not exists data (id integer primary key autoincrement, site text not null , mail text not null , user text not null, pass text not null, other text not null )");
        db.close();
        //

        //登录判断
        preferences = getSharedPreferences("logined", MODE_PRIVATE);
        String name = preferences.getString("logined", "");
        if (Objects.equals(name, "")) {
            fm.beginTransaction().replace(R.id.id_content, login).commit();
        } else {
            fm.beginTransaction().replace(R.id.id_content, query).commit();
        }
        //
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SharedPreferences.Editor editor = this.getSharedPreferences("logined", this.MODE_PRIVATE).edit();
            editor.putString("logined", "");
            editor.apply();
            Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = getSupportFragmentManager();//清除回退stack
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fm.beginTransaction().replace(R.id.id_content, login).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (logined()) {
            if (id == R.id.nav_query) {
                fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, query).commit();
            } else if (id == R.id.nav_add) {
                fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, add).commit();
            } else if (id == R.id.nav_setting) {
                fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, setting).commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean logined() {
        preferences = getSharedPreferences("logined", MODE_PRIVATE);
        String name = preferences.getString("logined", "");
        if (Objects.equals(name, "")) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, login).commit();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "拒绝存储权限将会导致软件更新失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
