package org.usfirst.frc.team1076.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.strongback.mock.Mock;
import org.strongback.mock.MockGyroscope;
import org.strongback.mock.MockMotor;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithGyro;

public class TestDrivetrainWithGyro {

    private static final double EPSILON = 1E-10;
    
    MockMotor left = Mock.stoppedMotor();
    MockMotor right = Mock.stoppedMotor();
    MockGyroscope gyro = Mock.gyroscope();
    DrivetrainWithGyro drivetrain = new DrivetrainWithGyro(left, right, gyro);
    
    @Before
    public void reset() {
        left.setSpeed(0);
        right.setSpeed(0);
        gyro.zero();
        drivetrain = new DrivetrainWithGyro(left, right, gyro);
    }
    
    @Test
    public void testShouldForwardAssist() {
        for (double i = -1.0; i < 1.0; i += 0.01) {
            double rotate = i;
            if (Math.abs(rotate) < Math.abs(DrivetrainWithGyro.FORWARD_ASSIST_MAX_TURN_SPEED)) {
                assertTrue("Teleop should assist with forward movement when turning with " + rotate,
                         drivetrain.shouldForwardAssist(rotate));
            } else {
                assertFalse("Teleop should not assist with forward movement when turning with " + rotate,
                        drivetrain.shouldForwardAssist(rotate));
            }
        }
    }
    
    @Test
    public void testNoForwardAssistGyroZero() {
        for (double i = DrivetrainWithGyro.FORWARD_ASSIST_MAX_TURN_SPEED; i < 1.0; i += 0.01) {
            double forward = 2*Math.random() - 1;
            double rotate = i;
            drivetrain.arcade(forward, rotate);
            assertEquals("Teleop should not assist forward movement (left motor)",
                    forward + rotate, left.getSpeed(), EPSILON);
            assertEquals("Teleop should not assist forward movement (right motor)",
                    forward - rotate, right.getSpeed(), EPSILON);
        }
    }
    
    @Test
    public void testNoForwardAssistGyroNonZero() {
        for (double i = DrivetrainWithGyro.FORWARD_ASSIST_MAX_TURN_SPEED; i < 1.0; i += 0.01) {
            double forward = 2*Math.random() - 1;
            double rotate = i;
            // Get a random angle between 10 and 70 or -70 and -10
            gyro.setAngle(Math.copySign(Math.random()*60+10, Math.random()-0.5));
            drivetrain.arcade(forward, rotate);
            assertEquals("Teleop should not assist forward movement (left motor)",
                    forward + rotate, left.getSpeed(), EPSILON);
            assertEquals("Teleop should not assist forward movement (right motor)",
                    forward - rotate, right.getSpeed(), EPSILON);
        }        
    }
    
    interface ForwardAssistAssertion {
        public void doAssertion(double forward, double rotate);
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
    
    // TODO: Fix these tests, they do not work currently.
    @Test @Ignore
    public void testForwardAssistDoesNotReduceLeftMotorWhenDriftingLeft() {
        testForwardAssist((double forward, double rotate)->assertEquals("The left motor should not reduce when drifting left", 
                forward + rotate, left.getSpeed(), EPSILON), -10);
    }
    
    @Test @Ignore
    public void testForwardAssistReducesRightMotorWhenDriftingLeft() {
        testForwardAssist((double forward, double rotate)->assertTrue("The right motor should reduce when drifting left and driving forwards", 
                Math.abs(forward - rotate) > Math.abs(right.getSpeed())), -10);
    }
    
    @Test @Ignore
    public void testForwardAssistReducesLeftMotorWhenDriftingRight() {
        testForwardAssist((double forward, double rotate)->assertTrue("The left motor should reduce when drifting right and driving forwards", 
                Math.abs(forward + rotate) > Math.abs(left.getSpeed())), 10);
    }
    
    @Test @Ignore
    public void testForwardAssistDoesNotReduceRightMotorWhenDriftingRight() {
        testForwardAssist((double forward, double rotate)->assertEquals("The right motor should not reduce when drifting left and driving forwards", 
                forward - rotate, right.getSpeed(), EPSILON), 10);
    }
    
    public void testForwardAssist(ForwardAssistAssertion assertion, double angle) {
        // Sets the initial "edge" for the RightY axis as positive
        drivetrain.arcade(1, 0);
        for (double i = 0; i < DrivetrainWithGyro.FORWARD_ASSIST_MAX_TURN_SPEED; i += 0.01) {
            gyro.setAngle(angle);
            double forward = randomForwardSpeed();
            double rotate = i;
            drivetrain.arcade(forward, rotate);
            assertion.doAssertion(forward, rotate);
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
    @Test @Ignore
    public void testBackwardAssistReducesLeftMotorWhenDriftingLeft() {
        testBackwardAssist((double forward, double rotate)->assertTrue("The left motor should reduce when drifting left and driving backwards", 
                Math.abs(forward + rotate) > Math.abs(left.getSpeed())), -10);
    }
    
    @Test @Ignore
    public void testBackwardAssistDoesNotReduceRightMotorWhenDriftingLeft() {
        testBackwardAssist((double forward, double rotate)->assertEquals("The right motor should not reduce when drifting left and driving backwards", 
                forward - rotate, right.getSpeed(), EPSILON), -10);
    }
    
    @Test @Ignore
    public void testBackwardAssistDoesNotReduceLeftMotorWhenDriftingRight() {
        testBackwardAssist((double forward, double rotate)->assertEquals("The left motor should not reduce when drifting right and driving backwards", 
                forward + rotate, left.getSpeed(), EPSILON), 10);
    }
    
    @Test @Ignore
    public void testBackwardAssistReducesRightMotorWhenDriftingRight() {
        testBackwardAssist((double forward, double rotate)->assertTrue("The right motor should reduce when drifting right and driving backwards", 
                Math.abs(forward - rotate) > Math.abs(right.getSpeed())), 10);
    }
    // Yes, this is code duplication, but it's probably a livable amount of duplication.
    public void testBackwardAssist(ForwardAssistAssertion assertion, double angle) {
        // Sets the initial "edge" for the RightY axis as negative
        drivetrain.arcade(-1, 0);
        for (double i = 0; i < DrivetrainWithGyro.FORWARD_ASSIST_MAX_TURN_SPEED; i += 0.01) {
            gyro.setAngle(angle);
            double forward = randomBackwardSpeed();
            double rotate = i;
            drivetrain.arcade(forward, rotate);
            assertion.doAssertion(forward, rotate);
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
