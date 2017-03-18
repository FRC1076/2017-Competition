package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.strongback.components.Gyroscope;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.GyroPIDCorrector;

/**
 * TurnWithGyro takes a Strongback Gyroscope, a Drivetrain,
 * a speed, and a target angle in degrees and attempts to turn
 * to that angle as specified by the gyro. Note that positive angles turn *right*
 * and negative angles turn *left*.
 */
public class TurnWithGyro extends Command {
    public double easeOutThreshold = 1.0;
    public double finalSpeed = 0.0;
    double targetAngle;
    Gyroscope gyro;
    Drivetrain drivetrain;
    GyroPIDCorrector corrector;
    double initialSpeed;
    
    /**
     * Create a new TurnWithGyro 
     * @param gyro        a Strongback Gyroscope
     * @param drivetrain  a drivetrain
     * @param speed       speed from -1 to 1 (inclusive) to drive at
     * @param targetAngle angle, in degrees, to turn. Note that positive is right, while negative is left
     */
    public TurnWithGyro(Drivetrain drivetrain, GyroPIDCorrector corrector, double initialSpeed, double targetAngle) {
        super(drivetrain);
        this.drivetrain = drivetrain;
        this.corrector = corrector;
        this.gyro = corrector.getGyro();
        this.initialSpeed = initialSpeed;
        this.targetAngle = targetAngle;
    }

    @Override
    public void initialize() {
        Strongback.logger().info("BEGIN TurnWithGyro AUTO");
        gyro.zero();
    }

    @Override
    public boolean execute() {
        double progress = gyro.getAngle() / targetAngle;
        double speed = initialSpeed;
        if (progress > easeOutThreshold) {
            double slope = (initialSpeed - finalSpeed) / (1 - easeOutThreshold);
            speed = -slope * (progress - easeOutThreshold) + initialSpeed;
        }

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
        return Math.abs(targetAngle) <= Math.abs(gyro.getAngle());
    }

    @Override
    public void end() {
        Strongback.logger().info("Finished Turning, Goal: " + targetAngle + ", Actual: " + gyro.getAngle());
        Strongback.logger().info("END TurnWithGyro AUTO");
        drivetrain.stop();
    }
}
