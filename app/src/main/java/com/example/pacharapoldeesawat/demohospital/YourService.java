package com.example.pacharapoldeesawat.demohospital;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.pacharapoldeesawat.demohospital.Model.Queue;
import com.example.pacharapoldeesawat.demohospital.Model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

/**
 * Created by pacharapoldeesawat on 11/4/2017 AD.
 */

public class YourService extends Service {

    private User obj;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences mPrefs2 = getSharedPreferences("label", 0);
        Gson gson3 = new Gson();
        String json3 = mPrefs2.getString("MyObjectUser", "");
        obj = gson3.fromJson(json3, User.class);

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
        return super.onStartCommand(intent, flags, startId);
    }

    public void getNotification() {

        long[] vibrate = {0, 100, 200, 300};

        Notification notification =
                new NotificationCompat.Builder(this) // this is context
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("DevAhoy News")
                        .setContentText("สวัสดีครับ ยินดีต้อนรับเข้าสู่บทความ Android Notification :)")
                        .setAutoCancel(true)
                        .build();

        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        notification.vibrate = vibrate;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1000, notification);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
