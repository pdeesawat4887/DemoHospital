package com.example.pacharapoldeesawat.demohospital;

import com.example.pacharapoldeesawat.demohospital.Model.Queue;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;


public class InsertQueue {

    private static long count;
    private static long countPerBox;
    private static int timeBox;
    private static DatabaseReference root;
    private static GetTime usT;

    public static void updateQueue(final String type, final String id) throws IOException {

        root = FirebaseDatabase.getInstance().getReference();
        usT = new GetTime();
        GetTime.whatTimeIsIt();
        timeBox = CheckTimeBox.checkTimeBox(Integer.parseInt(usT.getHour()), Integer.parseInt(usT.getMin()));

        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                count = (long) dataSnapshot.child(type).getValue();
                count += 1;
                countPerBox = (long) dataSnapshot.child(String.valueOf(timeBox)).child(type).getValue();
                countPerBox += 1;

                Queue qqq = new Queue(id, Integer.parseInt(usT.getStrTime()), type, (int) count);

                root.child("demoQueue").push().setValue(qqq);
                root.child("useQueue").push().setValue(qqq);

                root.child(String.valueOf(timeBox)).child(type).setValue(countPerBox);
                root.child(type).setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public int getTimeB(){
        return timeBox;
    }

}
