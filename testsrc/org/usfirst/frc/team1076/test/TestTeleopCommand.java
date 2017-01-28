package org.usfirst.frc.team1076.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.strongback.mock.Mock;
import org.strongback.mock.MockMotor;
import org.usfirst.frc.team1076.robot.commands.TeleopCommand;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.test.mock.MockGamepad;

public class TestTeleopCommand {

	private static final double EPSILON = 1E-10;

	MockMotor left = Mock.stoppedMotor();
	MockMotor right = Mock.stoppedMotor();
	MockGamepad gamepad = new MockGamepad();
	Drivetrain drivetrain = new Drivetrain(left, right);
	TeleopCommand teleop = new TeleopCommand(gamepad, drivetrain);
	
	@Before
	public void reset() {
		left.setSpeed(0);
		right.setSpeed(0);
		gamepad.reset();
	}
	
	@Test
	public void testForwards() {
		gamepad.ry = 1;
		teleop.execute();
		assertEquals("Left motor should be 1.0 when going forward",
				1.0, left.getSpeed(), EPSILON);
		assertEquals("Right motor should be 1.0 when going forward",
				1.0, right.getSpeed(), EPSILON);
	}
	
	@Test
	public void testRotate() {
		gamepad.lx = 1;
		teleop.execute();
		assertEquals("Left motor should be negative when turning left",
				1.0, left.getSpeed(), EPSILON);
		assertEquals("Right motor should be negative when turning right",
				-1.0, right.getSpeed(), EPSILON);
	}
	
	@Test
	public void testRotateAndForwards() {
		gamepad.ry = 1;
		gamepad.lx = 0.5;
		teleop.execute();
		
		System.out.println("[TeleopCommand Turning]");
		System.out.println("Left Motor: " + left.getSpeed());
		System.out.println("Right Motor: " + right.getSpeed());
		
		assertEquals("Left motor should be at maximum speed",
				1.0, left.getSpeed(), EPSILON);
		assertTrue("Right motor should be less than left motor",
				right.getSpeed() < left.getSpeed());
		assertTrue("Right motor should not have zero or negative speed",
				right.getSpeed() > 0);
	}

}
