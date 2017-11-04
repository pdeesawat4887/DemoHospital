package com.example.pacharapoldeesawat.demohospital;

/**
 * Created by pacharapoldeesawat on 10/28/2017 AD.
 */

public class ResetValue {

    private static String newValue;

    public static String getNewString(int old){

        if (old < 10){
            newValue = "00" + old;
        } else if (old < 100){
            newValue = "0" + old;
        } else {
            String.valueOf(old);
        }
        return newValue;
    }
}
