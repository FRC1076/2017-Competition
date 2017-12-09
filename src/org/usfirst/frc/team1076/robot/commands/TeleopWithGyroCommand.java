package org.usfirst.frc.team1076.robot.commands;

import org.strongback.components.Gyroscope;
import org.strongback.components.Motor;
import org.usfirst.frc.team1076.robot.IGamepad;
import org.usfirst.frc.team1076.robot.subsystems.BallSpitter;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.GyroPIDCorrector;
import org.usfirst.frc.team1076.robot.subsystems.Winch;

/**
 * Tiny command which does basically the same thing as a TeleopCommand, 
 * but supports the use of gyro. This command zeros the gyro upon initialization
 */
public class TeleopWithGyroCommand extends TeleopCommand {
    Gyroscope gyro;
    
    public TeleopWithGyroCommand(Drivetrain drivetrain, GyroPIDCorrector corrector, IGamepad driver, IGamepad operator, Winch winch, BallSpitter ballSpitter) {
        super(drivetrain, corrector, driver, operator, winch, ballSpitter);
        this.gyro = corrector.getGyro();
    }

    @Override
    public void initialize() {
        gyro.zero();
    }
}
