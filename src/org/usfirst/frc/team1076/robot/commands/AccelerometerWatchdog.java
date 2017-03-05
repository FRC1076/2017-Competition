package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.components.Accelerometer;
import org.strongback.components.ThreeAxisAccelerometer;

public class AccelerometerWatchdog extends Command {

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
            return true;
        }
        
        if(accelerometer.getAcceleration() > 0 && Math.abs(accelerometer.getAcceleration()) > 10) {
            command.doCancel();
            return true;
        }
            
        return false;
    }

}
