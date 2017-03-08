package org.usfirst.frc.team1076.robot;

public class StrongbackGamepad extends Gamepad {
    
    org.strongback.components.ui.Gamepad gamepad;
    
    public StrongbackGamepad(org.strongback.components.ui.Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    @Override
    public double getRawAxis(GamepadAxis axis) {
        return gamepad.getAxis(axis.value()).read();
    }

    @Override
    public boolean getButton(GamepadButton button) {
        return gamepad.getButton(button.value()).isTriggered();
    }
}