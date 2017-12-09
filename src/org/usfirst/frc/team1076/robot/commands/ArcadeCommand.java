package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.strongback.components.Gyroscope;
import org.usfirst.frc.team1076.robot.subsystems.ArcadeNoCorrector;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.GyroPIDCorrector;

/**
 * ForwardWithGyro attempts to go straight at the specified speed.
 * It uses the gyroscope's angle as a correction mechanism, slowing
 * one of the motors in order to keep straight.
 */
public class ArcadeCommand extends CancelableCommand {
    Drivetrain drivetrain;
    double forward;
    double rotate;
    
    /**
     * Create a new ForwardWithGyro
     * @param gyro        a Strongback Gyroscope
     * @param drivetrain  a drivetrain
     * @param speed       speed from -1 to 1 (inclusive) to drive at
     * @param targetTime  time, in seconds, to drive forward
     */
    public ArcadeCommand(Drivetrain drivetrain, double forward, double rotate, double targetTime) {
        super(targetTime, drivetrain); // This command automatically times out after the specified time. 
        this.drivetrain = drivetrain;
        this.forward = forward;
        this.rotate = rotate;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        Strongback.logger().info("BEGIN ArcadeCommand AUTO");
    }
    
    /**
     * Edge case note: If the speed is less than the normed gyro value,
     * then the speed reduction for one of the motors will actually be a speed increase.
     * This is due to the fact that Math.abs(speed) - Math.abs(gyroNorm) > Math.abs(gyroNorm) - Math.abs(speed)
     * Fortunately, this should not happen when passing reasonably large values.
     */
    @Override
    public boolean execute() {
        drivetrain.arcade(forward, rotate, new ArcadeNoCorrector());
       return !isRunning;
    }
    
    @Override
    public void end() {
        Strongback.logger().info("END ArcadeCommand AUTO");
        drivetrain.stop();
    }
    
    @Override
    public void doCancel() {
        isRunning = false;
    }
}
