package org.usfirst.frc.team1076.robot;

public class StrongbackGamepad extends Gamepad {
    
    org.strongback.components.ui.Gamepad gamepad;
    
    public StrongbackGamepad(org.strongback.components.ui.Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    @Override
    public double getRawAxis(GamepadAxis axis) {
        double value = gamepad.getAxis(axis.value()).read();
        // inverts y-axis because it is backwards (unaltered, the gamepad treats down as positive)
        if (axis == GamepadAxis.LeftY || axis == GamepadAxis.RightY) {
            return -value;
        }
        return value;
    }

    @Override
    public boolean getButton(GamepadButton button) {
        return gamepad.getButton(button.value()).isTriggered();
    }
}
