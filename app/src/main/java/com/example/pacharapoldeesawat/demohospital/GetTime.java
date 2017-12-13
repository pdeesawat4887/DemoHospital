package com.example.pacharapoldeesawat.demohospital;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

public class GetTime {

    private static String strTime;
    private static final String TIME_SERVER = "time-a.nist.gov";
    private static String hour;
    private static String min;
    private static String sec;
    private static Date realDate;

    public static String whatTimeIsIt() throws IOException {
        NTPUDPClient timeClient = new NTPUDPClient();
        InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
        TimeInfo timeInfo = timeClient.getTime(inetAddress);
        long systemTime = System.currentTimeMillis();
        timeInfo.computeDetails();
        realDate = new Date(systemTime + timeInfo.getOffset());
        hour = String.valueOf(realDate.getHours());
        min = String.valueOf(realDate.getMinutes());
        sec = String.valueOf(realDate.getSeconds());

        if (realDate.getSeconds() < 10) {
            sec = "0" + realDate.getSeconds();
        }
        if (realDate.getMinutes() < 10) {
            min = "0" + realDate.getMinutes();
        }
        if (realDate.getHours() < 10) {
            hour = "0" + realDate.getHours();
        }

        strTime = hour + min + sec;
        return strTime;
    }

    public String getStrTime() {
        return strTime;
    }

    public String getHour() {
        return hour;
    }

    public String getMin() {
        return min;
    }

}
