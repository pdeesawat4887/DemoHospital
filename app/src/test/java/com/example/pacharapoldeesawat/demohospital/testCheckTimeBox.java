package com.example.pacharapoldeesawat.demohospital;

import org.junit.Test;

import static org.junit.Assert.*;

public class testCheckTimeBox {
    @Test
    public void checkTimeBoxTestOne() {
        assertEquals(4, CheckTimeBox.checkTimeBox(22, 32));
    }

    @Test
    public void checkTimeBoxTestTwo() {
        assertEquals(5, CheckTimeBox.checkTimeBox(23, 2));
    }

    @Test
    public void checkTimeBoxTestThree() {
        assertEquals(0, CheckTimeBox.checkTimeBox(19, 0));
    }

    @Test
    public void checkTimeBoxTestFive() {
        assertEquals(10, CheckTimeBox.checkTimeBox(1, 30));
    }

    @Test
    public void checkTimeBoxTestSix() {
        assertEquals(8, CheckTimeBox.checkTimeBox(0, 59));
    }

    @Test
    public void checkTimeBoxTestSeven() {
        assertEquals(0, CheckTimeBox.checkTimeBox(24, 30));
    }
}
