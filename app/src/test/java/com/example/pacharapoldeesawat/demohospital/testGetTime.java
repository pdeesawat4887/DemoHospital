package com.example.pacharapoldeesawat.demohospital;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class testGetTime {

    @Test
    public void testGetTimeHourOne() {
        GetTime test = new GetTime();
        try {
            test.whatTimeIsIt();
            assertEquals("23", test.getHour());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testGetTimeMinOne() {
        GetTime test = new GetTime();
        try {
            test.whatTimeIsIt();
            assertEquals("14", test.getMin());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
