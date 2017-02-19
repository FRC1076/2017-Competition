package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Solenoid;
import org.usfirst.frc.team1076.robot.Gamepad;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;

public class SolenoidSwitcher extends Command {
    Solenoid gearShifter;
    Gamepad gamepad;
    
    public SolenoidSwitcher(Solenoid gearShifter, Gamepad gamepad) {
        this.gearShifter = gearShifter;
        this.gamepad = gamepad;
    }
    
    public boolean execute() {
        if (gamepad.getButton(GamepadButton.LB)) {
            gearShifter.retract();
        } else if (gamepad.getButton(GamepadButton.RB)) {
            gearShifter.extend();
        }
        
        return false;
    }
    
}
