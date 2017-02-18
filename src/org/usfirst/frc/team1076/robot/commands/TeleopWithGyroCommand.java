package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Gyroscope;
import org.usfirst.frc.team1076.robot.IGamepad;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;

public class TeleopWithGyroCommand extends Command {

    public static final double MAX_DEGREES_PER_SECOND = 90;
    public static final double FORWARD_ASSIST_MAX_TURN_SPEED = 0.1;
    public static double FORWARD_ASSIST_SENSITIVITY = 1.0;
    Gyroscope gyro;
    Drivetrain drivetrain;
    IGamepad gamepad;
    
    
    
    public TeleopWithGyroCommand(Gyroscope gyro, Drivetrain drivetrain, IGamepad gamepad) {
        super(drivetrain);
        this.gyro = gyro;
        this.drivetrain = drivetrain;
        this.gamepad = gamepad;
    }

    @Override
    public void initialize() {
        gyro.zero();
    }
    
    int previousSign = 0;
    @Override
    public boolean execute() {
        final double forward = gamepad.getAxis(GamepadAxis.RightY);
        final double rotate = gamepad.getAxis(GamepadAxis.LeftX);
        
        final int sign = (int) Math.signum(forward);
        if (sign != previousSign) {
        	gyro.zero();
        	previousSign = sign;
        }
        
        // The normGyroRate should be equal to rotate under ideal conditions
        final double normGyroAngle = Math.signum(forward) * FORWARD_ASSIST_SENSITIVITY * gyro.getAngle() / 360; 
        // TODO: Finish this left/right calculation
        double left = forward + rotate;
        double right = forward - rotate;
        
        if (shouldForwardAssist()) {
            if (normGyroAngle > 0) { // Drifting right
                left = left * (1 - Math.abs(normGyroAngle));
//                System.out.println("Left Corrected: " + left);
            } else {
                // left = (Math.abs(left) * (1 + Math.abs(normGyroAngle)));
                right = right * (1 - Math.abs(normGyroAngle));
//                System.out.println("Right Corrected: " + right);
            }
        } else {
        	// turning
        	gyro.zero();
        }
        
//        System.out.println("Correction Factor: " + (1 - Math.abs(normGyroAngle)));
        drivetrain.setLeftSpeed(left);
        drivetrain.setRightSpeed(right);
        return false;
    }
    
    public boolean shouldForwardAssist() {
        return Math.abs(gamepad.getAxis(GamepadAxis.LeftX)) < FORWARD_ASSIST_MAX_TURN_SPEED;
    }

}
