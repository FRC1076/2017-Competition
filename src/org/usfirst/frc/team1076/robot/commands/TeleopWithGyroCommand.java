package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.strongback.components.Gyroscope;
import org.strongback.mock.Mock;
import org.usfirst.frc.team1076.robot.IGamepad;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadAxis;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadStick;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithGyro;
import org.usfirst.frc.team1076.robot.subsystems.Winch;

public class TeleopWithGyroCommand extends Command {
    Gyroscope gyro;
    DrivetrainWithGyro drivetrain;
    IGamepad gamepad;
    Winch winch;
    
    public TeleopWithGyroCommand(DrivetrainWithGyro drivetrain, IGamepad gamepad, Winch winch) {
        super(drivetrain, winch);
        this.gyro = drivetrain.getGyro();
        this.drivetrain = drivetrain;
        this.gamepad = gamepad;
        this.winch = winch;
    }
    
    public TeleopWithGyroCommand(DrivetrainWithGyro drivetrain, IGamepad gamepad) {
        this(drivetrain, gamepad, new Winch(Mock.stoppedMotor()));
        Strongback.logger().warn("TeleopWithGyroCommand initalized without Winch, using mock winch instead");
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

        final double winch_extend = gamepad.getAxis(GamepadAxis.LeftTrigger);
        final double winch_retract = gamepad.getAxis(GamepadAxis.RightTrigger);
        // Ideally you shouldn't press both buttons at the same time, however
        // taking the difference of the two is a simple way of allowing both actions
        // without special logic
        winch.setSpeed(winch_extend - winch_retract);
        return false;
    }
}
