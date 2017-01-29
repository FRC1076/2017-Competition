package org.usfirst.frc.team1076.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.strongback.mock.Mock;
import org.strongback.mock.MockGyroscope;
import org.strongback.mock.MockMotor;
import org.usfirst.frc.team1076.robot.commands.TeleopWithGyroCommand;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.test.mock.MockGamepad;

public class TestTeleopWithGyroCommand {

    private static final double EPSILON = 1E-10;

    MockMotor left = Mock.stoppedMotor();
    MockMotor right = Mock.stoppedMotor();
    MockGamepad gamepad = new MockGamepad();
    Drivetrain drivetrain = new Drivetrain(left, right);
    MockGyroscope gyro = Mock.gyroscope();
    
    
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
        TeleopWithGyroCommand teleop = new TeleopWithGyroCommand(gyro, drivetrain, gamepad); 
        teleop.initialize();
        assertEquals("The command should zero the gyro when initialized", 
                0.0, gyro.getAngle(), EPSILON); 
    }
}
