package org.usfirst.frc.team1076.robot;

import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;

public interface IGamepad {
	public double getAxis(GamepadAxis axis);
	public boolean getButton(GamepadButton button);
}
