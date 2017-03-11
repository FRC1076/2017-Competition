package org.usfirst.frc.team1076.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.strongback.mock.Mock;
import org.strongback.mock.MockMotor;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;

public class TestDrivetrain {

	private static final double EPSILON = 1E-10;
	MockMotor left = Mock.stoppedMotor(); 
	MockMotor right = Mock.stoppedMotor(); 
	Drivetrain drivetrain = new Drivetrain(left, right); 
	
	@Before
	public void reset() {
		left.setSpeed(0); 
	    right.setSpeed(0);
	    drivetrain.leftFactor = 1;
	    drivetrain.rightFactor = 1;
	}
	
	@Test
	public void testSetLeftSpeed() {
		drivetrain.setLeftSpeed(0.7);
		assertEquals("Left speed should match exactly when the leftFactor is 1",
				0.7, left.getSpeed(), EPSILON);
		
		drivetrain.leftFactor = 0.5;
		drivetrain.setLeftSpeed(0.7);
		assertEquals("Left speed should be scaled by leftFactor",
				0.7 * 0.5, left.getSpeed(), EPSILON);
	}

	@Test
	public void testSetRightSpeed() {
		drivetrain.setRightSpeed(0.7);
		assertEquals("Right speed should match exactly when the rightFactor is 1",
				0.7, right.getSpeed(), EPSILON);
		
		drivetrain.rightFactor = 0.5;
		drivetrain.setRightSpeed(0.7);
		assertEquals("Right speed should be scaled by rightFactor",
				0.7 * 0.5, right.getSpeed(), EPSILON);
	}

	@Test
	public void testSetSpeed() {
		drivetrain.setSpeed(1.0);
		assertEquals("Left and right motor speeds should be equal",
				left.getSpeed(), right.getSpeed(), EPSILON);
		
		drivetrain.leftFactor = 0.6;
		drivetrain.rightFactor = 0.4;
		drivetrain.setSpeed(1.0);
		assertEquals("Left motor speeds should scale with leftFactor independent of rightFactor",
				0.6, left.getSpeed(), EPSILON);
		assertEquals("Right motor speeds should scale with rightFactor independent of leftFactor",
				0.4, right.getSpeed(), EPSILON);
	}

	@Test
	public void testStop() {
		drivetrain.setSpeed(1.0);
		assertTrue("Motors should not be stopped before stopping",
				left.getSpeed() > 0 && right.getSpeed() > 0);
		drivetrain.stop();
		assertEquals("Left motor should be stopped",
				0.0, left.getSpeed(), EPSILON);
		assertEquals("Right motor should be stopped",
				0.0, right.getSpeed(), EPSILON);
	}

    @Test
    public void testForwardArcade() {
        drivetrain.arcade(1, 0);
        assertEquals("Left motor should be equal to forward value", 
                1.0, left.getSpeed(), EPSILON);
        assertEquals("Right motor should be equal to forward value", 
                1.0, right.getSpeed(), EPSILON);
        
        drivetrain.arcade(-1.0, 0);
        assertEquals("Left motor should be equal to forward value", 
                -1.0, left.getSpeed(), EPSILON);
        assertEquals("Right motor should be equal to forward value", 
                -1.0, right.getSpeed(), EPSILON);
    }
    
    @Test
    public void testRotateArcade() {
        drivetrain.arcade(0, 1);
        assertEquals("Left motor run forwards to turn robot left", 
                1.0, left.getSpeed(), EPSILON);
        assertEquals("Right motor run backwards to turn robot right", 
                -1.0, right.getSpeed(), EPSILON);
        
        drivetrain.arcade(0, -1);
        assertEquals("Left motor run backwards to turn robot right", 
                -1.0, left.getSpeed(), EPSILON);
        assertEquals("Right motor run forwards to turn robot right", 
                1.0, right.getSpeed(), EPSILON);
    }
}
