package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;

public class StrafeCommand extends Command {
	
	Drivetrain drive;
	double time = 0;
	
	public StrafeCommand(Drivetrain drive) {
		super(drive);
		this.drive = drive;
	}
	
	@Override
	public boolean execute() {	
		if (time < 2) {
			drive.setSpeed(-0.75);			
		} else if (time < 4) {
			drive.setSpeed(0.75);
		} else {
			drive.stop();			
		}
		
		time += 1.0/50;
		return time > 4;
	}

}
