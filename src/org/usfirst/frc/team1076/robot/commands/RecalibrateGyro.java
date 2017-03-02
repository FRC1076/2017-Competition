package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class RecalibrateGyro extends Command implements Sendable {
    Gyro gyro;
    Drivetrain drivetrain;
    
    public RecalibrateGyro(Gyro gyro, Drivetrain drivetrain) {
        this.gyro = gyro;
        this.drivetrain = drivetrain;
    }
    
    @Override
    protected void execute() {
    	Strongback.logger().info("Recalibrating the gyro. Please don't move the robot!");
        drivetrain.disable();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        gyro.calibrate();
        gyro.reset();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        drivetrain.enable();
        Strongback.logger().info("Recalibration complete!");
    }
    
    @Override
    protected boolean isFinished() {
        return true;
    }

}
