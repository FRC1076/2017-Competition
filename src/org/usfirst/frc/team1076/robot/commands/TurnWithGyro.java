package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Gyroscope;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;

public class TurnWithGyro extends Command {
    Gyroscope gyro;
    Drivetrain drivetrain;
    double speed;
    double targetAngle;
    
    public TurnWithGyro(Gyroscope gyro, Drivetrain drivetrain, double speed, double targetAngle) {
        super(drivetrain);
        this.gyro = gyro;
        this.drivetrain = drivetrain;
        this.speed = speed;
        this.targetAngle = targetAngle;
    }

    @Override
    public void initialize() {
        gyro.zero();
    }

    @Override
    public boolean execute() {
        // If turning right
        if (targetAngle > 0) {
            drivetrain.setLeftSpeed(speed);
            drivetrain.setRightSpeed(-speed);
        } else { // Else if turning left
            drivetrain.setLeftSpeed(-speed);
            drivetrain.setRightSpeed(speed);        
        }
        return isFinished();
    }
    
    public boolean isFinished() {
        return targetAngle - gyro.getAngle() <= 0;        
    }

}
