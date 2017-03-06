package org.usfirst.frc.team1076.robot;

import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadStick;

public class MockGamepad implements IGamepad {

	public double lt, lx, ly, rt, rx, ry;
	public boolean a, b, back, lb, lstick, rb, rstick, start, x, y;
	public double deadzone;
	public MockGamepad() {
		reset();
	}
	
	public void reset() {
		lt = lx = ly = rt = rx = ry = 0;
		a = b = back = lb = lstick = rb = rstick = start = x = y = false;
	}
	
	public double getAxis(GamepadAxis axis) {
		switch (axis) {
		case LeftTrigger: return lt;
		case LeftX: return lx;
		case LeftY: return ly;
		case RightTrigger: return rt;
		case RightX: return rx;
		case RightY: return ry;
		default: return 0;
		}
	}
	
	public boolean getButton(GamepadButton button) {
		switch (button) {
		case A: return a;
		case B: return b;
		case Back: return back;
		case LB: return lb;
		case LStick: return lstick;
		case RB: return rb;
		case RStick: return rstick;
		case Start: return start;
		case X: return x;
		case Y: return y;
		default: return false;
		}
	}

	/*
	 * This is a mock; no difference between raw axis and
	 * normal axis since there is no deadzone.
	 */
    @Override
    public double getRawAxis(GamepadAxis axis) {
        return getAxis(axis);
    }

    @Override
    public Coords getStick(GamepadStick stick) {
        switch (stick) {
        case Left: return new Coords(lx, ly);
        case Right: return new Coords(rx, ry);
        default: return null; // Unreachable
        }
    }

    @Override
    public void setDeadzone(double deadzone) {
        this.deadzone = deadzone;
    }
}
