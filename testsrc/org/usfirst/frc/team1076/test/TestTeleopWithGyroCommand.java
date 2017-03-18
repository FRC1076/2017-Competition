package org.usfirst.frc.team1076.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.strongback.mock.Mock;
import org.strongback.mock.MockGyroscope;
import org.strongback.mock.MockMotor;
import org.usfirst.frc.team1076.robot.MockGamepad;
import org.usfirst.frc.team1076.robot.commands.TeleopWithGyroCommand;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.GyroPIDCorrector;
import org.usfirst.frc.team1076.robot.subsystems.Winch;

public class TestTeleopWithGyroCommand {

    private static final double EPSILON = 1E-10;

    MockMotor left = Mock.stoppedMotor();
    MockMotor right = Mock.stoppedMotor();
    Winch winch = new Winch(Mock.stoppedMotor()); // Mock winch
    MockGamepad driver = new MockGamepad();
    MockGamepad operator = new MockGamepad();
    MockGyroscope gyro = Mock.gyroscope();
    Drivetrain drivetrain = new Drivetrain(left, right);
    GyroPIDCorrector corrector = new GyroPIDCorrector(gyro);
    TeleopWithGyroCommand teleop = new TeleopWithGyroCommand(drivetrain, corrector, driver, operator, winch);
    
    @Before
    public void reset() {
        left.setSpeed(0);
        right.setSpeed(0);
        driver.reset();
        gyro.zero();
        drivetrain = new Drivetrain(left, right);
    }
    
    @Test 
    public void testInitializeZerosGyro() { 
        gyro.setAngle(100); 
        teleop.initialize();
        assertEquals("The command should zero the gyro when initialized", 
                0.0, gyro.getAngle(), EPSILON); 
    }
}
