package cc.tachi.passwordrecorder;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentLogin login;
    private FragmentQuery query;
    private FragmentAdd add;
    private FragmentManager fm;
    private android.support.v4.app.FragmentTransaction transaction;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                preferences = getSharedPreferences("logined", MODE_PRIVATE);
                String name = preferences.getString("logined","");
                Log.i("name",name);
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
        init();
    }

    private void init() {
        SQLiteDatabase db = openOrCreateDatabase("tachi.db", MODE_PRIVATE, null);

        db.execSQL("create table if not exists user (id integer primary key autoincrement, user text not null , password text not null )");
        db.execSQL("create table if not exists data (id integer primary key autoincrement, site text not null , mail text not null , user text not null, pass text not null, other text not null )");


        preferences = getSharedPreferences("logined", MODE_PRIVATE);
        String name = preferences.getString("logined","");
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
//            Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
            try {
                String encryptedData = AESHelper.encrypt("123456", "mabao");
                Log.v("EncryptDecrypt", "Encoded String " + encryptedData);
                try {
                    String decryptedData = AESHelper.decrypt("12345", encryptedData);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.v("info","error");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        preferences = getSharedPreferences("logined", MODE_PRIVATE);
        String name = preferences.getString("logined","");
        Log.i("name",name);
        if (Objects.equals(name, "")) {
            this.setTitle("登录");
            transaction = fm.beginTransaction();
            transaction.replace(R.id.id_content, login);
            transaction.commit();
        } else {
            if (id == R.id.nav_login) {
                Toast.makeText(this,"已登录",Toast.LENGTH_SHORT).show();
            } else {
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
                }
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
}
