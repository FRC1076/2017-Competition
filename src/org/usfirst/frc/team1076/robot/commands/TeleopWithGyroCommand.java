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

public class TeleopWithGyroCommand extends TeleopCommand {
    Gyroscope gyro;
    
    public TeleopWithGyroCommand(DrivetrainWithGyro drivetrain, IGamepad driver, IGamepad operator, Winch winch) {
        super(drivetrain, driver, operator, winch);
        this.gyro = drivetrain.getGyro();
    }

    @Override
    public void initialize() {
        gyro.zero();
    }
}
