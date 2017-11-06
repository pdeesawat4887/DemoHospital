package com.example.pacharapoldeesawat.demohospital;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pacharapoldeesawat.demohospital.Model.Queue;
import com.example.pacharapoldeesawat.demohospital.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Timer;


public class Manage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference root;
    private DatabaseReference mPerf;
    private TableLayout stk;
    private TextView t1v;
    private Button btnOk;
    private Button refresh;
    private Button push;
    private Timer timer;
    private TextView t2v;
    private int index;
    private User obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences mPrefs2 = getSharedPreferences("label", 0);
        SharedPreferences.Editor mPerfsEdit = mPrefs2.edit();
        Gson gson3 = new Gson();
        String json3 = mPrefs2.getString("MyObjectUser", "");
        obj = gson3.fromJson(json3, User.class);

        stk = findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setPadding(10, 0, 10, 0);
        TextView tv0 = new TextView(this);
        tv0.setText(" คิว ");
        tv0.setTextColor(Color.WHITE);
        tv0.setGravity(Gravity.CENTER);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" เวลาที่เหลือ ");
        tv1.setTextColor(Color.WHITE);
        tv1.setGravity(Gravity.CENTER);
        tbrow0.addView(tv1);
        final TextView tv2 = new TextView(this);
        tv2.setText(" มาแล้ว ");
        tv2.setTextColor(Color.WHITE);
        tv2.setGravity(Gravity.CENTER);
        tbrow0.addView(tv2);
        stk.addView(tbrow0);


        refresh = findViewById(R.id.refresh);
        push = findViewById(R.id.push);

        mPerf = FirebaseDatabase.getInstance().getReference();
        root = FirebaseDatabase.getInstance().getReference();

        push.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                root.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child("useQueue").exists()) {
                            Toast.makeText(getApplicationContext(), "ในขณะนี้ไม่มีการจองคิวเข้ามา", Toast.LENGTH_SHORT).show();
                        } else {
                            Query pushQuery = mPerf.child("useQueue").limitToFirst(1);


                            pushQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot queueSnapshot : dataSnapshot.getChildren()) {

                                        Queue qqq = queueSnapshot.getValue(Queue.class);
                                        if (qqq.getType().equals("B")) {
                                            init(qqq.getType() + qqq.getQueueNum(), 900000);
                                            root.child("queueB_Oneday").child(qqq.getId()).removeValue();
                                        } else {
                                            init(qqq.getType() + qqq.getQueueNum(), 300000);
                                        }

                                        queueSnapshot.getRef().removeValue();

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        ImageView avatar = (ImageView) header.findViewById(R.id.avatar);
        if (obj.getRole().equals("nurse")){
            avatar.setImageResource(R.drawable.ic_021_nurse);
        } else {
            avatar.setImageResource(R.drawable.ic_009_sick);
        }

        TextView role = (TextView)header.findViewById(R.id.userRole);
        role.setText(obj.getRole().toUpperCase());
        TextView id = (TextView) header.findViewById(R.id.id);
        id.setText("ID : "+obj.getCitizenId());
    }

    public void countdown(final TextView textTime, long time, long sec) {
        new CountDownTimer(time, sec) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int hours = seconds / (60 * 60);
                int tempMint = (seconds - (hours * 60 * 60));
                int minutes = tempMint / 60;
                seconds = tempMint - (minutes * 60);
                textTime.setText(String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                textTime.setText("หมดเวลา!");
                t2v.setTextColor(Color.RED);
            }
        }.start();
    }

    public void init(String text1, int type) {
        TableRow tbrow = new TableRow(this);
        tbrow.setPadding(0, 0, 0, 0);
        btnOk = new Button(this);
        btnOk.setText("-Ok-");
        t1v = new TextView(this);
        t1v.setText(text1);
        t1v.setTextColor(Color.WHITE);
        t1v.setGravity(Gravity.CENTER);
        t2v = new TextView(this);
        countdown(t2v, type, 1000);
        t2v.setTextColor(Color.WHITE);
        t2v.setGravity(Gravity.CENTER);
        tbrow.addView(t1v);
        tbrow.addView(t2v);
        tbrow.addView(btnOk);
        stk.addView(tbrow);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = (TableRow) view.getParent();
                index = stk.indexOfChild(row);
                stk.removeView(stk.getChildAt(index));
            }
        });

    }


    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manage, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_walk) {
            if (obj.getRole().equals("nurse")){
                Intent it = new Intent(Manage.this, WalkInActivity.class);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(),"ไม่อนุญาตให้เข้าได้",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_phone) {
            Intent it = new Intent(Manage.this, Miniinapp.class);
            startActivity(it);
        } else if (id == R.id.nav_manage) {
            if (obj.getRole().equals("nurse")){
                Intent it = new Intent(Manage.this, Manage.class);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(),"ไม่อนุญาตให้เข้าได้",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_setting) {
            if (obj.getRole().equals("nurse")){
                Intent it = new Intent(Manage.this, Setting.class);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(),"ไม่อนุญาตให้เข้าได้",Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
