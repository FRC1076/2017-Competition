package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.strongback.components.Accelerometer;

/**
 * This command takes a CancelableCommand and
 * and accelerometer axis and cancels said command if
 * the accelerometer exceeds a threshold. This command
 * should run in parallel with the other command, but does
 * not need to be started at the same time as the CancelableCommand.
 * 
 * This command ends upon cancelation of the other command or
 * if the other command ever returns false for its isRunning() call
 */
public class AccelerometerWatchdog extends Command {
    public double accelerometer_threshold = 1.0;
    Accelerometer accelerometer;
    CancelableCommand command;
    
    public AccelerometerWatchdog(Accelerometer accelerometer, CancelableCommand command, double threshold) {
        this.accelerometer = accelerometer;
        this.command = command;
        this.accelerometer_threshold = threshold;
    }
    
    public AccelerometerWatchdog(Accelerometer accelerometer, CancelableCommand command) {
        this(accelerometer, command, 1.0);
    }
    
    
    
    @Override
    public boolean execute() {
        if(command.isRunning() == false) {
            Strongback.logger().info("Command Not Running, exiting now");
            return true;
        }
        
        double acceleration = accelerometer.getAcceleration();
        if(acceleration > 0 && Math.abs(acceleration) > accelerometer_threshold) {
            Strongback.logger().info("Acceleration Hit Detected: " + acceleration);
            Strongback.logger().info("Canceled Command: " + command.getClass().getName());
            command.doCancel();
            return true;
        }
            
        return false;
    }

}
