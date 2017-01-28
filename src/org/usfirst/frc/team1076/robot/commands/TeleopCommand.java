package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.IGamepad;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;

/**
 * Controls the robot using a joy stick controller.
 * This command is intended to run continuously for the entire lifetime of the teleop mode.
 */
public class TeleopCommand extends Command {

	double maxSpeed = 1.0;
	Drivetrain leftRight;
	IGamepad gamepad;
	
    public TeleopCommand(IGamepad gamepad, Drivetrain leftRight ) {
         super(leftRight); //Require the motors
         this.gamepad = gamepad;
         this.leftRight = leftRight;
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public boolean execute() {    	
    	final double forward = gamepad.getAxis(GamepadAxis.RightY);
    	final double rotate = gamepad.getAxis(GamepadAxis.LeftX);

    	// To rotate counterclockwise, we want the following modification:
    	// V   ^
    	// Which means that left is decreased, and right is increased.
    	final double left = forward + rotate;
    	final double right = forward - rotate;
    	
    	// We don't want any motor to run faster than unit speed, so if anything
    	// is larger than the max speed we'll scale them down.
    	// We use the reciprocal of the max speed so that if for example maxSpeed
    	// is 0.5, then we'll get 2.0 and divide by 2.0.
    	final double norm = selectMaxAbs(1/maxSpeed, left, right);
    	
    	leftRight.setLeftSpeed(left / norm);
    	leftRight.setRightSpeed(right / norm);
    	
    	return isFinished();
    }
    
    private double selectMaxAbs(double... items) {
    	assert items.length > 0;
    	double result = Math.abs(items[0]);
    	for (double item : items) {
    		result = Math.max(result, Math.abs(item));
    	}
    	return result;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
}
