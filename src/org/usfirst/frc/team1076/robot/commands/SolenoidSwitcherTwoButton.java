package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Solenoid;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;
import org.usfirst.frc.team1076.robot.IGamepad;

public class SolenoidSwitcherTwoButton extends Command {
    Solenoid gearShifter;
    IGamepad gamepad;
    GamepadButton retractButton; // = GamepadButton.LB;
    GamepadButton extendButton; // = GamepadButton.RB;
    
    public SolenoidSwitcherTwoButton(Solenoid gearShifter, IGamepad gamepad, GamepadButton retractButton, GamepadButton extendButton) {
        this.gearShifter = gearShifter;
        this.gamepad = gamepad;
        this.retractButton = retractButton;
        this.extendButton = extendButton;
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
