package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.strongback.components.Accelerometer;
import org.strongback.components.ThreeAxisAccelerometer;

public class AccelerometerWatchdog extends Command {
    public double accelerometer_threshold = 1.0;
    // take a command
    // while that command runs
    // we check the accelerometer
    // if the accelerometer is big
    // kill the command
    // also if the subcommand ends, this command ends also
    Accelerometer accelerometer;
    CancelableCommand command;
    
    public AccelerometerWatchdog(Accelerometer accelerometer, CancelableCommand command) {
        this.accelerometer = accelerometer;
        this.command = command;
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
