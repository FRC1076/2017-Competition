package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Solenoid;
import org.usfirst.frc.team1076.robot.Gamepad;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;

public class BrakeCommand extends Command {
    Solenoid brake;
    Gamepad gamepad;
    
    public BrakeCommand(Solenoid brake, Gamepad gamepad) {
        this.brake = brake;
        this.gamepad = gamepad;
    }
    
    public boolean execute() {
        final double trigger = gamepad.getAxis(GamepadAxis.RightTrigger);
        if (trigger < 0.5) { // Keep brakes up
            brake.extend();
        } else { // Activate brakes
            brake.retract();
        }
        return false;
    }
}
