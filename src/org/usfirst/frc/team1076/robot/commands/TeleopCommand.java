package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.strongback.mock.Mock;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadStick;
import org.usfirst.frc.team1076.robot.IGamepad;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.Winch;

/**
 * Controls the robot using a joy stick controller.
 * This command is intended to run continuously for the entire lifetime of the teleop mode.
 */
public class TeleopCommand extends Command {
	Drivetrain leftRight;
	IGamepad driver;
	IGamepad operator;
	Winch winch;
	
    public TeleopCommand(Drivetrain leftRight, IGamepad driver, IGamepad operator, Winch winch) {
         super(leftRight, winch); //Require the motors and winch
         this.driver = driver;
         this.leftRight = leftRight;
         this.winch = winch;
         this.operator = operator;
    }

    // Called repeatedly when this Command is scheduled to run
    public boolean execute() {
        final double forward = driver.getStick(GamepadStick.Right).y; //.getAxis(GamepadAxis.RightY);
        final double rotate = driver.getStick(GamepadStick.Left).x; //.getAxis(GamepadAxis.LeftX);
    	leftRight.arcade(forward, rotate);
    	
    	final double winch_extend = gamepad.getAxis(GamepadAxis.LeftTrigger);
    	final double winch_retract = gamepad.getAxis(GamepadAxis.RightTrigger);
    	// Ideally you shouldn't press both buttons at the same time, however
    	// taking the difference of the two is a simple way of allowing both actions
    	// without special logic
    	winch.setSpeed(winch_extend - winch_retract);
    	return isFinished();
    }
    

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
}
