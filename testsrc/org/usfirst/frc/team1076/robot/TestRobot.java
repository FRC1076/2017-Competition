package org.usfirst.frc.team1076.robot;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestRobot {

    @Test
    public void test() {
        assertTrue(true);
    }
    
    @Test(expected = AssertionError.class)
    public void testFail() {
        assertTrue(false);
    }

}
