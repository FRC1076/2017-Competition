package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Solenoid;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;
import org.usfirst.frc.team1076.robot.IGamepad;

public class SolenoidSwitcherOneButton extends Command {
    public enum SwitchType {
        ON_HOLD_EXTEND, ON_HOLD_RETRACT, STICKY;
    }
    
    Solenoid gearShifter;
    IGamepad gamepad;
    GamepadButton button;
    SwitchType type;
    
    boolean lastButton;
    
    
    public SolenoidSwitcherOneButton(Solenoid gearShifter, IGamepad gamepad, GamepadButton button, SwitchType type) {
        this.gearShifter = gearShifter;
        this.gamepad = gamepad;
        this.button = button;
        this.type = type;
    }
    
    public boolean execute() {
        if (type == SwitchType.STICKY) {
            stickyExecute();
        } else if (type == SwitchType.ON_HOLD_EXTEND) {
            onHoldExtendExecute();
        } else if (type == SwitchType.ON_HOLD_RETRACT) {
            onHoldRetractExecute();
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
    
    public void onHoldExtendExecute() {
        boolean buttonHeld = gamepad.getButton(button);
        // Button pressed edge
        if (buttonHeld && !lastButton) {
            gearShifter.extend();
        } else if (!buttonHeld && lastButton) { // Button released edge
            gearShifter.retract();
        }
    }
    
    public void onHoldRetractExecute() {
        boolean buttonHeld = gamepad.getButton(button);
        // Button pressed edge
        if (buttonHeld && !lastButton) {
            gearShifter.retract();
        } else if (!buttonHeld && lastButton) { // Button released edge
            gearShifter.extend();
        }        
    }
}
