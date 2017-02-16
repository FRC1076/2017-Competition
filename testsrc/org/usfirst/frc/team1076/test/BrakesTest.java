package org.usfirst.frc.team1076.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.strongback.components.Solenoid;
import org.strongback.mock.Mock;
import org.strongback.mock.MockSolenoid;
import org.usfirst.frc.team1076.robot.subsystems.Brakes;

public class BrakesTest {

	@Test
	public void testBrakes() {
		
		MockSolenoid solenoid = Mock.manualSolenoid();
		Brakes brakes = new Brakes(solenoid);
		/*
		solenoid.retract();
		assertEquals("The solenoid should be retracted", Solenoid.Direction.RETRACTING, solenoid.getDirection());
		*/
		// The strongback mock is broken, so we can't have a real test until we fix that
		brakes.set(Brakes.State.Enabled);
		assertEquals("The solenoid should be extended when the brakes are enabled",
				Solenoid.Direction.EXTENDING, solenoid.getDirection());
		/*
		brakes.set(Brakes.State.Disabled);
		assertEquals("The solenoid should be retracted when the brakes are disabled",
				Solenoid.Direction.RETRACTING, solenoid.getDirection());
		*/
	}

}
