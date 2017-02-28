package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.strongback.mock.Mock;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;
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
    boolean isReversed = false;
    boolean lastEdge = false;
    
    public TeleopCommand(Drivetrain leftRight, IGamepad driver, IGamepad operator, Winch winch) {
        super(leftRight, winch); //Require the motors and winch
        this.driver = driver;
        this.leftRight = leftRight;
        this.winch = winch;
        this.operator = operator;
    }

    // Called repeatedly when this Command is scheduled to run
    public boolean execute() {
        double forward = driver.getStick(GamepadStick.Right).y;
        double rotate = driver.getStick(GamepadStick.Left).x;

        if (isReversed) {
            forward = -forward;
        }

        // Makes the robot drive left (from the robot's perspective) when driving backwards
        // instead of driving left from the driver's perspective.
        if (forward >= 0) {
            leftRight.arcade(forward, rotate*Math.abs(rotate));
        } else {
            leftRight.arcade(forward, -(rotate*Math.abs(rotate)));
        }
        final double winchSpeed = operator.getStick(GamepadStick.Left).y;
        // Ideally you shouldn't press both buttons at the same time, however
        // taking the difference of the two is a simple way of allowing both actions
        // without special logic
        winch.setSpeed(winchSpeed);

        // Rising Edge of X button
        if (driver.getButton(GamepadButton.X) && !lastEdge) {
            if (isReversed) {
                isReversed = false;
                Strongback.logger().info("Controls are now normal!");
            } else {
                isReversed = true;
                Strongback.logger().info("Controls are now reversed!");
            }

        }
        lastEdge = driver.getButton(GamepadButton.X);

        return isFinished();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
}
