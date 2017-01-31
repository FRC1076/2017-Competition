package org.usfirst.frc.team1076.robot.subsystems;

import org.strongback.components.Switch;

/**
 * AutonmousSwitches takes two Strongback Switches. Physically, these
 * should be actual switches which turn on or off. They are used to determine
 * which autonomous to use.
 * LEFT is for when the robot is on the left side,
 * RIGHT is for when the robot is on the left side,
 * CENTER is for when the robot is in the middle
 * NONE is for no autonomous (or, possible an experimental/unused auto mode). 
 */
public class AutonomousSwitches {
    public enum AutonomousType {
        LEFT, RIGHT, CENTER, NONE;
    }
    Switch left;
    Switch right;
    
    public AutonomousSwitches(Switch left, Switch right) {
        this.left = left;
        this.right = right;
    }
    
    public AutonomousType getAutonomousType() {
        if (left.isTriggered() && right.isTriggered()) {
            return AutonomousType.CENTER;
        } else if (left.isTriggered() && !right.isTriggered()) {
            return AutonomousType.LEFT;
        } else if (!left.isTriggered() && right.isTriggered()) {
            return AutonomousType.RIGHT;
        } else {
            return AutonomousType.NONE;
        }
    }
}
