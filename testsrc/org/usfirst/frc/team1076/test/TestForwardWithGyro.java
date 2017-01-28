package org.usfirst.frc.team1076.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.strongback.command.CommandTester;
import org.strongback.mock.Mock;
import org.strongback.mock.MockGyroscope;
import org.strongback.mock.MockMotor;
import org.usfirst.frc.team1076.robot.commands.ForwardWithGyro;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.test.mock.MockGamepad;

public class TestForwardWithGyro {

    private static final double EPSILON = 1E-10;
    MockMotor left = Mock.stoppedMotor();
    MockMotor right = Mock.stoppedMotor();
    MockGamepad gamepad = new MockGamepad();
    MockGyroscope gyro = Mock.gyroscope();
    Drivetrain drivetrain = new Drivetrain(left, right);
    
    
    @Before
    public void reset() {
        left.setSpeed(0);
        right.setSpeed(0);
        gamepad.reset();
        gyro.zero();
    }
    
    @Test
    public void testInitializeZerosGyro() {
        gyro.setAngle(100);
        ForwardWithGyro forward = new ForwardWithGyro(gyro, drivetrain, 0, 0);
        forward.initialize();
        assertEquals("The gyro's angle should be zeroed prior to executing the command",
                0.0, gyro.getAngle(), EPSILON);
    }

    @Test
    public void testRunsForCorrectTime() {
        // Should run for a second
        ForwardWithGyro forward = new ForwardWithGyro(gyro, drivetrain, 0, 1.0);
        CommandTester tester = new CommandTester(forward);
        // step steps the command forward by the number of *milliseconds* passed
        assertFalse("The command should not finish executing before the specified time", tester.step(0));
        assertTrue("The command should have finished executing by now", tester.step(1000));
    }
    
    @Test
    public void testForwardNoAngle() {
        gyro.setAngle(0);
        for (int i = 0; i < 5; i++) {
            double speed = Math.random(); 
            ForwardWithGyro forward = new ForwardWithGyro(gyro, drivetrain, speed, 0);
            forward.execute();
            assertEquals("The left motor should drive without being reduced when there is no angle rotation",
                speed, left.getSpeed(), EPSILON);
            assertEquals("The right motor should drive without being reduced when there is no angle rotation",
                speed, right.getSpeed(), EPSILON);
        }
    }
    
    @Test
    public void testDriftingTowardsRight() {
        gyro.setAngle(10.0);
        for (int i = 0; i < 5; i++) {
            double speed = Math.random();
            ForwardWithGyro forward = new ForwardWithGyro(gyro, drivetrain, speed, 0);
            forward.execute();
            assertTrue("The left motor should be reduced when there is a rightward drift",
                speed > left.getSpeed());
            assertEquals("The right motor should drive without being reduced when there is a rightward drift",
                speed, right.getSpeed(), EPSILON);
        }
    }
    
    @Test
    public void testDriftingTowardsLeft() {
        gyro.setAngle(-10.0);
        for (int i = 0; i < 5; i++) {
            double speed = Math.random();
            ForwardWithGyro forward = new ForwardWithGyro(gyro, drivetrain, speed, 0);
            forward.execute();
            assertEquals("The left motor should drive without being reduced when there is a leftward drift",
                speed, left.getSpeed(), EPSILON);
            assertTrue("The right motor should be reduced when there is a leftward drift",
                speed > right.getSpeed());
        }
    }
}
