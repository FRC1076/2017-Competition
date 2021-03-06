package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Gyroscope;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;

/**
 * ForwardWithGyro attempts to go straight at the specified speed.
 * It uses the gyroscope's angle as a correction mechanism, slowing
 * one of the motors in order to keep straight.
 */
public class ForwardWithGyro extends Command {
    Gyroscope gyro;
    Drivetrain drivetrain;
    double speed;
    
    /**
     * Create a new ForwardWithGyro
     * @param gyro        a Strongback Gyroscope
     * @param drivetrain  a drivetrain
     * @param speed       speed from -1 to 1 (inclusive) to drive at
     * @param targetTime  time, in seconds, to drive forward
     */
    public ForwardWithGyro(Gyroscope gyro, Drivetrain drivetrain, double speed, double targetTime) {
        super(targetTime, drivetrain); // This command automatically times out after the specified time. 
        this.gyro = gyro;
        this.drivetrain = drivetrain;
        this.speed = speed;
    }
    
    @Override
    public void initialize() {
        gyro.zero();
    }
    
    /**
     * Edge case note: If the speed is less than the normed gyro value,
     * then the speed reduction for one of the motors will actually be a speed increase.
     * This is due to the fact that Math.abs(speed) - Math.abs(gyroNorm) > Math.abs(gyroNorm) - Math.abs(speed)
     * Fortunately, this should not happen when passing reasonably large values.
     */
    @Override
    public boolean execute() {
        drivetrain.arcade(speed, 0);
        
        return false; // This command only stops after timing out
    }
    
    @Override
    public void end() {
        drivetrain.stop();
    }
}
