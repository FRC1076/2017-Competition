package org.usfirst.frc.team1076.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.strongback.mock.Mock;
import org.strongback.mock.MockSwitch;
import org.usfirst.frc.team1076.robot.subsystems.AutonomousSwitches;
import org.usfirst.frc.team1076.robot.subsystems.AutonomousSwitches.AutonomousType;

public class TestAutonomousSwitches {
    MockSwitch left = Mock.notTriggeredSwitch();
    MockSwitch right = Mock.notTriggeredSwitch();
    AutonomousSwitches switches = new AutonomousSwitches(left, right);
    
    @Before
    public void reset() {
        left.setNotTriggered();
        right.setNotTriggered();
    }

    @Test
    public void testBothSwitchesActive() {
        left.setTriggered();
        right.setTriggered();
        assertEquals("The CENTER autonomous should be used when both switches are triggered",
                AutonomousType.CENTER, switches.getAutonomousType());
    }

    @Test
    public void testLeftSwitchActive() {
        left.setTriggered();
        right.setNotTriggered();
        assertEquals("The LEFT autonomous should be used when only the left switch is triggered",
                AutonomousType.LEFT, switches.getAutonomousType());
    }

    @Test
    public void testRightSwitchActive() {
        left.setNotTriggered();
        right.setTriggered();
        assertEquals("The RIGHT autonomous should be used when only the right switch is triggered",
                AutonomousType.RIGHT, switches.getAutonomousType());
    }

    @Test
    public void testNoSwitchesActive() {
        left.setNotTriggered();
        right.setNotTriggered();
        assertEquals("The NONE autonomous should be used when no switches are triggered",
                AutonomousType.NONE, switches.getAutonomousType());
    }
}
