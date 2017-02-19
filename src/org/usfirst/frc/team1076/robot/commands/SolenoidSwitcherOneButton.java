package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Solenoid;
import org.usfirst.frc.team1076.robot.Gamepad;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;

public class SolenoidSwitcherOneButton extends Command {
    public enum SwitchType {
        HOLD, STICKY;
    }
    
    Solenoid gearShifter;
    Gamepad gamepad;
    GamepadButton button;
    SwitchType type;
    
    boolean lastButton;
    
    
    public SolenoidSwitcherOneButton(Solenoid gearShifter, Gamepad gamepad, GamepadButton button, SwitchType type) {
        this.gearShifter = gearShifter;
        this.gamepad = gamepad;
        this.button = button;
        this.type = type;
    }
    
    public boolean execute() {
        if (type == SwitchType.STICKY) {
            stickyExecute();
        } else if (type == SwitchType.HOLD) {
            holdExecute();
        }
        
        lastButton = gamepad.getButton(button);
        return false;
    }

    public void stickyExecute() {
        boolean buttonHeld = gamepad.getButton(button);
        // Button pressed edge
        if (buttonHeld && !lastButton) {
            if (gearShifter.isRetracting()) {
                gearShifter.extend();
            } else {
                gearShifter.retract();
            }
        }
    }
    
    
    public void holdExecute() {
        boolean buttonHeld = gamepad.getButton(button);
        // Button pressed edge
        if (buttonHeld && !lastButton) {
            gearShifter.extend();
        } else if (!buttonHeld && lastButton) { // Button released edge
            gearShifter.retract();
        }
    }
}
