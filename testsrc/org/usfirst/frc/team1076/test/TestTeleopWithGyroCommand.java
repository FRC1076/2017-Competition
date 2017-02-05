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
        for (double i = -1.0; i < 1.0; i += 0.01) {
            gamepad.lx = i;
            if (Math.abs(gamepad.lx) < Math.abs(TeleopWithGyroCommand.FORWARD_ASSIST_MAX_TURN_SPEED)) {
                assertTrue("Teleop should assist with forward movement when turning with " + gamepad.lx,
                         teleop.shouldForwardAssist());
            } else {
                assertFalse("Teleop should not assist with forward movement when turning with " + gamepad.lx,
                        teleop.shouldForwardAssist());
            }
        }
    }
    
    @Test
    public void testNoForwardAssistGyroZero() {
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
    
    @Test
    public void testNoForwardAssistGyroNonZero() {
        for (double i = TeleopWithGyroCommand.FORWARD_ASSIST_MAX_TURN_SPEED; i < 1.0; i += 0.01) {
            gamepad.ry = 2*Math.random() - 1;
            gamepad.lx = i;
            // Get a random angle between 10 and 70 or -70 and -10
            gyro.setAngle(Math.copySign(Math.random()*60+10, Math.random()-0.5));
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
    
    /* Explanation of forward tests
     * Get up and walk very slowly. Now, start to turn left. You'll notice that your RIGHT foot
     * travels a longer distance and that your LEFT foot does not. In fact, you can turn on the spot
     * purely by swinging your right foot while keeping your left foot still. The opposite is true for
     * turning right.
     * 
     * This suggests that preventing this turn is accomplished by either speeding up the slow foot 
     * (left foot for left turn, right foot for right turn), or by slowing the fast foot (right foot for 
     * left turn, left foot for right turn). We choose to slow the fast foot as this is always achievable 
     * (if you turn at your maximum foot speed, you can't speed up any faster!). We also decide NOT to change
     * the slow foot, as this reduces speed needlessly.
     * 
     * TLDR: Slow the motor opposite to your drift direction
     */
    
    @Test
    public void testForwardAssistDoesNotReduceLeftMotorWhenDriftingLeft() {
        testForwardAssist((MockGamepad gamepad)->assertEquals("The left motor should not reduce when drifting left", 
                gamepad.ry + gamepad.lx, left.getSpeed(), EPSILON), -10);
    }
    
    @Test
    public void testForwardAssistReducesRightMotorWhenDriftingLeft() {
        testForwardAssist((MockGamepad gamepad)->assertTrue("The right motor should reduce when drifting left and driving forwards", 
                Math.abs(gamepad.ry - gamepad.lx) > Math.abs(right.getSpeed())), -10);
    }
    
    @Test
    public void testForwardAssistReducesLeftMotorWhenDriftingRight() {
        testForwardAssist((MockGamepad gamepad)->assertTrue("The left motor should reduce when drifting right and driving forwards", 
                Math.abs(gamepad.ry + gamepad.lx) > Math.abs(left.getSpeed())), 10);
    }
    
    @Test
    public void testForwardAssistDoesNotReduceRightMotorWhenDriftingRight() {
        testForwardAssist((MockGamepad gamepad)->assertEquals("The right motor should not reduce when drifting left and driving forwards", 
                gamepad.ry - gamepad.lx, right.getSpeed(), EPSILON), 10);
    }
    
    public void testForwardAssist(ForwardAssistAssertion assertion, double angle) {
        // Sets the initial "edge" for the RightY axis as positive
        gamepad.ry = 1;
        teleop.execute();
        for (double i = 0; i < TeleopWithGyroCommand.FORWARD_ASSIST_MAX_TURN_SPEED; i += 0.01) {
            gyro.setAngle(angle);
            gamepad.ry = randomForwardSpeed();
            gamepad.lx = i;
            teleop.execute();
            assertion.doAssertion(gamepad);
        }
    }
    
    /* Explanation of backward tests
     * Walk backwards slowly and turn to your left. This time, you'll notice that your
     * left foot is the fast foot. This is because turning left while facing backwards means you
     * drift RIGHT. Your nose should point to the right of wherever you were pointed at before.
     * In other words, turning left backwards actually produces a drift towards the right!
     * 
     * (Another way to thing about this is that when you turn 180 degrees, everything that was
     * on your left is now on your right and vice versa)
     * 
     * Thus, to prevent drift, you must reduce the speed of the motor on the SAME side as the drift
     */
    @Test
    public void testBackwardAssistReducesLeftMotorWhenDriftingLeft() {
        testBackwardAssist((MockGamepad gamepad)->assertTrue("The left motor should reduce when drifting left and driving backwards", 
                Math.abs(gamepad.ry + gamepad.lx) > Math.abs(left.getSpeed())), -10);
    }
    
    @Test
    public void testBackwardAssistDoesNotReduceRightMotorWhenDriftingLeft() {
        testBackwardAssist((MockGamepad gamepad)->assertEquals("The right motor should not reduce when drifting left and driving backwards", 
                gamepad.ry - gamepad.lx, right.getSpeed(), EPSILON), -10);
    }
    
    @Test
    public void testBackwardAssistDoesNotReduceLeftMotorWhenDriftingRight() {
        testBackwardAssist((MockGamepad gamepad)->assertEquals("The left motor should not reduce when drifting right and driving backwards", 
                gamepad.ry + gamepad.lx, left.getSpeed(), EPSILON), 10);
    }
    
    @Test
    public void testBackwardAssistReducesRightMotorWhenDriftingRight() {
        testBackwardAssist((MockGamepad gamepad)->assertTrue("The right motor should reduce when drifting right and driving backwards", 
                Math.abs(gamepad.ry - gamepad.lx) > Math.abs(right.getSpeed())), 10);
    }
    // Yes, this is code duplication, but it's probably a livable amount of duplication.
    public void testBackwardAssist(ForwardAssistAssertion assertion, double angle) {
        // Sets the initial "edge" for the RightY axis as negative
        gamepad.ry = -1;
        teleop.execute();
        for (double i = 0; i < TeleopWithGyroCommand.FORWARD_ASSIST_MAX_TURN_SPEED; i += 0.01) {
            gyro.setAngle(angle);
            gamepad.ry = randomBackwardSpeed();
            gamepad.lx = i;
            teleop.execute();
            assertion.doAssertion(gamepad);
        }
    }
    
    // Get a random speed from 0.1 to 1 
    double randomForwardSpeed() { 
        return Math.random() * 0.9 + 0.1; 
    }
    // Get a random speed from -1 to -0.1
    double randomBackwardSpeed() {
        return -(Math.random() * 0.9 + 0.1);
    }
}
