package com.example.pacharapoldeesawat.demohospital;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pacharapoldeesawat.demohospital.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;


public class login extends AppCompatActivity {

    private EditText usrusr;
    private TextView sup;
    private Button lin;
    private String string2;
    private SharedPreferences mPrefs;
    private User person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PermissionManager.with(this)
                .permission(PermissionEnum.ACCESS_FINE_LOCATION, PermissionEnum.ACCESS_COARSE_LOCATION) // You can put all permissions here
                .askagain(true)
                .ask();

        boolean isFirstTime = MyPreferences.isFirst(login.this);
        Log.i("First time", String.valueOf(isFirstTime));

        if (!isFirstTime) {
            mPrefs = getSharedPreferences("label", 0);
            Gson gson = new Gson();
            String json = mPrefs.getString("MyObjectUser", "");
            person = gson.fromJson(json, User.class);

            if (person.getCitizenId() != null) {
                if (person.getRole().equals("user")) {
                    Intent it = new Intent(login.this, Miniinapp.class);
                    startActivity(it);
                } else if (person.getRole().equals("nurse")) {
                    Intent it = new Intent(login.this, Minimenu.class);
                    startActivity(it);
                }
            }
        } else {

            lin = findViewById(R.id.lin);
            usrusr = findViewById(R.id.usrusr);
            sup = findViewById(R.id.sup);
            Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/LatoLight.ttf");
            Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/LatoRegular.ttf");
            lin.setTypeface(custom_font1);
            sup.setTypeface(custom_font);
            usrusr.setTypeface(custom_font);

            lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseMessaging.getInstance().subscribeToTopic("demoFCM");

                    string2 = usrusr.getText().toString();
                    final User person = new User();

                    final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference users = root.child("users");
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public String toString() {
                            return "$classname{}";
                        }

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.child(string2).exists()) {
                                Toast.makeText(login.this, "Citizen " + string2 + " logged in.", Toast.LENGTH_SHORT).show();
                                //Log.i("First Store", mPrefs.getString("citizenId", null));
                                String xxx = snapshot.child(string2).getValue(String.class);
                                person.setCitizenId(string2);
                                person.setRole(xxx);
                                SharedPreferences m2Prefs = getSharedPreferences("label", 0);
                                SharedPreferences.Editor prefsEditor2 = m2Prefs.edit();
                                Gson gson2 = new Gson();
                                String json2 = gson2.toJson(person);
                                Log.i("json", json2);
                                prefsEditor2.putString("MyObjectUser", json2);
                                prefsEditor2.commit();

                                if (person.getRole().equals("nurse")) {
                                    Intent it = new Intent(login.this, Minimenu.class);
                                    startActivity(it);
                                } else {
                                    Intent it = new Intent(login.this, Miniinapp.class);
                                    startActivity(it);
                                }
                            } else {
                                Toast.makeText(login.this, "Citizen Invalid", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
        }


    }


}

