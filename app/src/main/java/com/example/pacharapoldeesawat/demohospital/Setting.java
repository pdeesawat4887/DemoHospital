package com.example.pacharapoldeesawat.demohospital;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pacharapoldeesawat.demohospital.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Setting extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("การตั้งค่า");
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleBar));
        setSupportActionBar(toolbar);


        SharedPreferences mPrefs2 = getSharedPreferences("label", 0);
        SharedPreferences.Editor mPerfsEdit = mPrefs2.edit();
        Gson gson3 = new Gson();
        String json3 = mPrefs2.getString("MyObjectUser", "");
        obj = gson3.fromJson(json3, User.class);

        Button updateDistance = findViewById(R.id.resetDistanceBtn);
        final EditText setDistance = findViewById(R.id.setFarDistance);


        updateDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference mainRoot = FirebaseDatabase.getInstance().getReference();
                mainRoot.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mainRoot.child("distance").setValue(Float.parseFloat(String.valueOf(setDistance.getText()))*1000);
                        Toast.makeText(getApplicationContext(), "เปลี่ยนแปลงระยะห่างเรียบร้อย",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        Button reset = findViewById(R.id.resetAll);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this)
                        .setTitle("ลบข้อมูลการจองคิว")
                        .setMessage("หากทำการลบข้อมูลแล้วจะไม่สามารถกู้คืนการจองคิวได้ โปรดระมัดระวังในการลบข้อมูล ยินยันที่จะลบข้อมูลในระบบ")
                        .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetDatabase();
                            }
                        })
                        .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Setting.this, "ยกเลิก",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        ImageView avatar = (ImageView) header.findViewById(R.id.avatar);
        TextView role = (TextView)header.findViewById(R.id.userRole);
        TextView id = (TextView) header.findViewById(R.id.id);

        avatar.setImageResource(R.drawable.ic_021_nurse);
        role.setText(R.string.nurse);
        id.setText("เลขประจำตัว: "+obj.getCitizenId());
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
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_walk) {
            if (obj.getRole().equals("nurse")){
                Intent it = new Intent(Setting.this, WalkInActivity.class);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(),"คุณต้องมีสิทธิ์เข้าถึง",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_phone) {
            Intent it = new Intent(Setting.this, InAppActivity.class);
            startActivity(it);
        } else if (id == R.id.nav_manage) {
            if (obj.getRole().equals("nurse")){
                Intent it = new Intent(Setting.this, CallQueue.class);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(),"คุณต้องมีสิทธิ์เข้าถึง",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_setting) {
            if (obj.getRole().equals("nurse")){
                Intent it = new Intent(Setting.this, Setting.class);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(),"คุณต้องมีสิทธิ์เข้าถึง",Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_logout) {
            Intent it = new Intent(Setting.this, LogoutActivity.class);
            startActivity(it);
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void resetDatabase(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        int timebox[] = {1,2,3,4,5,6,7,8,9,10};
        for (int child:timebox){
            root.child(String.valueOf(child)).child("A").setValue(0);
            root.child(String.valueOf(child)).child("B").setValue(0);
        }
        root.child("A").setValue(0);
        root.child("B").setValue(0);
        root.child("demoQueue").removeValue();
        root.child("useQueue").removeValue();
        root.child("queueB_Oneday").removeValue();
        root.child("distance").setValue(1500);
        Toast.makeText(Setting.this, "คืนค่าเริ่มต้นสำเร็จ",Toast.LENGTH_LONG).show();
    }
}
