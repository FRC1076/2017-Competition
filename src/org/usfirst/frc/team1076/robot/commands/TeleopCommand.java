package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadStick;
import org.usfirst.frc.team1076.robot.IGamepad;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;

/**
 * Controls the robot using a joy stick controller.
 * This command is intended to run continuously for the entire lifetime of the teleop mode.
 */
public class TeleopCommand extends Command {
	Drivetrain leftRight;
	IGamepad gamepad;
	
    public TeleopCommand(IGamepad gamepad, Drivetrain leftRight ) {
         super(leftRight); //Require the motors
         this.gamepad = gamepad;
         this.leftRight = leftRight;
    }

    // Called repeatedly when this Command is scheduled to run
    public boolean execute() {
        final double forward = gamepad.getStick(GamepadStick.Right).y; //.getAxis(GamepadAxis.RightY);
        final double rotate = gamepad.getStick(GamepadStick.Left).x; //.getAxis(GamepadAxis.LeftX);
    	leftRight.arcade(forward, rotate);
    	return isFinished();
    }
    

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
}
