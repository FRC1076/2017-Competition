package org.usfirst.frc.team1076.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.strongback.components.Motor;
import org.strongback.mock.Mock;
import org.usfirst.frc.team1076.robot.subsystems.Winch;

public class TestWinch {
    public static final double EPSILON = 1E-10;
    Motor motor = Mock.stoppedMotor();
    Winch winch = new Winch(motor);
    
    @Before
    public void reset() {
        motor.setSpeed(0);
    }

//    @Test
//    public void testSetSpeed() {
//        winch.extend(1.0);
//        assertEquals("Winch should be set to the right speed", 1.0, motor.getSpeed(), EPSILON);
//        winch.extend(-1.0);
//        assertEquals("Winch should be set to the right speed", -1.0, motor.getSpeed(), EPSILON);
//    }

    
    @Test
    public void testExtend() {
        winch.extend(1.0);
        assertEquals("Winch should extend", 1.0, motor.getSpeed(), EPSILON);
    }

//    @Test
//    public void testRetract() {
//        winch.retract(1.0);
//        assertEquals("Winch should retract", -1.0, motor.getSpeed(), EPSILON);
//    }

}
