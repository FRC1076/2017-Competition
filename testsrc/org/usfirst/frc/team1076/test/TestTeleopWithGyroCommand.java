package org.usfirst.frc.team1076.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.strongback.mock.Mock;
import org.strongback.mock.MockGyroscope;
import org.strongback.mock.MockMotor;
import org.usfirst.frc.team1076.robot.commands.TeleopWithGyroCommand;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithGyro;
import org.usfirst.frc.team1076.test.mock.MockGamepad;

public class TestTeleopWithGyroCommand {

    private static final double EPSILON = 1E-10;

    MockMotor left = Mock.stoppedMotor();
    MockMotor right = Mock.stoppedMotor();
    MockGamepad gamepad = new MockGamepad();
    MockGyroscope gyro = Mock.gyroscope();
    DrivetrainWithGyro drivetrain = new DrivetrainWithGyro(left, right, gyro);
    TeleopWithGyroCommand teleop = new TeleopWithGyroCommand(drivetrain, gamepad);
    
    @Before
    public void reset() {
        left.setSpeed(0);
        right.setSpeed(0);
        gamepad.reset();
        gyro.zero();
        drivetrain = new DrivetrainWithGyro(left, right, gyro);
    }
    
    @Test 
    public void testInitializeZerosGyro() { 
        gyro.setAngle(100); 
        teleop.initialize();
        assertEquals("The command should zero the gyro when initialized", 
                0.0, gyro.getAngle(), EPSILON); 
    }
}
