package org.usfirst.frc.team1076.robot.commands;

import org.strongback.components.Gyroscope;
import org.usfirst.frc.team1076.robot.IGamepad;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithGyro;
import org.usfirst.frc.team1076.robot.subsystems.Winch;

/**
 * Tiny command which does basically the same thing as a TeleopCommand, 
 * but supports the use of gyro. This command zeros the gyro upon initialization
 */
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
