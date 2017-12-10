package com.example.pacharapoldeesawat.demohospital;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.EditTextPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pacharapoldeesawat.demohospital.Model.Queue;
import com.example.pacharapoldeesawat.demohospital.Model.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;


public class InAppActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Button checkLocation;
    private final double hospitalLat = 13.722385;
    private final double hospitalLong = 100.784024;
    private double loc2Latitude;
    private double loc2Longitude;
    private float distanceInMeters;
    private User obj;
    private DatabaseReference root2;

    private static final String TAG = "CheckApp";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;    // 10 sec
    private long FASTEST_INTERVAL = 2000;       // 2 sec
    private LocationManager locationManager;
    private long queueB;
    SharedPreferences.Editor prefsEditor2;

    private boolean doubleBackToExitPressedOnce = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miniinapp);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("จองคิวผ่านแอปพลิเคชัน");
        toolbar.setTitleTextColor(getResources().getColor(R.color.titleBar));
//        toolbar.setLogo(R.drawable.ic_phone);
        setSupportActionBar(toolbar);

        final TextView youQ = (TextView) findViewById(R.id.youQ);


        SharedPreferences mPrefs2 = getSharedPreferences("label", 0);
        Gson gson3 = new Gson();
        String json3 = mPrefs2.getString("MyObjectUser", "");
        obj = gson3.fromJson(json3, User.class);
        prefsEditor2 = mPrefs2.edit();



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation();

        final TextView remainQ = findViewById(R.id.remainQueue);

        checkMe();


        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                remainQ.setText("รอคิวประมาณ " + (dataSnapshot.child("useQueue").getChildrenCount()*4) + " นาที");

                if (dataSnapshot.child("queueB_Oneday").child(obj.getCitizenId()).getValue() != null){
                    queueB = (long) dataSnapshot.child("queueB_Oneday").child(obj.getCitizenId()).getValue();
                    prefsEditor2.putString("queueB", "B"+queueB).commit();
                    youQ.setText("คิวของคุณคือ B"+ queueB);
                }else {
                    youQ.setText("คุณยังไม่ได้ทำการจองคิว");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button queueInApp = findViewById(R.id.inappbtn);
        queueInApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Location loc1 = new Location("");
                loc1.setLatitude(hospitalLat);
                loc1.setLongitude(hospitalLong);
                Location loc2 = new Location("");
                loc2.setLatitude(loc2Latitude);
                loc2.setLongitude(loc2Longitude);
                distanceInMeters = loc1.distanceTo(loc2);

                root2 = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            Long farDistance = (long) dataSnapshot.child("distance").getValue();
                            float farDisInt = farDistance.floatValue();
                            GetTime time = new GetTime();
                            time.whatTimeIsIt();
                            int checkTimeBox = new CheckTimeBox().checkTimeBox(Integer.parseInt(time.getHour()), Integer.parseInt(time.getMin()));
                            long checkLimit = (long) dataSnapshot.child(String.valueOf(checkTimeBox)).child("B").getValue();

                            if (checkLimit > 9) {
                                Toast.makeText(getApplicationContext(), "ขออภัยในความไม่สะดวกตอนนี้คิวเต็ม กรุณากดใหม่ หลังจากนี้ ??? นาที", Toast.LENGTH_LONG).show();
                            } else {
                                if (dataSnapshot.child("queueB_Oneday").child(obj.getCitizenId()).exists()) {
                                    Toast.makeText(getApplicationContext(), "คุณทำการจองคิวไปแล้ว", Toast.LENGTH_LONG).show();
                                } else {
                                    if (distanceInMeters > farDistance) {

                                        Toast.makeText(getApplicationContext(), "คุณอยู่ห่างจากโรงพยาบาลเกิน "+ (farDisInt/1000) +" กิโลเมตร", Toast.LENGTH_LONG).show();
                                        Log.d("TAG_LOCATION", String.valueOf(distanceInMeters));
                                    } else {
                                        InsertQueue newQueue = new InsertQueue();
                                        newQueue.updateQueue("B", obj.getCitizenId());
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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
        TextView role = (TextView)header.findViewById(R.id.userRole);
        TextView id = (TextView) header.findViewById(R.id.id);

        if (obj.getRole().equals("nurse")){
            avatar.setImageResource(R.drawable.ic_021_nurse);
            role.setText(R.string.nurse);
        } else {
            avatar.setImageResource(R.drawable.ic_009_sick);
            role.setText(R.string.in_patient);
        }
        id.setText("เลขประจำตัว: "+obj.getCitizenId());
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            if (doubleBackToExitPressedOnce) {
//                this.doubleBackToExitPressedOnce = false;
//                Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
//            } else {
//                finish();
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.miniinapp, menu);
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
            if (obj.getRole().equals("nurse")) {
                Intent it = new Intent(InAppActivity.this, WalkInActivity.class);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(), "คุณต้องมีสิทธิ์เข้าถึง", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_phone_inapp) {
            Intent it = new Intent(InAppActivity.this, InAppActivity.class);
            startActivity(it);
        } else if (id == R.id.nav_manage) {
            if (obj.getRole().equals("nurse")) {
                Intent it = new Intent(InAppActivity.this, CallQueue.class);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(), "คุณต้องมีสิทธิ์เข้าถึง", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_setting) {
            if (obj.getRole().equals("nurse")) {
                Intent it = new Intent(InAppActivity.this, Setting.class);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(), "คุณต้องมีสิทธิ์เข้าถึง", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_logout) {
            Intent it = new Intent(InAppActivity.this, LogoutActivity.class);
            startActivity(it);
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation == null) {
            startLocationUpdates();
        }
        if (mLocation != null) {
            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
//        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + location.getLatitude() + "\nLong: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        loc2Latitude = location.getLatitude();
        loc2Longitude = location.getLongitude();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");

    }

    public void checkMe() {
        DatabaseReference useQ = FirebaseDatabase.getInstance().getReference();
        useQ.child("useQueue").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Queue qqq = dataSnapshot.getValue(Queue.class);
                Log.d("TAG", qqq.getId());
                if (qqq.getId().equals(obj.getCitizenId())) {
                    if (qqq.getType().equals("B")) {
                        getNotification();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getNotification() {

        long[] vibrate = {0, 100, 200, 300};

//        Intent intent = new Intent(Intent.ACTION_VIEW,
//                Uri.parse("https://google.com"));
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent intent = new Intent(this, ReceiverActivity.class);
        PendingIntent activity = PendingIntent.getActivity(this,0,intent,0);


        Notification notification =
                new NotificationCompat.Builder(this) // this is context
                        .setTicker("QueueQ")
                        .setSmallIcon(R.drawable.ic_hospital_2)
                        .setContentTitle("ถึงเวลาคิว B"+queueB+" แล้วค่ะ")
                        .setContentText("ขอให้ท่านเดินทางมาที่โรงพยาบาลภายในเวลา 15 นาทีเพื่อทำการซักประวัติและคัดกรองผู้ป่วยเบื้องต้นด้วยค่ะ")
                        .setAutoCancel(false)
                        .setContentIntent(activity)
                        .build();

        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.vibrate = vibrate;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1000, notification);
    }


}