package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Gyroscope;
import org.usfirst.frc.team1076.robot.IGamepad;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadStick;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithGyro;

public class TeleopWithGyroCommand extends Command {
    Gyroscope gyro;
    DrivetrainWithGyro drivetrain;
    IGamepad gamepad;
    
    public TeleopWithGyroCommand(DrivetrainWithGyro drivetrain, IGamepad gamepad) {
        super(drivetrain);
        this.gyro = drivetrain.getGyro();
        this.drivetrain = drivetrain;
        this.gamepad = gamepad;
    }

    @Override
    public void initialize() {
        gyro.zero();
    }
    
    @Override
    public boolean execute() {
        final double forward = gamepad.getStick(GamepadStick.Right).y; //.getAxis(GamepadAxis.RightY);
        final double rotate = gamepad.getStick(GamepadStick.Left).x; //.getAxis(GamepadAxis.LeftX);
        drivetrain.arcade(forward, rotate);
        return false;
    }
}
