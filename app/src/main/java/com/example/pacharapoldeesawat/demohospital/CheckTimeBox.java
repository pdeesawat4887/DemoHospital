package com.example.pacharapoldeesawat.demohospital;


import android.util.Log;




public class CheckTimeBox {


    private static int timebox;


    public static int checkTimeBox(int hour, int min){



        switch (hour) {

            case 21: timebox = (min < 30) ? 1 : 2;

                break;

            case 22: timebox = (min < 30) ? 3 : 4;

                break;

            case 23: timebox = (min < 30) ? 5 : 6;

                break;

            case 0: timebox = (min < 30) ? 7 : 8;

                break;

            case 1: timebox = (min < 30) ? 9 : 10;

                break;

            default:   timebox = 0;

                break;

        }

        return timebox;


    }

}
