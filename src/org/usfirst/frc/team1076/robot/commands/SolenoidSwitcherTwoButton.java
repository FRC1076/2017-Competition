package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Solenoid;
import org.usfirst.frc.team1076.robot.Gamepad;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;

public class SolenoidSwitcherTwoButton extends Command {
    Solenoid gearShifter;
    Gamepad gamepad;
    GamepadButton retractButton; // = GamepadButton.LB;
    GamepadButton extendButton; // = GamepadButton.RB;
    
    public SolenoidSwitcherTwoButton(Solenoid gearShifter, Gamepad gamepad, GamepadButton retractButton, GamepadButton extendButton) {
        this.gearShifter = gearShifter;
        this.gamepad = gamepad;
    }
    
    public boolean execute() {
        if (gamepad.getButton(retractButton)) {
            gearShifter.retract();
        } else if (gamepad.getButton(extendButton)) {
            gearShifter.extend();
        }
        
        return false;
    }
    
}
