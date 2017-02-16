package org.usfirst.frc.team1076.robot.subsystems;

import org.strongback.components.Solenoid;

public class GearRamp {
	public enum State {
		Up,
		Down;
	}
	
	Solenoid solenoid;
	
	public GearRamp(Solenoid solenoid) {
		this.solenoid = solenoid;
	}
	
	public void set(State state) {
		if (state == State.Up) {
			solenoid.extend();
		} else {
			solenoid.retract();
		}
	}
}
