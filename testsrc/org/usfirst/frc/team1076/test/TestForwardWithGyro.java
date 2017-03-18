package org.usfirst.frc.team1076.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.strongback.command.CommandTester;
import org.strongback.mock.Mock;
import org.strongback.mock.MockGyroscope;
import org.strongback.mock.MockMotor;
import org.usfirst.frc.team1076.robot.MockGamepad;
import org.usfirst.frc.team1076.robot.commands.ForwardWithGyro;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.GyroPIDCorrector;

public class TestForwardWithGyro {

    static final int NUM_TEST_ITERS = 1024;
    private static final double EPSILON = 1E-10;
    MockMotor left = Mock.stoppedMotor();
    MockMotor right = Mock.stoppedMotor();
    MockGamepad gamepad = new MockGamepad();
    MockGyroscope gyro = Mock.gyroscope();
    Drivetrain drivetrain = new Drivetrain(left, right);
    GyroPIDCorrector corrector = new GyroPIDCorrector(gyro);

    
    
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
        ForwardWithGyro forward = new ForwardWithGyro(drivetrain, corrector, 0, 0);
        forward.initialize();
        assertEquals("The gyro's angle should be zeroed prior to executing the command",
                0.0, gyro.getAngle(), EPSILON);
    }

    @Test
    public void testMotorsBothPositive() {
        for (int i = -90; i < 90; i++) {
            gyro.setAngle(i);
            ForwardWithGyro forward = new ForwardWithGyro(drivetrain, corrector, 1.0, 0);
            forward.execute();
            assertTrue("The left motor should be positive (actual: " + left.getSpeed() + " angle: " + gyro.getAngle(),
                    0 < left.getSpeed());
            assertTrue("The right motor should be positive (actual: " + right.getSpeed() + " angle: " + gyro.getAngle(),
                    0 < right.getSpeed());
        }
    }

    @Test
    public void testMotorsBothNegative() {
        for (int i = -90; i < 90; i++) {
            gyro.setAngle(i);
            ForwardWithGyro forward = new ForwardWithGyro(drivetrain, corrector, -1.0, 0);
            forward.execute();
            assertTrue("The left motor should be negative (actual: " + left.getSpeed() + " angle: " + gyro.getAngle(),
                    0 > left.getSpeed());
            assertTrue("The right motor should be negative (actual: " + right.getSpeed() + " angle: " + gyro.getAngle(),
                    0 > right.getSpeed());
        }
    }
    
    @Test
    public void testRunsForCorrectTime() {
        // Should run for a second
        ForwardWithGyro forward = new ForwardWithGyro(drivetrain, corrector, 0, 1.0);
        CommandTester tester = new CommandTester(forward);
        // step steps the command forward by the number of *milliseconds* passed
        assertFalse("The command should not finish executing before the specified time", tester.step(0));
        assertTrue("The command should have finished executing by now", tester.step(1000));
    }
    
    @Test
    public void testForwardNoAngle() {
        gyro.setAngle(0);
        for (int i = 0; i < NUM_TEST_ITERS; i++) {
            double speed = Math.random(); 
            ForwardWithGyro forward = new ForwardWithGyro(drivetrain, corrector, speed, 0);
            forward.execute();
            assertEquals("The left motor should drive without being reduced when there is no angle rotation",
                speed, left.getSpeed(), EPSILON);
            assertEquals("The right motor should drive without being reduced when there is no angle rotation",
                speed, right.getSpeed(), EPSILON);
        }
    }
    
    @Test
    public void testEndStopsMotors() {
        ForwardWithGyro forward = new ForwardWithGyro(drivetrain, corrector, 1.0, 0.0);
        forward.execute();
        forward.end();
        assertEquals("The left motor should stop when end is called.", 0.0, left.getSpeed(), EPSILON);
        assertEquals("The right motor should stop when end is called.", 0.0, right.getSpeed(), EPSILON);
    }
}
