package org.usfirst.frc.team1076.robot.subsystems;

import org.strongback.components.Solenoid;

public class Brakes {
	public enum State {
		Enabled,
		Disabled;
	}

	Solenoid solenoid;

	public Brakes(Solenoid solenoid) {
		this.solenoid = solenoid;
	}
	
	public void set(State state) {
		if (state == State.Enabled) {
			solenoid.extend();
		} else {
			solenoid.retract();
		}
	}
}
