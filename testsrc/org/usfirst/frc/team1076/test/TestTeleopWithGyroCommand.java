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

    private static final int NUM_TEST_ITERATIONS = 1000;

    MockMotor left = Mock.stoppedMotor();
    MockMotor right = Mock.stoppedMotor();
    MockGamepad gamepad = new MockGamepad();
    Drivetrain drivetrain = new Drivetrain(left, right);
    MockGyroscope gyro = Mock.gyroscope();
    TeleopWithGyroCommand teleop = new TeleopWithGyroCommand(gyro, drivetrain, gamepad);
    
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
        teleop.initialize();
        assertEquals("The command should zero the gyro when initialized", 
                0.0, gyro.getAngle(), EPSILON); 
    }
    
    @Test
    public void testShouldForwardAssist() {
        for (double i = 0.0; i < 1.0; i += 0.01) {
            gamepad.lx = i;
            if (gamepad.lx < TeleopWithGyroCommand.FORWARD_ASSIST_MAX_TURN_SPEED) {
                assertTrue("Teleop should assist with forward movement when turning with " + gamepad.lx,
                         teleop.shouldForwardAssist());
            } else {
                assertFalse("Teleop should not assist with forward movement when turning with " + gamepad.lx,
                        teleop.shouldForwardAssist());
            }
        }
    }
    
    @Test
    public void testNoForwardAssist() {
        for (double i = TeleopWithGyroCommand.FORWARD_ASSIST_MAX_TURN_SPEED; i < 1.0; i += 0.01) {
            gamepad.ry = 2*Math.random() - 1;
            gamepad.lx = i;
            teleop.execute();
            assertEquals("Teleop should not assist forward movement (left motor)",
                    gamepad.ry + gamepad.lx, left.getSpeed(), EPSILON);
            assertEquals("Teleop should not assist forward movement (right motor)",
                    gamepad.ry - gamepad.lx, right.getSpeed(), EPSILON);
        }
    }
    
    interface ForwardAssistAssertion {
        public void doAssertion(MockGamepad gamepad);
    }
    
    @Test
    public void testForwardAssistDoesNotReduceLeftMotorWhenDriftingLeft() {
        testForwardAssist((MockGamepad gamepad)->assertEquals("The left motor should not reduce when drifting left", 
                gamepad.ry + gamepad.lx, left.getSpeed(), EPSILON), -10);
    }
    
    @Test
    public void testForwardAssistReducesRightMotorWhenDriftingLeft() {
        testForwardAssist((MockGamepad gamepad)->assertTrue("The right motor should reduce when drifting left", 
                Math.abs(gamepad.ry - gamepad.lx) > Math.abs(right.getSpeed())), -10);
    }
    
    @Test
    public void testForwardAssistReducesLeftMotorWhenDriftingRight() {
        testForwardAssist((MockGamepad gamepad)->assertTrue("The left motor should reduce when drifting left", 
                Math.abs(gamepad.ry + gamepad.lx) > Math.abs(left.getSpeed())), 10);
    }
    
    @Test
    public void testForwardAssistDoesNotReduceRightMotorWhenDriftingRight() {
        testForwardAssist((MockGamepad gamepad)->assertEquals("The right motor should not reduce when drifting left", 
                gamepad.ry - gamepad.lx, right.getSpeed(), EPSILON), 10);
    }
    
    public void testForwardAssist(ForwardAssistAssertion assertion, double angle) {
        for (double i = 0; i < TeleopWithGyroCommand.FORWARD_ASSIST_MAX_TURN_SPEED; i += 0.01) {
            gyro.setAngle(angle);
            gamepad.ry = randomSpeed();
            gamepad.lx = i;
            teleop.execute();
            assertion.doAssertion(gamepad);
        }
    }
    
    // Get a random speed from -1 to -0.1 or 0.1 to 1 
    double randomSpeed() { 
        return Math.copySign(Math.random() * 0.9 + 0.1, Math.random() - 0.5); 
    } 
}
