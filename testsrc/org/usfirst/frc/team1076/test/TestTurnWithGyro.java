package org.usfirst.frc.team1076.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.strongback.mock.Mock;
import org.strongback.mock.MockGyroscope;
import org.strongback.mock.MockMotor;
import org.usfirst.frc.team1076.robot.commands.TurnWithGyro;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.test.mock.MockGamepad;

public class TestTurnWithGyro {

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
        TurnWithGyro turn = new TurnWithGyro(gyro, drivetrain, 0.0, 0.0);
        turn.initialize();
        assertEquals("The command should zero the gyro when initialized",
                0.0, gyro.getAngle(), EPSILON);
    }

    @Test
    public void testDoesNotFinishEarly() {
        TurnWithGyro turn = new TurnWithGyro(gyro, drivetrain, 0.0, 60.0);
        assertFalse("The command should not finish before rotating to the correct angle",
                turn.isFinished());
    }
    
    @Test
    public void testDoesFinish() {
        TurnWithGyro turn = new TurnWithGyro(gyro, drivetrain, 0.0, 60.0);
        gyro.setAngle(60.0);
        assertTrue("The command should finish after rotating to the correct angle",
                turn.isFinished());
        
        turn = new TurnWithGyro(gyro, drivetrain, 0.0, -60.0);
        gyro.setAngle(-70.0);
        assertTrue("The command should finish after rotating to the correct angle",
                turn.isFinished());
    }
    
    @Test
    public void testTurnLeft() {
        double speed = Math.random();
        TurnWithGyro turn = new TurnWithGyro(gyro, drivetrain, speed, -60.0);
        turn.execute();
        assertEquals("The left motor should be negative when turning left",
                -speed, left.getSpeed(), EPSILON);
        assertEquals("The right motor should be positive when turning left",
                speed, right.getSpeed(), EPSILON);
    }

    @Test
    public void testTurnRight() {
        double speed = Math.random();
        TurnWithGyro turn = new TurnWithGyro(gyro, drivetrain, speed, 60.0);
        turn.execute();
        assertEquals("The left motor should be positive when turning right",
                speed, left.getSpeed(), EPSILON);
        assertEquals("The right motor should be negative when turning right",
                -speed, right.getSpeed(), EPSILON);        
    }
    
    @Test
    public void testEndStopsMotors() {
        TurnWithGyro turn = new TurnWithGyro(gyro, drivetrain, 1.0, 0.0);
        turn.execute();
        turn.end();
        assertEquals("The left motor should stop when end is called.", 0.0, left.getSpeed(), EPSILON);
        assertEquals("The right motor should stop when end is called.", 0.0, right.getSpeed(), EPSILON);
    }
}
