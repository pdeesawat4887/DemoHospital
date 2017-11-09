package com.example.pacharapoldeesawat.demohospital;

import android.util.Log;

/**
 * Created by pacharapoldeesawat on 11/1/2017 AD.
 */

public class CheckTimeBox {

    private static int timebox;

    public static int checkTimeBox(int hour, int min){

        switch (hour) {
            case 16: timebox = (min < 30) ? 1 : 2;
                break;
            case 17: timebox = (min < 30) ? 3 : 4;
                break;
            case 18: timebox = (min < 30) ? 5 : 6;
                break;
            case 19: timebox = (min < 30) ? 7 : 8;
                break;
            case 20: timebox = (min < 30) ? 9 : 10;
                break;
        }
        return timebox;

    }
}
