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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentLogin login;
    private FragmentQuery query;
    private FragmentAdd add;
    private FragmentSetting setting;
    private FragmentManager fm;
    private android.support.v4.app.FragmentTransaction transaction;
    private SharedPreferences preferences;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                preferences = getSharedPreferences("logined", MODE_PRIVATE);
                String name = preferences.getString("logined", "");
                Log.i("name", name);
                if (!Objects.equals(name, "")) {
                    setTitle("添加");
                    transaction = fm.beginTransaction();
                    transaction.replace(R.id.id_content, add);
                    transaction.commit();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fm = getSupportFragmentManager();
        query = new FragmentQuery();
        login = new FragmentLogin();
        add = new FragmentAdd();
        setting = new FragmentSetting();
        init();
    }

    private void init() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }

        Update update = new Update(context);
        update.checkUpdate();

        SQLiteDatabase db = openOrCreateDatabase("tachi.db", MODE_PRIVATE, null);

        db.execSQL("create table if not exists user (id integer primary key autoincrement, user text not null , password text not null )");
        db.execSQL("create table if not exists data (id integer primary key autoincrement, site text not null , mail text not null , user text not null, pass text not null, other text not null )");
        db.close();

        preferences = getSharedPreferences("logined", MODE_PRIVATE);
        String name = preferences.getString("logined", "");
        if (Objects.equals(name, "")) {
            transaction = fm.beginTransaction();
            transaction.replace(R.id.id_content, login);
            transaction.commit();
        } else {
            transaction = fm.beginTransaction();
            transaction.replace(R.id.id_content, query);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
//            if (logined()) {
//                this.setTitle("设置");
//                transaction = fm.beginTransaction();
//                transaction.replace(R.id.id_content, setting);
//                transaction.commit();
//                return true;
//            }
            SharedPreferences.Editor editor = this.getSharedPreferences("logined", this.MODE_PRIVATE).edit();
            editor.putString("logined", "");
            editor.apply();
            Toast.makeText(this,"已退出登录",Toast.LENGTH_SHORT).show();
            this.setTitle("登录");
            transaction = fm.beginTransaction();
            transaction.replace(R.id.id_content, login);
            transaction.commit();
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
                this.setTitle("查询");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.id_content, query);
                transaction.commit();
            } else if (id == R.id.nav_add) {
                this.setTitle("添加");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.id_content, add);
                transaction.commit();
            } else if (id == R.id.nav_setting) {
                this.setTitle("设置");
                transaction = fm.beginTransaction();
                transaction.replace(R.id.id_content, setting);
                transaction.commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(logined()){
            this.setTitle("查询");
            transaction = fm.beginTransaction();
            transaction.replace(R.id.id_content, query);
            transaction.commit();
        }
        return false;
    }

    public boolean logined() {
        preferences = getSharedPreferences("logined", MODE_PRIVATE);
        String name = preferences.getString("logined", "");
        if (Objects.equals(name, "")) {
            Toast.makeText(this,"请先登录",Toast.LENGTH_SHORT).show();
            this.setTitle("登录");
            transaction = fm.beginTransaction();
            transaction.replace(R.id.id_content, login);
            transaction.commit();
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
