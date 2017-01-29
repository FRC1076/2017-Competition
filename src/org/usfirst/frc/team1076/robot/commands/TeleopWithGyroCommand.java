package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Gyroscope;
import org.usfirst.frc.team1076.robot.IGamepad;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;

public class TeleopWithGyroCommand extends Command {

    public static final double MAX_DEGREES_PER_SECOND = 90;
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
    
    @Override
    public boolean execute() {
        final double forward = gamepad.getAxis(GamepadAxis.RightY);
        final double rotate = gamepad.getAxis(GamepadAxis.LeftX);
        // The normGyroRate should be equal to rotate under ideal conditions
        final double normGyroRate = gyro.getRate() / MAX_DEGREES_PER_SECOND;
        
        // Positive values means the robot is turning left too fast
        // Negative values means the robot is turning right too fast
        double deltaRate = normGyroRate - rotate; 
        // TODO: Finish this left/right calculation
        double left = (forward + rotate);
        double right = (forward - rotate);
        
        drivetrain.setLeftSpeed(left);
        drivetrain.setRightSpeed(right);
        
        return false;
    }

}
