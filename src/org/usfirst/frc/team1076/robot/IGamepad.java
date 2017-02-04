package org.usfirst.frc.team1076.robot;

import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadStick;

public interface IGamepad {
	class Coords {
		double x, y;
		public Coords(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public double getAxis(GamepadAxis axis);
	public double getRawAxis(GamepadAxis axis);
	public boolean getButton(GamepadButton button);
	public Coords getStick(GamepadStick stick);
}
