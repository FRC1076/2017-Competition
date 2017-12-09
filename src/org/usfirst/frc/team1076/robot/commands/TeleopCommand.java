package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.strongback.components.Motor;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadStick;
import org.usfirst.frc.team1076.robot.IGamepad;
import org.usfirst.frc.team1076.robot.subsystems.ArcadeCorrector;
import org.usfirst.frc.team1076.robot.subsystems.BallSpitter;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.Winch;

/**
 * Controls the robot using a joy stick controller.
 * This command is intended to run continuously for the entire lifetime of the teleop mode.
 * Note that this command does NOT control pneumatic systems or other button presses.
 * Instead, that is handled by Strongback's Switch Reactor
 */
public class TeleopCommand extends Command {
    Drivetrain leftRight;
    IGamepad driver;
    IGamepad operator;
    Winch winch;
    BallSpitter ballSpitter;
    boolean isReversed;
    boolean lastEdge;
    ArcadeCorrector corrector;
    
    
    public TeleopCommand(Drivetrain leftRight, ArcadeCorrector corrector, IGamepad driver, IGamepad operator, Winch winch, BallSpitter ballSpitter) {
        super(leftRight, winch); //Require the motors and winch
        this.driver = driver;
        this.leftRight = leftRight;
        this.winch = winch;
        this.operator = operator;
        this.corrector = corrector;
        this.ballSpitter = ballSpitter;
    }
    
    @Override
    public void initialize() {
        isReversed = false;
        lastEdge = false;
    }

    // Called repeatedly when this Command is scheduled to run
    public boolean execute() {
        double forward = driver.getStick(GamepadStick.Right).y;
        double rotate = driver.getStick(GamepadStick.Left).x;

        if (isReversed) {
            forward = -forward;
            rotate = -rotate;
        }
        
        // Makes the robot drive left (from the robot's perspective) when driving backwards
        // instead of driving left from the driver's perspective.
        if (forward >= 0) {
            if (forward == 0 && isReversed) {
                rotate = -rotate;
            }
            leftRight.arcade(forward, rotate*Math.abs(rotate), corrector);
        } else {
            leftRight.arcade(forward, -(rotate*Math.abs(rotate)), corrector);
        }
        final double winchSpeed = operator.getStick(GamepadStick.Left).y;
        // Ideally you shouldn't press both buttons at the same time, however
        // taking the difference of the two is a simple way of allowing both actions
        // without special logic
        winch.extend(winchSpeed);
        
        final double spitterSpeed = operator.getStick(GamepadStick.Right).y;
        ballSpitter.setSpeed(spitterSpeed);
        
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
    
    public void end() {
        Strongback.logger().warn("TeleopCommand killed!");
    }
}
