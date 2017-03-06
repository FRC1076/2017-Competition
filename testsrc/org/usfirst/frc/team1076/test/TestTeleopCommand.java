package org.usfirst.frc.team1076.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.strongback.mock.Mock;
import org.strongback.mock.MockMotor;
import org.usfirst.frc.team1076.robot.MockGamepad;
import org.usfirst.frc.team1076.robot.commands.TeleopCommand;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.Winch;

public class TestTeleopCommand {

	private static final double EPSILON = 1E-10;

	MockMotor left = Mock.stoppedMotor();
	MockMotor right = Mock.stoppedMotor();
	Winch winch = new Winch(Mock.stoppedMotor()); // Mock winch
	MockGamepad driver = new MockGamepad();
	MockGamepad operator = new MockGamepad();
	Drivetrain drivetrain = new Drivetrain(left, right);
	TeleopCommand teleop = new TeleopCommand(drivetrain, driver, operator, winch);
	
	@Before
	public void reset() {
		left.setSpeed(0);
		right.setSpeed(0);
		driver.reset();
	}
	
	@Test
	public void testForwards() {
		driver.ry = 1;
		teleop.execute();
		assertEquals("Left motor should be 1.0 when going forward",
				1.0, left.getSpeed(), EPSILON);
		assertEquals("Right motor should be 1.0 when going forward",
				1.0, right.getSpeed(), EPSILON);
	}
	
	@Test
	public void testRotateLeft() {
		driver.lx = 1;
		teleop.execute();
		assertEquals("Left motor should be moving forwards when the robot turns left",
				1.0, left.getSpeed(), EPSILON);
		assertEquals("Right motor should be moving backwards when the robot turns left",
				-1.0, right.getSpeed(), EPSILON);
	}

    @Test
    public void testRotateRight() {
        driver.lx = -1;
        teleop.execute();
        assertEquals("Left motor should be moving backwards when the robot turns right",
                -1.0, left.getSpeed(), EPSILON);
        assertEquals("Right motor should be moving forwards when the robot turns right",
                1.0, right.getSpeed(), EPSILON);
    }
	
	@Test
	public void testRotateAndForwards() {
		driver.ry = 1;
		driver.lx = 0.5;
		teleop.execute();
		
		assertEquals("Left motor should be at maximum speed",
				1.0, left.getSpeed(), EPSILON);
		assertTrue("Right motor should be less than left motor",
				right.getSpeed() < left.getSpeed());
		assertTrue("Right motor should not have zero or negative speed",
				right.getSpeed() > 0);
	}

}
